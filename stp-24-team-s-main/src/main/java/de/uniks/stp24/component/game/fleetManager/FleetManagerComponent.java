package de.uniks.stp24.component.game.fleetManager;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Ships.BlueprintInFleetDto;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.ShipType;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.model.Jobs.Job;

@Component(view = "FleetManager.fxml")
public class FleetManagerComponent extends AnchorPane {
    @FXML
    public VBox fleetsOverviewVBox;
    @FXML
    public ListView<Fleet> fleetsListView;
    @FXML
    public ListView<BlueprintInFleetDto> blueprintInFleetListView;
    @FXML
    public VBox fleetBuilderVBox;
    @FXML
    public Label fleetNameText;
    @FXML
    public ImageView shipImageView;
    @FXML
    public Label commandLimitLabel;
    @FXML
    public Label islandLabel;
    @FXML
    public Button shipsButton;
    @FXML
    public Button blueprintButton;
    @FXML
    public VBox blueprintsVBox;
    @FXML
    public ListView<ShipType> blueprintsListView;
    @FXML
    public VBox shipsVBox;
    @FXML
    public ListView<ReadShipDTO> shipsListView;
    @FXML
    public VBox infoButtonVBox;
    @FXML
    public StackPane fleetManagerStackPane;
    @FXML
    public Button closeFleetManagerButton;
    @FXML
    public Button createFleetButton;
    @FXML
    public Button showFleetsButton;
    @FXML
    public Label buildShipErrorLabel;
    @FXML
    public Label shipLabel;


    @Inject
    public App app;
    @Inject
    public FleetService fleetService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public JobsService jobsService;
    @Inject
    public VariableService variableService;
    @Inject
    public ShipService shipService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ResourcesService resourcesService;
    @Inject
    public IslandsService islandsService;

    @SubComponent
    @Inject
    public NewFleetComponent newFleetComponent;
    @SubComponent
    @Inject
    public ChangeFleetComponent changeFleetComponent;
    @SubComponent
    @Inject
    public BlueprintsDetailsComponent blueprintsDetailsComponent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public ImageCache imageCache = new ImageCache();

    private Fleet editedFleet;
    private FadeTransition transition;

    public Provider<FleetComponent> fleetComponentProvider = () -> new FleetComponent(this, this.tokenStorage, this.subscriber, this.fleetService);
    public Provider<ShipTypesOfFleetComponent> shipTypesOfFleetComponentProvider = () -> new ShipTypesOfFleetComponent(this, this.resourcesService, this.shipService, this.subscriber, this.fleetService, this.gameResourceBundle);
    public Provider<BlueprintsComponent> blueprintsAddableComponentProvider = () -> new BlueprintsComponent(this, true, this.blueprintsDetailsComponent, gameResourceBundle);
    public Provider<BlueprintsComponent> blueprintsNotAddableComponentProvider = () -> new BlueprintsComponent(this, false, this.blueprintsDetailsComponent, gameResourceBundle);
    public Provider<ShipComponent> shipComponentProvider = () -> new ShipComponent(this, this.subscriber, this.shipService, this.fleetService, this.imageCache, gameResourceBundle);

    public ObservableList<BlueprintInFleetDto> blueprintsInFleetList = FXCollections.observableArrayList();
    public ObservableList<Fleet> fleets = FXCollections.observableArrayList();
    public ObservableList<ShipType> blueprints = FXCollections.observableArrayList();
    public ObservableList<ReadShipDTO> ships = FXCollections.observableArrayList();

    @Inject
    public FleetManagerComponent() {
    }

    @OnInit
    public void init(){
        this.jobsService.onJobsLoadingFinished("ship", this::setShipFinisher);
        this.newFleetComponent.setFleetManager(this);
        this.changeFleetComponent.setFleetManager(this);
        this.fleetService.onFleetDestroyed(fleet -> {
            if (Objects.nonNull(this.editedFleet) && this.editedFleet.equals(fleet)) this.showFleets();
        });
    }

    @OnRender
    public void render() {

        blueprintInFleetListView.setSelectionModel(null);
        fleetsListView.setSelectionModel(null);
        blueprintsListView.setSelectionModel(null);
        shipsListView.setSelectionModel(null);

        this.fleets = this.fleetService.getEmpireFleets(this.tokenStorage.getEmpireId());
        this.ships = this.shipService.getShipsInSelectedFleet();
        this.blueprintsInFleetList = this.shipService.getBlueprintsInSelectedFleet();
        FilteredList<ShipType> filteredBlueprints = new FilteredList<>(blueprints, shipType -> shipType.build_time() > 0);

        this.fleetsListView.setItems(fleets);
        this.fleetsListView.setCellFactory(list -> new ComponentListCell<>(app, fleetComponentProvider));
        this.blueprintInFleetListView.setItems(blueprintsInFleetList);
        this.blueprintInFleetListView.setCellFactory(list -> new ComponentListCell<>(app, shipTypesOfFleetComponentProvider));
        this.blueprintsListView.setItems(filteredBlueprints);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));
        this.shipsListView.setItems(ships);
        this.shipsListView.setCellFactory(list -> new ComponentListCell<>(app, shipComponentProvider));

        this.fleetManagerStackPane.getChildren().add(this.newFleetComponent);
        this.fleetManagerStackPane.getChildren().add(this.changeFleetComponent);
        this.newFleetComponent.setVisible(false);
        this.changeFleetComponent.setVisible(false);

        this.transition = new FadeTransition(Duration.seconds(5), this.buildShipErrorLabel);
    }

    public void showFleets() {
        showBlueprints();
        this.fleetsListView.refresh();
        this.shipService.clearEditedFleetInfos();
        shipImageView.setImage(imageCache.get("icons/ships/ship_Image_With_Frame1.png"));

        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));

        commandLimitLabel.setVisible(false);
        islandLabel.setVisible(false);
        blueprintButton.setVisible(false);
        shipsButton.setVisible(false);
        createFleetButton.setVisible(true);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));


        this.fleetNameText.setText("Fleets");
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsNotAddableComponentProvider));

        this.fleetsListView.setVisible(true);
        this.fleetBuilderVBox.setVisible(false);
    }

    public void showBlueprints() {
        this.shipLabel.setText("Blueprints");
        this.blueprints.clear();
        this.blueprints.addAll(shipService.shipTypesAttributes);

        this.shipsVBox.setVisible(false);
        this.blueprintsListView.setVisible(true);
    }

    public void showShips() {
        this.shipLabel.setText("Ships");
        this.shipsVBox.setVisible(true);
        this.blueprintsListView.setVisible(false);
    }

    public void close() {
        this.shipService.clearEditedFleetInfos();
        this.setVisible(false);
    }

    /**
     * Load all ships of the fleet and set editedFleet, the fleet name, the island name and the command limit
     * Initialize the fleet edition in the ship Service (see description there)
     * @param fleet: currently edited fleet
     */
    public void editSelectedFleet(Fleet fleet) {
        this.buildShipErrorLabel.setVisible(false);
        this.blueprintsListView.setCellFactory(list -> new ComponentListCell<>(app, blueprintsAddableComponentProvider));
        this.editedFleet = fleet;
        this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                dto -> {
                    createFleetButton.setVisible(false);

                    this.shipService.initializeFleetEdition(dto, editedFleet);
                    showBlueprints();
                    setIslandName(false);
                    setCommandLimit(fleet,false);
                    shipImageView.setImage(imageCache.get("icons/ships/ship_Image_With_Frame2.png"));

                    commandLimitLabel.setVisible(true);
                    islandLabel.setVisible(true);
                    blueprintButton.setVisible(true);
                    shipsButton.setVisible(true);

                    this.fleetNameText.setText(fleet.name());
                    this.fleetsListView.setVisible(false);
                    this.fleetBuilderVBox.setVisible(true);
                }, Throwable::printStackTrace);
    }

    /**
     * If a blueprint is not already contained in a fleet, it will be added with one planned ship of this type
     * @param shipType: type of the blueprint
     */
    public void addBlueprintToFleet(ShipType shipType) {
        if ((!this.editedFleet.size().containsKey(shipType._id())) || (this.editedFleet.size().get(shipType._id()) == 0)){
            this.subscriber.subscribe(this.fleetService.editSizeOfFleet(shipType._id(), 1, editedFleet),
                    dto -> {
                        this.shipService.addBlueprintToFleet(new BlueprintInFleetDto(shipType._id(), 0, this.editedFleet));
                        this.setCommandLimit(dto,false);
                    }, error -> System.out.println("Error while adding a Blueprint to a FleetManagerComponent:\n" + error.getMessage()));
        }
    }

    /**
     * @param fleet: currently edited fleet
     * @param shipDeleted: to avoid timing issues when a ship was deleted - the eventListener in shipService is sometimes slower than the call of this method
     */
    public void setCommandLimit(Fleet fleet, boolean shipDeleted) {
        int numberOfShips = ships.size();
        if(shipDeleted && ships.size() == fleet.ships()) {
            numberOfShips = ships.size() - 1;
        }
        this.commandLimitLabel.setText(this.gameResourceBundle.getString("command.limit") + " \n" + numberOfShips + "/"
                + fleet.size().values().stream().mapToInt(Integer::intValue).sum());
    }

    /**
     * Checks if the current island of the edited fleet belongs to the empire and in this case counts the number of shipyards on this island.
     * @param shipJobStarted: to avoid timing issues when a ship job has been started - eventListener in JobService is sometimes slower than the call of this method
     */
    public void setIslandName(boolean shipJobStarted) {
        List<Island> islands = islandsService.getIsles().stream().filter(island -> island.id().equals(this.editedFleet.location())).toList();
        if(islands.isEmpty() || islands.getFirst().owner() == null){
            this.islandLabel.setText(this.gameResourceBundle.getString("unknown.seas"));
        } else if (!islands.getFirst().owner().equals(this.tokenStorage.getEmpireId())) {
            this.islandLabel.setText(islands.getFirst().name() + "\n" + this.gameResourceBundle.getString("not.your.island"));
        } else {
            int numberOfShipyards = islands.getFirst().buildings().stream().filter("shipyard"::equals).toList().size();
            int numberOfShipJobs = this.jobsService.getObservableListForSystem(this.editedFleet.location())
                    .filtered(job -> job.type().equals("ship")).size();
            if(shipJobStarted) numberOfShipJobs += 1;
            this.islandLabel.setText(islands.getFirst().name() + "\n" + numberOfShipJobs + " / " + numberOfShipyards + " " + this.gameResourceBundle.getString("shipyards.occupied"));
        }
    }

    /**
     * Defines the events when a ship job was finished or deleted
     * @param job: ship building job
     */
    public void setShipFinisher(Job job){
        this.jobsService.onJobCompletion(job._id(), ()  -> {
            setCommandLimit(this.fleetService.getFleet(editedFleet._id()), false);
            setIslandName(false);
            this.blueprintInFleetListView.refresh();
        });
        this.jobsService.onJobDeletion(job._id(), ()  -> {
            if(this.isVisible())  setIslandName(false);
        });
    }

    public void createFleet() {
        this.newFleetComponent.createNewFleet();
    }

    /**
     * Depending on the reason why a ship can't be built the errorLabel is set. It disappears after 5s.
     * @param error: reason why a ship can't be built
     */
    public void setErrorLabel(String error){
        this.transition.stop();
        this.buildShipErrorLabel.setVisible(true);
        this.transition.setFromValue(1);
        this.transition.setToValue(0);
        this.transition.play();
        switch (error) {
            case "resources" -> this.buildShipErrorLabel.setText(this.gameResourceBundle.getString("buildShipError.resources"));
            case "shipyard" -> this.buildShipErrorLabel.setText(this.gameResourceBundle.getString("buildShipError.shipyard"));
            case "plannedSize" -> this.buildShipErrorLabel.setText(this.gameResourceBundle.getString("buildShipError.plannedSize"));
            case "successful" -> this.buildShipErrorLabel.setText(this.gameResourceBundle.getString("buildShipError.successful"));
            case "wilderness", "enemiesIsland" -> this.buildShipErrorLabel.setText(this.gameResourceBundle.getString("not.your.island"));
        }
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }

}
