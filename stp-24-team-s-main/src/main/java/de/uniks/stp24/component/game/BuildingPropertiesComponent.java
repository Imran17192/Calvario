package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.BuildingAttributes;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.*;

@Component(view = "BuildingProperties.fxml")
public class BuildingPropertiesComponent extends AnchorPane {

    @FXML
    ListView<Resource> buildingCostsListView;
    @FXML
    Button buyButton;
    @FXML
    ListView<Resource> buildingProducesListView;
    @FXML
    ListView<Resource> buildingConsumesListView;
    @FXML
    Button closeButton;
    @FXML
    Button destroyButton;
    @FXML
    Text buildingName;
    @FXML
    ImageView buildingImage;
    @FXML
    Pane jobProgressPane;
    @FXML
    Pane textInfoPane;
    @FXML
    Label textInfoLabel;

    @Inject
    public JobsService jobsService;
    @Inject
    public ResourcesService resourcesService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public IslandsService islandsService;
    @Inject
    @SubComponent
    public PropertiesJobProgressComponent propertiesJobProgressComponent;
    @Inject
    public GameSystemsApiService gameSystemsApiService;
    @Inject
    public App app;
    @Inject
    public ExplanationService explanationService;
    @Inject
    public VariableService variableService;
    @Inject
    public ImageCache imageCache;

    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public IslandAttributeStorage islandAttributeStorage;
    Map<String, String> buildingsMap;
    String buildingType;
    InGameController inGameController;
    Map<String, Double> priceOfBuilding;
    ObservableList<Jobs.Job> buildingJobs;

    Jobs.Job currentJob;
    public BuildingAttributes certainBuilding;

    final Provider<ResourceComponent> negativeResouceProvider = () -> new ResourceComponent("negative", this.gameResourceBundle, this.imageCache);
    final Provider<ResourceComponent> positiveResourceProvider = () -> new ResourceComponent("positive", this.gameResourceBundle, this.imageCache);

    @Inject
    public BuildingPropertiesComponent() {
    }

    @OnInit
    public void init() {
        buildingsMap = buildingsIconPathsMap;
    }

    @OnInit
    public void setBuildingUpdates() {
        this.jobsService.onJobsLoadingFinished("building", this::setBuildingJobFinishers);
        this.jobsService.onJobsLoadingFinished(() ->
                this.buildingJobs = this.jobsService.getJobObservableListOfType("building"));
//{{{        this.jobsService.onJobCommonFinish(this::updateIslandBuildings);}}}
    }

    @OnRender
    public void render() {
        this.setPickOnBounds(false);
        this.jobProgressPane.setPickOnBounds(false);
        this.propertiesJobProgressComponent.setPickOnBounds(false);
        this.jobProgressPane.getChildren().add(this.propertiesJobProgressComponent);
    }

    @OnRender
    public void addRunnable() {
        // this method will be run after resources update themselves, to (dis)-enable buttons dynamically
        resourcesService.setOnResourceUpdates(this::setButtonsDisable);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }


    public void setBuildingType(String buildingType, String jobID, BUILT_STATUS isBuilt) {
        this.buildingType = buildingType;

        displayInfoBuilding();

        if (Objects.nonNull(this.buildingJobs))
            this.setJobsPaneProgress(this.buildingJobs.stream().filter(started -> started._id().equals(jobID)
                    && started.system().equals(this.tokenStorage.getIsland().id())).findFirst().orElse(null));

        switch (isBuilt) {
            case BUILT -> {
                this.buyButton.setVisible(false);
                this.destroyButton.setVisible(true);
                setDestroyButtonDisable();
            }
            case QUEUED -> {
                this.buyButton.setVisible(false);
                this.destroyButton.setVisible(false);
            }
            case NOT_BUILT -> {
                this.buyButton.setVisible(true);
                this.destroyButton.setVisible(false);
                setBuyButtonDisable();
            }
        }
    }


    private void setBuildingJobFinishers(Jobs.Job job) {
        this.jobsService.onJobDeletion(job._id(), () -> {
            if (Objects.nonNull(this.currentJob)) {
                if (this.currentJob._id().equals(job._id())) this.getParent().setVisible(false);
                else this.setJobsPaneProgress(this.currentJob);
            }
        });

        this.jobsService.onJobCompletion(job._id(), () -> {
            if (Objects.nonNull(this.currentJob)) {
                if (this.currentJob._id().equals(job._id())) {
                    this.setJobsPaneVisibility(false);
                    this.destroyButton.setVisible(true);
                    setDestroyButtonDisable();
                } else this.setJobsPaneProgress(this.currentJob);
            }
        });
    }

    private void setJobsPaneProgress(Jobs.Job job) {
        this.currentJob = job;
        this.setJobsPaneVisibility(Objects.nonNull(job));
        if (Objects.nonNull(job)) {
            this.propertiesJobProgressComponent.setJobProgress(job);
            this.propertiesJobProgressComponent.setShouldTick(this.jobsService.isCurrentIslandJob(job));
        }
    }

    public void setJobsPaneVisibility(boolean isVisible) {
        this.jobProgressPane.setVisible(isVisible);
        this.buildingCostsListView.setVisible(!isVisible);
    }

    // Checks if buy and destroy building has to be deactivated
    public void setButtonsDisable() {
        setDestroyButtonDisable();
        setBuyButtonDisable();
    }

    private void setBuyButtonDisable() {
        // check
        // 1) if empire has enough resources to build this building
        // 2) if island has enough capacity
        if (Objects.nonNull(buildingType)) {
            int islandJobsInQueue = jobsService.getStructureJobsInQueueCount(islandAttributeStorage.getIsland().id());
            if (islandAttributeStorage.getUsedSlots() + islandJobsInQueue >=
                    islandAttributeStorage.getIsland().resourceCapacity()) {
                buyButton.setDisable(true);
            } else {
                subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
                    priceOfBuilding = result.cost();
                    buyButton.setDisable(!resourcesService.hasEnoughResources(priceOfBuilding));
                }, error -> System.out.println("error updateButtonStates(): " + error));
            }
        }
    }

    private void setDestroyButtonDisable() {
        if (Objects.nonNull(tokenStorage.getIsland()) &&
                tokenStorage.getIsland().buildings().contains(buildingType))
            destroyButton.setDisable(false);
    }

    public void destroyBuilding() {
        setDestroyButtonDisable();
        setVisible(false);
        inGameController.handleDeleteStructure(buildingType);
    }

    public void buyBuilding() {
        subscriber.subscribe(resourcesService.getResourcesBuilding(buildingType), result -> {
                    priceOfBuilding = result.cost();
                    if (resourcesService.hasEnoughResources(priceOfBuilding)) {
                        this.subscriber.subscribe(this.jobsService.beginJob(
                                        Jobs.createBuildingJob(this.tokenStorage.getIsland().id(), this.buildingType)), job -> {
                                    this.setJobsPaneProgress(job);
                                    this.updateIslandBuildings();
                                    this.setBuildingJobFinishers(job);
                                },
                                error -> System.out.println("Error in buyBuilding: " + error));
                        buyButton.setVisible(false);

                    } else buyButton.setDisable(true);
                },
                error -> System.out.println("Error in buyBuilding: " + error));
    }

    private void updateIslandBuildings() {
        if (Objects.nonNull(this.islandAttributeStorage.getIsland())) {
            this.subscriber.subscribe(this.gameSystemsApiService.getSystem(
                    this.tokenStorage.getGameId(), this.islandAttributeStorage.getIsland().id()), island -> {
                this.tokenStorage.setIsland(this.islandsService.updateIsland(island));
                this.islandAttributeStorage.setIsland(this.islandsService.updateIsland(island));
                this.inGameController.islandsService.updateIslandBuildings(this.islandAttributeStorage,
                        this.inGameController, this.islandAttributeStorage.getIsland().buildings());
                this.inGameController.setSitePropertiesInvisible();
            }, error -> System.out.println("Error by updating island buildings in BuildingPropertiesComponent:\n"
                    + error.getMessage()));
        }
    }

    //Gets resources of the building and shows them in three listviews
    public void displayInfoBuilding() {
        buildingImage.setImage(this.imageCache.get("/" + buildingsMap.get(buildingType)));
        buildingName.setText(gameResourceBundle.getString(buildingTranslation.get(buildingType)));

        setCertainBuilding();
        resourceListGeneration(certainBuilding);

        buildingCostsListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, negativeResouceProvider), "buildings", buildingType, "cost"));
        buildingProducesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, positiveResourceProvider), "buildings", buildingType, "production"));
        buildingConsumesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, negativeResouceProvider), "buildings", buildingType, "upkeep"));
    }

    //Sets upkeep, production and cost of buildings in listviews
    private void resourceListGeneration(BuildingAttributes buildingDto) {
        if (Objects.nonNull(buildingDto)) {
            Map<String, Double> resourceMapUpkeep = buildingDto.upkeep();
            ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(resourceMapUpkeep, buildingConsumesListView.getItems(), null, false);
            buildingConsumesListView.setItems(resourceListUpkeep);

            Map<String, Double> resourceMapProduce = buildingDto.production();
            ObservableList<Resource> resourceListProduce = resourcesService.generateResourceList(resourceMapProduce, buildingProducesListView.getItems(), null, false);
            buildingProducesListView.setItems(resourceListProduce);

            Map<String, Double> resourceMapCost = buildingDto.cost();
            ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(resourceMapCost, buildingCostsListView.getItems(), null, false);
            buildingCostsListView.setItems(resourceListCost);

            /* Sets infoText for buildings that does not produce anything */
            if (buildingProducesListView.getItems().isEmpty()) {
                textInfoPane.setVisible(true);
                if (buildingDto.id().equals("fortress")) {
                    textInfoLabel.setText(gameResourceBundle.getString("building.stronghold.info"));
                } else if (buildingDto.id().equals("shipyard")) {
                    textInfoLabel.setText(gameResourceBundle.getString("building.shipyard.info"));
                }
            } else {
                textInfoPane.setVisible(false);
                textInfoLabel.setText("");
            }
        }
    }

    public void onClose() {
        inGameController.buildingPropertiesComponent.setVisible(false);
    }

    private void setCertainBuilding() {
        for (BuildingAttributes building : islandAttributeStorage.buildingsAttributes) {
            if (building.id().equals(buildingType)) {
                certainBuilding = building;
                priceOfBuilding = certainBuilding.cost();
            }
        }
    }
}
