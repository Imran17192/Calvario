package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Site;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.islandTranslation;

@Component(view = "IslandClaiming.fxml")
public class IslandClaimingComponent extends Pane {
    public Button closeClaimingButton;
    @FXML
    SplitPane travelButtonControlPane;
    @FXML
    SplitPane exploreButtonControlPane;
    @FXML
    Text islandTypeText;
    @FXML
    Text colonizersText;
    @FXML
    Text timeText;
    @FXML
    Text capacityText;
    @FXML
    ImageView capacityImage;
    @FXML
    ImageView colonizersImage;
    @FXML
    ImageView timerImage;
    @FXML
    Button exploreButton;
    @FXML
    Button travelButton;
    @FXML
    Button cancelJobButton;
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Pane colonizePane;
    @FXML
    Text noSitesText;
    @FXML
    ListView<Site> sitesListView;
    @FXML
    ListView<de.uniks.stp24.model.Resource> consumeListView;
    @FXML
    ListView<de.uniks.stp24.model.Resource> costsListView;
    @FXML
    Tooltip travelTooltip;
    @FXML
    Tooltip claimingTooltip;


    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public JobsService jobsService;
    @Inject
    public ImageCache imageCache;
    @Inject
    public IslandsService islandsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public FleetCoordinationService fleetCoordinationService;
    @Inject
    public FleetService fleetService;
    @Inject
    public ShipService shipService;
    @Inject
    App app;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public Provider<ClaimingSiteComponent> componentProvider;
    final Provider<ResourceComponent> negativeResourceProvider = () -> new ResourceComponent("negative", gameResourceBundle, this.imageCache);
    private ObservableList<Job> upgradeJobs = FXCollections.observableArrayList();
    private ObservableList<Job> travelJobs = FXCollections.observableArrayList();
    private final ObservableList<Site> siteObservableList = FXCollections.observableArrayList();
    private final ObservableList<de.uniks.stp24.model.Resource> consumeObservableList = FXCollections.observableArrayList();
    private final ObservableList<de.uniks.stp24.model.Resource> costsObservableList = FXCollections.observableArrayList();
    private Island currentIsland;
    private Job islandJob;
    private double incrementAmount;
    private double progress;

    private final Duration TOOLTIP_ANIMATION_DURATION = Duration.seconds(0.1);

    @Inject
    public IslandClaimingComponent() {}


    @OnRender
    public void render() {
        this.travelTooltip.setShowDelay(TOOLTIP_ANIMATION_DURATION);
        this.claimingTooltip.setShowDelay(TOOLTIP_ANIMATION_DURATION);
        this.travelTooltip.setHideDelay(TOOLTIP_ANIMATION_DURATION);
        this.claimingTooltip.setHideDelay(TOOLTIP_ANIMATION_DURATION);

        this.travelButtonControlPane.setTooltip(this.travelTooltip);
        this.exploreButtonControlPane.setTooltip(this.claimingTooltip);

        this.timerImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/time.png"));
        this.capacityImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/capacity_icon.png"));
        this.colonizersImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/islands/crewmates_icon.png"));
        this.setPickOnBounds(false);
    }

    public void setIslandInformation(Island island) {
        this.currentIsland = island;
        this.islandTypeText.setText(this.gameResourceBundle.getString(islandTranslation.get(island.type().toString())));
        this.capacityText.setText(String.valueOf(island.resourceCapacity()));
        this.colonizersText.setText(String.valueOf(island.crewCapacity()));

        if (this.currentIsland.upgrade().equals("explored")) {
            this.siteObservableList.clear();
            this.consumeObservableList.clear();
            this.costsObservableList.clear();
            this.timeText.setText("12");

            island.sitesSlots().forEach((name, amount) -> this.siteObservableList
                    .add(new Site(name, null, null, null, 0, amount)));
            this.sitesListView.setItems(this.siteObservableList);
            this.sitesListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.componentProvider));
            this.noSitesText.setVisible(this.sitesListView.getItems().isEmpty());

            this.islandAttributes.systemUpgradeAttributes.colonized().cost().forEach((name, amount) -> this.costsObservableList
                    .add(new de.uniks.stp24.model.Resource(name, amount, 0)));
            this.costsListView.setItems(this.costsObservableList);
            this.costsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));

            this.islandAttributes.systemUpgradeAttributes.colonized().upkeep().forEach((name, amount) -> this.consumeObservableList
                    .add(new de.uniks.stp24.model.Resource(name, amount, 0)));
            this.consumeListView.setItems(this.consumeObservableList);
            this.consumeListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));

            this.exploreButton.setText(this.gameResourceBundle.getString("claiming.colonize"));
            this.colonizePane.setVisible(true);
        } else {
            this.exploreButton.setText(this.gameResourceBundle.getString("claiming.explore"));
            this.colonizePane.setVisible(false);
            this.timeText.setText("3");
        }

        this.setFleetInformation(this.fleetCoordinationService.getSelectedFleet());
        this.setClaimingInformation();
    }

    public void exploreIsland() {
        this.subscriber.subscribe(this.jobsService.beginJob(Jobs.createIslandUpgradeJob(this.currentIsland.id())),
                job -> {
            this.refreshJobProgressBar(job);
            this.setJobFinishers(job);
        }, Throwable::printStackTrace);
    }

    private void refreshJobProgressBar(Job job) {
        this.islandJob = job;
        this.progress = job.progress();
        this.incrementAmount = 1/job.total();
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
        this.setProgressBarVisibility(true);
    }


    public void setClaimingInformation() {
        this.setProgressBarVisibility(false);
        this.exploreButton.setDisable(true);
        this.exploreButtonControlPane.setTooltip(this.claimingTooltip);

        // Check whether the island is being upgraded
        Optional<Job> upgradeJob = this.upgradeJobs.stream().filter(job ->
                job.system().equals(this.currentIsland.id())).findFirst();
        if (upgradeJob.isPresent()) {
            this.refreshJobProgressBar(upgradeJob.get());
            return;
        }

        // Check if the island has enough ships of corresponding type to begin the claiming
        String requiredShipType = this.currentIsland.upgrade().equals("unexplored") ? "explorer" : "colonizer";
        ObservableList<Fleet> fleetsOnIsland = this.fleetService.getFleetsOnIsland(this.currentIsland.id());
        if (!fleetsOnIsland.isEmpty()) {
            fleetsOnIsland.forEach(fleet ->
                this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()),
                        result -> Arrays.stream(result)
                            .map(ReadShipDTO::type)
                            .filter(type -> type.equals(requiredShipType)).findFirst()
                            .map(type -> {
                                this.exploreButton.setDisable(false);
                                this.exploreButtonControlPane.setTooltip(null);
                                return type;
                            }).orElseGet(() -> {
                                this.claimingTooltip.setText("No fleets possess a ship of type " + requiredShipType);
                                return "";
                            }),
                        error -> System.out.printf("Caught an error while trying to retrieve ships for" +
                                              "fleets in the IslandClaimingComponent:\n%s", error.getMessage())
                ));
        } else this.claimingTooltip.setText("No fleets of your empire are near this island");
    }

    public void setFleetInformation(Fleet fleet) {
        if (Objects.isNull(this.currentIsland)) return;

        this.travelButton.setDisable(true);
        this.travelButtonControlPane.setTooltip(this.travelTooltip);
        this.timeText.setText("??");

        if (Objects.nonNull(fleet)) {
            if (!this.currentIsland.id().equals(fleet.location())) {
                this.fleetCoordinationService.generateTravelPaths(fleet.location(), this.currentIsland.id(), fleet);

                this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), dtos -> {
                    int speed = this.shipService.getFleetSpeed(dtos);
                    this.timeText.setText(""+this.fleetCoordinationService.getTravelDuration(
                            fleet.location(), this.currentIsland.id(), speed));
                }, error -> System.out.printf("Caught an error while trying to get travel duration in the " +
                        "IslandClaimingComponent:\n%s", error.getMessage()));

                if (this.travelJobs.filtered(job -> job.fleet().equals(fleet._id())).isEmpty()) {
                    this.subscriber.subscribe(this.shipService.getShipsOfFleet(fleet._id()), result -> {
                        if (result.length != 0) {
                            this.travelButton.setDisable(false);
                            this.travelButtonControlPane.setTooltip(null);
                        }
                        else this.travelTooltip.setText("This fleet has no ships for travel!");
                    }, error -> System.out.printf("Caught an error while trying to retrieve ships " +
                                                  "of the fleet in IslandClaimingComponent:\n %s", error.getMessage()));
                } else this.travelTooltip.setText("This fleet is already traveling! Stop it's travel and try again!");
            } else this.travelTooltip.setText("The fleet is already parked on this island!");
        } else this.travelTooltip.setText("Select the fleet first!");
    }

    public void travelToIsland() {
        this.fleetCoordinationService.travelToIsland(this.currentIsland.id(),
                this.fleetCoordinationService.getSelectedFleetInstance());
        this.travelButton.setDisable(true);
    }

    @OnRender
    public void setJobUpdates() {
        this.jobsService.onJobsLoadingFinished(() -> {
            this.upgradeJobs = this.jobsService.getJobObservableListOfType("upgrade");
            this.travelJobs = this.jobsService.getJobObservableListOfType("travel");
        });

        this.jobsService.onJobsLoadingFinished("upgrade", this::setJobFinishers);
            this.jobsService.onGameTicked(this::incrementProgress);

        this.fleetCoordinationService.onFleetSelected(this::setFleetInformation);
    }

    private void setJobFinishers(Job job) {
        this.jobsService.onJobCompletion(job._id(), () -> {
            if (Objects.nonNull(this.currentIsland))
                if (this.currentIsland.id().equals(job.system())) this.getParent().setVisible(false);
        });
    }


    private void incrementProgress() {
        this.progress++;
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
    }

    private void setProgressBarVisibility(boolean isVisible) {
        this.exploreButton.setVisible(!isVisible);
        this.cancelJobButton.setVisible(isVisible);
        this.jobProgressBar.setVisible(isVisible);
    }

    public void close() {
        this.getParent().setVisible(false);
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.islandJob._id()),
                result -> this.setProgressBarVisibility(false));
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
