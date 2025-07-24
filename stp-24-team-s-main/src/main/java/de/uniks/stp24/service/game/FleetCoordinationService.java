package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.GameFleetController;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.controllers.helper.DistancePoint;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.utils.PathEntry;
import de.uniks.stp24.utils.PathTableEntry;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.util.Duration;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;

import static de.uniks.stp24.model.Fleets.Fleet;
import static de.uniks.stp24.service.Constants.*;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * A service that coordinates fleets position on the map and their traveling paths.
 * Contains methods for path generation and saving, as well as animation processing.
 */
@Singleton
public class FleetCoordinationService {
    @Inject
    public TimerService timerService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public FleetService fleetService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public App app;
    @Inject
    public ContactsService contactsService;
    @Inject
    public JobsService jobsService;
    @Inject
    public ImageCache imageCache;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ShipService shipService;
    public final Random random = new Random();
    private GameFleetController selectedFleet;
    private ObservableList<Node> mapGrid;
    private final List<PathEntry> pathEntries = new ArrayList<>();
    private final Map<GameFleetController, List<DistancePoint>> coordinatedPaths = new HashMap<>();
    private ObservableList<Job> travelJobs = FXCollections.observableArrayList();
    private final List<Consumer<Fleet>> onFleetSelectedConsumers = new ArrayList<>();
    private final List<GameFleetController> enemyFleets = new ArrayList<>();
    final Map<String, GameFleetController> mapOfFleetComponents = new HashMap<>();

    private final int ROTATE_DURATION = 2;
    public InGameController inGameController;

    @Inject
    public FleetCoordinationService() {
    }

    public void setJobFinishers() {
        this.random.setSeed(Integer.parseInt(this.tokenStorage.getGameId().substring(0, 4), 16));

        this.jobsService.onJobsLoadingFinished(() -> {
            this.travelJobs = this.jobsService.getJobObservableListOfType("travel");
            this.travelJobs.forEach(job -> {
                this.setOnJobDeletion(job);
                this.setOnJobCompletion(job);
            });
        });

        this.fleetService.onLoadingFinished(() -> this.fleetService.getGameFleets().forEach(this::putFleetOnMap));

        this.fleetService.onFleetCreated(this::putFleetOnMap);
        this.fleetService.onFleetDestroyed(this::deleteFleetFromMap);
        this.fleetService.onFleetLocationChanged(this::processTravelForEnemyFleets);
        this.shipService.aliveFleets.clear();
    }

    private void setOnJobDeletion(Job job) {
        this.jobsService.onJobDeletion(job._id(), () ->
            this.getTravelingFleet(job.fleet())
                    .map(gameFleet -> {
                        this.coordinatedPaths.remove(gameFleet);
                        gameFleet.setFleet(this.fleetService.getFleet(job.fleet()));
                        this.processReturn(gameFleet);
                        return gameFleet;
                    }).orElseThrow()
        );
    }

    private void setOnJobCompletion(Job job) {
        this.jobsService.onJobCompletion(job._id(), () ->
           this.getTravelingFleet(job.fleet())
                    .map(gameFleet -> {
                        this.coordinatedPaths.remove(gameFleet);
                        gameFleet.setFleet(this.fleetService.getFleet(job.fleet()));
                        this.processFinish(gameFleet, this.islandsService.getIslandComponent(job.path().getLast()));
                        return gameFleet;
                    }).orElseThrow()
        );
    }

    private Optional<GameFleetController> getTravelingFleet(String fleetID) {
        return  this.coordinatedPaths.keySet().stream()
                .filter(gameFleet -> gameFleet.getFleet()._id().equals(fleetID))
                .findFirst();

    }

    public void setFleet(GameFleetController fleet) {
        if (Objects.nonNull(this.selectedFleet)) {
            this.selectedFleet.toggleActive();
            if (this.selectedFleet.equals(fleet)) this.selectedFleet = null;
            else {
                this.selectedFleet = fleet;
                fleet.toggleActive();
            }
        } else {
            this.selectedFleet = fleet;
            fleet.toggleActive();
            if (!fleet.getFleet().size().isEmpty()) {
            System.out.println("your fleet has these ships: " + fleet.getFleet().size() );
            fleet.refreshListOfShips();
            }
        }
        this.onFleetSelectedConsumers.forEach(func -> func.accept(this.getSelectedFleet()));
    }

    private void deleteFleetFromMap(Fleet fleet) {
        var comp = shipService.mapOfFleetComponents.remove(fleet._id());
        shipService.aliveFleets.remove(comp);
        fleetService.updateFleets.remove(comp);
        fleetService.mapOfFleetComponents.remove(fleet._id());
        if (Objects.nonNull(this.selectedFleet) && fleet.equals(this.selectedFleet.getFleet()))
            this.selectedFleet = null;

        this.mapGrid.removeIf(node -> {
            if (node instanceof GameFleetController fleetController)
                return (fleetController.getFleet().equals(fleet));
            return false;
        });
    }

    public void putFleetOnMap(Fleet fleet) {
        this.travelJobs.stream()
                .filter(job -> job.fleet().equals(fleet._id()))
                // If fleet had a travel job, put fleet on the travel progress location and continue the travel
                .findFirst().map(job -> this.startTravelAfterRejoin(fleet, job))
                // Else put the fleet near the island its located on
                .orElseGet(() -> this.putFleetNearIsland(fleet));
    }

    private GameFleetController createFleetInstance(Fleet fleet) {
        var gameFleet = this.app.initAndRender(new GameFleetController(fleet,this));

        if (Objects.nonNull(fleet.empire())) {
            gameFleet.renderWithColor(this.islandsService.getEmpire(fleet.empire()).color());
            if (!fleet.empire().equals(this.tokenStorage.getEmpireId())) this.enemyFleets.add(gameFleet);
        } else gameFleet.renderWithColor("white");

        this.mapGrid.add(gameFleet);
        return gameFleet;
    }

    private Job putFleetNearIsland(Fleet fleet) {
        GameFleetController gameFleet = this.createFleetInstance(fleet);
        IslandComponent island = this.islandsService.getIslandComponent(fleet.location());
        DistancePoint parkingPoint = this.findParkingPoint(new DistancePoint(island, null));
        gameFleet.setLayoutX(parkingPoint.getX());
        gameFleet.setLayoutY(parkingPoint.getY());
        gameFleet.setStartingPoint();
        gameFleet.collisionCircle.setRadius(Constants.FLEET_COLLISION_RADIUS);
        mapOfFleetComponents.put(fleet._id(), gameFleet);
        shipService.addFleetComponent(fleet._id(), gameFleet);
        fleetService.mapOfFleetComponents.put(fleet._id(), gameFleet);
        return null;
    }

    private Job startTravelAfterRejoin(Fleet fleet, Job travelJob) {
        GameFleetController gameFleet = this.createFleetInstance(fleet);
        this.generateTravelPaths(travelJob.path().getFirst(), travelJob.path().getLast(), fleet);
        PathEntry entry = this.getPathEntry(travelJob.path().getFirst(), travelJob.path().getLast());
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), dtos -> {
            int speed = this.shipService.getFleetSpeed(dtos);
            List<DistancePoint> distancePoints = this.createCoordinatedPath(entry, gameFleet,
                    travelJob.path().getFirst(), speed, (int) travelJob.total());
            for (int i = 0; i < travelJob.progress()-1; i++) distancePoints.removeFirst();
            DistancePoint location = distancePoints.removeFirst();

            gameFleet.setCurrentPoint(distancePoints.removeFirst());
            gameFleet.setLayoutX(location.getX());
            gameFleet.setLayoutY(location.getY());
            gameFleet.fleetImage.setRotate(this.getDirectionalAngle(
                    gameFleet.getCurrentPoint().getPrev(),
                    gameFleet.getCurrentPoint()));
            this.processSpeedChanged(gameFleet);
            fleetService.mapOfFleetComponents.put(fleet._id(), gameFleet);
        }, Throwable::printStackTrace);
        return travelJob;
    }

    private DistancePoint findParkingPoint(DistancePoint islandPoint) {
        double angle = (random.nextInt(360)-90)*Math.PI/180;
        return new DistancePoint(
                islandPoint.getX() + (FLEET_FROM_ISLAND_DISTANCE)*Math.cos(angle),
                islandPoint.getY() + ( FLEET_FROM_ISLAND_DISTANCE)*Math.sin(angle),
                POINT_TYPE.ISLAND,
                islandPoint.islandComponent,
                islandPoint.getPrev()
        );
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
        this.mapGrid = inGameController.mapGrid.getChildren();
        this.timerService.onGameTicked(this::processTravel);
        this.timerService.onSpeedChanged(this::processSpeedChanged);
        this.timerService.onGameTicked(() -> islandsService.refreshListOfColonizedSystems());
         this.timerService.onGameTicked(() -> fleetService.updateTheseFleetsHealth());
    }

    public Fleet getSelectedFleet() {
        if (Objects.nonNull(this.selectedFleet)) {
            return this.selectedFleet.getFleet();
        }
        return null;
    }

    public GameFleetController getSelectedFleetInstance() {
        if (Objects.nonNull(this.selectedFleet)) {
            return this.selectedFleet;
        }
        return null;
    }

    private void processSpeedChanged() {
        this.coordinatedPaths.keySet().forEach(this::processSpeedChanged);
    }

    private void processSpeedChanged(GameFleetController fleet) {
        if (this.timerService.getServerSpeed() == 0) fleet.stopTravel();
        else fleet.travelToPoint(this.createSpeedChangedKeyframe(fleet), fleet.getCurrentPoint(), true);
    }

    private void processTravel() {
        this.coordinatedPaths.forEach((fleet, points) -> {
            if (!points.isEmpty())
                fleet.travelToPoint(this.createTravelKeyFrames(fleet, points.getFirst(), 60), points.removeFirst(), true);
        });
    }

    private void processTravel(GameFleetController fleet) {
        fleet.travelToPoint(this.createTravelKeyFrames(fleet, this.coordinatedPaths.get(fleet).getFirst(), 60),
                this.coordinatedPaths.get(fleet).removeFirst(), true);
    }

    private void processFinish(GameFleetController fleet, IslandComponent finishIsland) {
        DistancePoint endPoint = this.findParkingPoint(new DistancePoint(finishIsland, fleet.getCurrentPoint()));
        fleet.travelToPoint(this.createTravelKeyFrames(fleet, endPoint, 60), endPoint, true);
    }

    private void processReturn(GameFleetController fleet) {
        DistancePoint prevIslandPoint;
        if (Objects.isNull(fleet.getCurrentPoint().getPrev())) {
            prevIslandPoint = fleet.getCurrentPoint();
        } else {
            prevIslandPoint = fleet.getCurrentPoint().getPrev();
            while (Objects.nonNull(prevIslandPoint.getPrev()) && !prevIslandPoint.getType().equals(POINT_TYPE.ISLAND))
                prevIslandPoint = prevIslandPoint.getPrev();
        }
        fleet.travelToPoint(this.createReturnKeyFrames(fleet, prevIslandPoint), prevIslandPoint, true);
    }

    private void processTravelForEnemyFleets(Fleet fleet) {
        if (!fleet.empire().equals(this.tokenStorage.getEmpireId())) {
            this.enemyFleets.stream().filter(gameFleet -> gameFleet.getFleet().equals(fleet))
                    .findFirst().map(gameFleet -> {
                        DistancePoint destination = this.findParkingPoint(new DistancePoint(
                                this.islandsService.getIslandComponent(fleet.location()),
                                gameFleet.getCurrentLocation()));
                        gameFleet.travelToPoint(this.createTravelKeyFrames(gameFleet, destination, 24), destination, false);
                        gameFleet.setFleet(fleet);
                        return gameFleet;
                    }).orElseThrow();
        }
    }

    private double getDirectionalAngle(DistancePoint prevPoint, DistancePoint nextPoint) {
        Point2D delta = nextPoint.subtract(prevPoint);
        return Math.atan2(delta.getY(), delta.getX())*180/Math.PI+90;
    }

    private List<KeyFrame> createTravelKeyFrames(GameFleetController fleet, DistancePoint nextPoint, double speed) {
        return List.of(
                new KeyFrame(Duration.seconds(ROTATE_DURATION),
                        new KeyValue(fleet.fleetImage.rotateProperty(), getDirectionalAngle(nextPoint.getPrev(), nextPoint), Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getPrev().getX()-FLEET_HW, Interpolator.EASE_BOTH),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getPrev().getY()-FLEET_HW, Interpolator.EASE_BOTH)),
                new KeyFrame(Duration.seconds((speed/ (double) this.timerService.getServerSpeed())-ROTATE_DURATION),
                        new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                        new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR))
        );

    }

    private List<KeyFrame> createSpeedChangedKeyframe(GameFleetController fleet) {
        DistancePoint nextPoint       = fleet.getCurrentPoint(),
                      currentLocation = fleet.getCurrentLocation();
        double distance         = Math.abs(nextPoint.getPrev().distance(nextPoint)),
               traveledDistance = Math.abs(currentLocation.distance(nextPoint)),
               timeDiff         = 60*(traveledDistance/distance);

        return List.of(
                new KeyFrame(Duration.seconds(timeDiff/this.timerService.getServerSpeed() + ROTATE_DURATION),
                new KeyValue(fleet.layoutXProperty(), nextPoint.getX()-FLEET_HW, Interpolator.LINEAR),
                new KeyValue(fleet.layoutYProperty(), nextPoint.getY()-FLEET_HW, Interpolator.LINEAR))
        );
    }

    private List<KeyFrame> createReturnKeyFrames(GameFleetController fleet, DistancePoint returnPoint) {
        DistancePoint parkingPoint = this.findParkingPoint(returnPoint);
        return List.of(
                new KeyFrame(Duration.seconds(ROTATE_DURATION),
                        new KeyValue(fleet.fleetImage.rotateProperty(), getDirectionalAngle(
                                fleet.getCurrentLocation(), parkingPoint), Interpolator.EASE_BOTH)),

                new KeyFrame(Duration.seconds(4),
                        new KeyValue(fleet.layoutXProperty(), parkingPoint.getX(), Interpolator.LINEAR),
                        new KeyValue(fleet.layoutYProperty(), parkingPoint.getY(), Interpolator.LINEAR)));
    }

    public void travelToIsland(String destinationIslandID, GameFleetController gameFleet) {
        if (Objects.isNull(gameFleet)) return;
        Fleet fleet = gameFleet.getFleet();

        // The path entry should be generated at this point!
        PathEntry entry = this.getPathEntry(fleet.location(), destinationIslandID);
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), dtos -> {
            int speed = this.shipService.getFleetSpeed(dtos);
            this.createCoordinatedPath(entry, gameFleet, fleet.location(), speed, -1);

            this.subscriber.subscribe(this.fleetService.beginTravelJob(entry.getPathFromLocation(
                            fleet.location()), fleet._id()), job -> {
                        this.processTravel(gameFleet);
                        this.setOnJobDeletion(job);
                        this.setOnJobCompletion(job);
                    }, error -> System.out.println("Caught an exception while trying to create a new travel job in the" +
                            "FleetCoordinationService:\n" + error.getMessage()));
        }, Throwable::printStackTrace);
    }

    private List<DistancePoint> createCoordinatedPath(PathEntry pathEntry, GameFleetController gameFleet,
                                                      String startingLocation, int speed, int travelDuration) {
        ArrayList<String> path = pathEntry.getPathFromLocation(startingLocation);
        List<DistancePoint> coordinatedPath = new ArrayList<>();
        IslandComponent island = this.islandsService.getIslandComponent(path.getFirst());

        // Get the needed total number of points that need to be put between the islands
        byte interPoints;
        if (travelDuration == -1) interPoints = (byte) (this.getTravelDuration(pathEntry, speed) - (path.size() - 1));
        else interPoints = (byte) (travelDuration - (path.size()-1));
        // Get the number of points that should be put between two islands
        byte pointAlloc  = (byte) (interPoints/(path.size()-1));

        coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, null)));

        // Put the intermediate points first, then the island location
        for (int i = 0; i < path.size()-2; i++) {
            // Intermediate points
            this.islandsService.generateDistancePoints(path.get(i), path.get(i+1), pointAlloc)
                    .forEach(point -> coordinatedPath.add(new DistancePoint(point.getX(), point.getY(),
                            POINT_TYPE.INTER, coordinatedPath.getLast())));

            // Island location
            island = this.islandsService.getIslandComponent(path.get(i+1));
            coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, coordinatedPath.getLast())));
        }

        // Fill the remaining path with points that were not allocated
        this.islandsService.generateDistancePoints(path.get(path.size()-2), path.getLast(), interPoints-pointAlloc*(path.size()-2))
                .forEach(point -> coordinatedPath.add(new DistancePoint(point.getX(), point.getY(),
                        POINT_TYPE.INTER, coordinatedPath.getLast())));

        island = this.islandsService.getIslandComponent(path.getLast());
        coordinatedPath.add(this.findParkingPoint(new DistancePoint(island, coordinatedPath.getLast())));

        coordinatedPath.removeFirst();
        this.coordinatedPaths.put(gameFleet, coordinatedPath);
        return coordinatedPath;
    }

    public void generateTravelPaths(String startLocation, String endLocation, Fleet fleet) {
        // Find the shortest path using Dijkstra's algorithm
        if (Objects.isNull(fleet)) return;

        // Return the travel path if it already was computed
        PathEntry calculatedPath = this.getPathEntry(startLocation, endLocation);
        if (Objects.nonNull(calculatedPath)) return;

        List<String> islandIDs = this.islandsService.getListOfIslands().stream().map(Island::id).toList();
        List<String> visitedIslands = new ArrayList<>();
        Map<String, PathTableEntry> tableEntries = islandIDs.stream()
                .collect(toMap(identity(), id -> new PathTableEntry(id, this.islandsService.getConnections(id))));

        PathTableEntry currentIsland = tableEntries.get(startLocation);

        while (visitedIslands.size() != islandIDs.size()) {
            // Calculate the new shortest path for the connected islands
            if (Objects.nonNull(currentIsland)) {
                String currentIslandID = currentIsland.getID();
                int currentShortestPath = currentIsland.getShortestPath();
                currentIsland.getConnections().forEach((id, dist) -> {
                    // Do nothing if the neighbor was already checked
                    if (visitedIslands.contains(id)) return;

                    PathTableEntry connectedIsland = tableEntries.get(id);
                    int prevDist = connectedIsland.getShortestPath();
                    // Do nothing if the neighbor's path is shorter than the new one
                    if (prevDist != -1 && prevDist < dist + currentShortestPath) return;

                    connectedIsland.setShortestPath(currentShortestPath + dist);
                    connectedIsland.setPreviousNode(currentIslandID);
                });
                visitedIslands.add(currentIsland.getID());

                // Stop iterating if the island of interest was already found
                if (currentIslandID.equals(endLocation)) break;
            }

            // Get entry with the shortest path
            currentIsland = tableEntries.values().stream()
                    // Take only islands with a defined path
                    .filter(entry -> entry.getShortestPath() != -1)
                    // Take only islands that weren't visited
                    .filter(entry -> !visitedIslands.contains(entry.getID()))
                    // Considering previous statements, take island with the shortest path
                    .min(Comparator.comparing(PathTableEntry::getShortestPath)).orElse(null);
        }

        // Reconstruct the paths for all traversed islands and save them for future use
        visitedIslands.removeFirst();
        visitedIslands.forEach(id -> {
            ArrayList<String> path = new ArrayList<>();
            PathTableEntry tableEntry = tableEntries.get(id);
            int distance = tableEntry.getShortestPath();
            while (Objects.nonNull(tableEntry)) {
                path.add(tableEntry.getID());
                tableEntry = tableEntries.get(tableEntry.getPreviousNode());
            }
            PathEntry entry = new PathEntry(path, distance);
            if (!this.pathEntries.contains(entry)) this.pathEntries.add(entry);
        });
    }

    /**
     * Returns the PathEntry, containing the shortest path between two islands counting them.
     * The order in which the island IDs are provided doesn't matter. <p>
     * The traveling path will be generated automatically by pressing on an island with selected fleet.
     * @param startIslandID ID of the first island
     * @param endIslandID ID of the second island
     * @return PathEntry with the shortest path between two islands
     */
    public PathEntry getPathEntry(String startIslandID, String endIslandID) {
        return this.pathEntries.stream().filter(entry -> entry.equals(startIslandID, endIslandID))
                .findFirst().orElse(null);
    }

    /**
     * Returns the travel duration between two islands, calculated from the travel distance and the speed of
     * the slowest ship in the fleet. The order in which the island IDs are provided doesn't matter.
     * @param startIslandID ID of the first island
     * @param endIslandID ID of the second island
     * @return Rounded number of the seasons needed for this travel
     */
    public int getTravelDuration(String startIslandID, String endIslandID, int speed) {
        return this.getTravelDuration(this.getPathEntry(startIslandID, endIslandID), speed);
    }

    public int getTravelDuration(PathEntry entry, int speed) {
        return (int) Math.ceil((double) entry.getDistance()/speed);
    }

    public void onFleetSelected(Consumer<Fleet> func) {
        this.onFleetSelectedConsumers.add(func);
    }

    public void dispose() {
        this.selectedFleet = null;
        this.subscriber.dispose();
        this.coordinatedPaths.clear();
        this.enemyFleets.clear();
        this.pathEntries.clear();
        this.onFleetSelectedConsumers.clear();
        this.mapOfFleetComponents.clear();
    }

    public void monitorFleetCollisions(IslandComponent islandComponent, GameFleetController gameFleetController) {
        if (islandComponent.isCollided(gameFleetController.getLayoutX(), gameFleetController.getLayoutY(), Constants.FLEET_COLLISION_RADIUS)) {
            if(Objects.nonNull(islandComponent.getIsland().owner())){
                if (!islandComponent.getIsland().owner().equals(gameFleetController.getFleet().empire()) && !islandComponent.getIsland().owner().equals(this.tokenStorage.getEmpireId())){
                    islandsService.refreshListOfColonizedSystems();
                    contactsService.addEnemy(islandComponent.getIsland().owner(), islandComponent.getIsland().id());
                }
            }
        }
    }
}
