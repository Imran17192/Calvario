package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.DistrictAttributes;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ExplanationService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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

import static de.uniks.stp24.service.Constants.siteTranslation;
import static de.uniks.stp24.service.Constants.sitesIconPathsMap;

@Component(view = "SiteProperties.fxml")
public class SitePropertiesComponent extends AnchorPane {
    @FXML
    GridPane siteAmountGridPane;
    @FXML
    ListView<Resource> siteCostsListView;
    @FXML
    ListView<Resource> siteProducesListView;
    @FXML
    ListView<Resource> siteConsumesListView;
    @FXML
    Button buildSiteButton;
    @FXML
    Button destroySiteButton;
    @FXML
    ImageView siteImage;
    @FXML
    Button closeWindowButton;
    @FXML
    Text siteName;
    @FXML
    AnchorPane jobPane;
    @FXML
    ScrollPane siteAmountScrollPane;

    String siteType;

    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public IslandAttributeStorage islandAttributeStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    public JobsService jobsService;

    @Inject
    public ResourcesService resourcesService;
    @Inject
    public ImageCache imageCache;

    @Inject
    public IslandsService islandsService;
    @Inject
    public ExplanationService explanationService;
    @Inject
    public App app;

    @Inject
    public GameSystemsApiService gameSystemsApiService;

    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    @SubComponent
    public PropertiesJobProgressComponent siteJobProgress;

    final Provider<ResourceComponent> negativeResouceProvider = () -> new ResourceComponent("negative", this.gameResourceBundle, this.imageCache);
    final Provider<ResourceComponent> positiveResourceProvider = () -> new ResourceComponent("positive", this.gameResourceBundle, this.imageCache);

    final Provider<ImageView> siteEmptyCellProvider = () -> {
        ImageView imageView = new ImageView(this.imageCache.get("/de/uniks/stp24/icons/other/empty_building_small_element.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        return imageView;
    };

    final Provider<ImageView> siteCellProvider = () -> {
        ImageView imageView = new ImageView(this.imageCache.get("/de/uniks/stp24/icons/other/building_small_element.png"));
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        return imageView;
    };

    Map<String, String> sitesMap;
    private ObservableList<Job> siteJobs;

    InGameController inGameController;

    @Inject
    public SitePropertiesComponent() {}

    @OnInit
    public void init(){ sitesMap = sitesIconPathsMap;}

    @OnInit
    public void setSitesJobUpdates() {
        this.jobsService.onJobsLoadingFinished("district", this::setSiteFinishers);
        this.jobsService.onJobsLoadingFinished(() ->
                this.siteJobs = this.jobsService.getJobObservableListOfType("district"));
    }

    @OnRender
    public void render() {
        this.jobPane.getChildren().add(this.siteJobProgress);
        this.setPickOnBounds(false);
        this.jobPane.setPickOnBounds(false);
        this.jobPane.setVisible(false);
    }

    @OnRender
    public void addRunnable() {
        // this method will be run after resources update themselves, to (dis)-enable buttons dynamically
        resourcesService.setOnResourceUpdates(this::setButtonsDisable);
        jobsService.onJobCommonStart(this::setButtonsDisable);
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

    public void setSiteType(String siteType){
        this.siteType = siteType;
        this.siteName.setText(gameResourceBundle.getString(siteTranslation.get(siteType)));
        this.siteImage.setImage(imageCache.get("/" + sitesMap.get(siteType)));
        displayCostsOfSite();
        displayAmountOfSite();

        if (Objects.nonNull(this.siteJobs))
            this.setJobsPaneProgress(this.siteJobs.stream().filter(started -> started.district().equals(siteType)
                && started.system().equals(this.tokenStorage.getIsland().id())).findFirst().orElse(null));
    }

    public void buildSite(){
        this.subscriber.subscribe(this.jobsService.beginJob(Jobs.createDistrictJob(
                this.tokenStorage.getIsland().id(), this.siteType)), job ->  {
            this.setJobsPaneProgress(job);
            this.setSiteFinishers(job);
        });
    }

    private void setSiteFinishers(Job job) {
        this.jobsService.onJobDeletion(job._id(), () -> {
            if (Objects.nonNull(this.islandAttributeStorage.getIsland()) &&
                    job.system().equals(this.islandAttributeStorage.getIsland().id()))
                if (job.district().equals(this.siteType)) this.setJobsPaneVisibility(false);
        });
        this.jobsService.onJobCompletion(job._id(), () -> {
            if (Objects.nonNull(this.islandAttributeStorage.getIsland()) &&
                    job.system().equals(this.islandAttributeStorage.getIsland().id()))
                if (job.district().equals(this.siteType)) this.setJobsPaneVisibility(false);
        });
    }

    private void setJobsPaneVisibility(boolean isVisible) {
        this.jobPane.setVisible(isVisible);
        this.siteCostsListView.setVisible(!isVisible);
    }

    private void setJobsPaneProgress(Jobs.Job job) {
        this.setJobsPaneVisibility(Objects.nonNull(job));
        if (Objects.nonNull(job)) {
            this.siteJobProgress.setJobProgress(job);
            this.siteJobProgress.setShouldTick(this.jobsService.isCurrentIslandJob(job));
        }
    }

    //Calls handleDeleteStructure in inGameController which shows the deleteWarning popup
    //and calls method in DeleteStructureComponent
    public void destroySite(){
        inGameController.handleDeleteStructure(siteType);
    }

    //Gets resources of site and displays them in listviews
    public void displayCostsOfSite(){
        siteCostsListView.setSelectionModel(null);
        for (DistrictAttributes district: islandAttributeStorage.districtAttributes){
            if (district.id().equals(siteType)) {
                resourceListGeneration(district);
                break;
            }
        }
        siteConsumesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, negativeResouceProvider), "districts", siteType, "upkeep"));
        siteCostsListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, negativeResouceProvider), "districts", siteType, "cost"));
        siteProducesListView.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, positiveResourceProvider), "districts", siteType, "production"));
    }

    //Uses a GridPane to display a graphic view of how many sites of each type you have
    public void displayAmountOfSite() {
        buildSiteButton.setDisable(false);
        destroySiteButton.setDisable(false);

        int amountSite = Objects.nonNull(tokenStorage.getIsland().sites().get(siteType)) ?
                tokenStorage.getIsland().sites().get(siteType) : 0;
        int amountSiteSlots = tokenStorage.getIsland().sitesSlots().get(siteType);

        siteAmountScrollPane.setVvalue(0);
        siteAmountGridPane.getChildren().clear();
        siteAmountGridPane.getRowConstraints().clear();
        siteAmountGridPane.addRow(0);

        int x = 0, y = 0, slots = 0, builtSlots = 0;
        while (slots != amountSiteSlots) {
            if (builtSlots < amountSite) siteAmountGridPane.add(siteCellProvider.get(), x, y);
            else siteAmountGridPane.add(siteEmptyCellProvider.get(), x, y);

            slots++;
            builtSlots++;
            x++;
            if (x == 5) {
                x = 0;
                y++;
                siteAmountGridPane.addRow(y);
            }
        }

        if (amountSiteSlots == amountSite)
            buildSiteButton.setDisable(true);
        else
            setButtonsDisable();

        destroySiteButton.setDisable(amountSite == 0);
    }

    private void setButtonsDisable() {
        // checks:
        // 1) if empire has enough resources to build a site cell
        // 2) if island has enough capacity for this building
        if (Objects.nonNull(siteType)) {
            Map<String, Double> costSite = Objects.requireNonNull(getCertainSite()).cost();
            int islandJobsInQueue = jobsService.getStructureJobsInQueueCount(islandAttributeStorage.getIsland().id());
            buildSiteButton.setDisable(!resourcesService.hasEnoughResources(costSite)
            ||
            islandAttributeStorage.getUsedSlots() + islandJobsInQueue >=
                    islandAttributeStorage.getIsland().resourceCapacity());
        }
    }

    private void resourceListGeneration(DistrictAttributes site) {
        ObservableList<Resource> resourceListPrice = resourcesService.generateResourceList(site.cost(), siteCostsListView.getItems(),null, false);
        siteCostsListView.setItems(resourceListPrice);
        ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(site.upkeep(), siteConsumesListView.getItems(), null, false);
        siteConsumesListView.setItems(resourceListUpkeep);
        ObservableList<Resource> resourceListProduce = resourcesService.generateResourceList(site.production(), siteProducesListView.getItems(), null, false);
        siteProducesListView.setItems(resourceListProduce);
    }

    private DistrictAttributes getCertainSite(){
        for(DistrictAttributes site: islandAttributeStorage.districtAttributes)
            if (site.id().equals(siteType)) return site;
        return null;
    }

    public void onClose() { setVisible(false);}
}