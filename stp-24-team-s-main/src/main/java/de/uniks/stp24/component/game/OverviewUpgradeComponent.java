package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.jobs.IslandUpgradesJobProgressComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ExplanationService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
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

@Component(view = "IslandOverviewUpgrade.fxml")
public class OverviewUpgradeComponent extends AnchorPane {
    @FXML
    public Button confirmUpgrade;
    @FXML
    public HBox upgrade_box;
    @FXML
    public Pane checkExplored;
    @FXML
    public Pane checkColonized;
    @FXML
    public Pane checkUpgraded;
    @FXML
    public Pane checkDeveloped;
    @FXML
    public Text levelOne;
    @FXML
    public Text levelTwo;
    @FXML
    public Label levelTwoText;
    @FXML
    public Text levelThree;
    @FXML
    public Label levelThreeText;
    @FXML
    public Text levelFour;
    @FXML
    public Label levelFourText;
    @FXML
    public ListView<Resource> upgradeUpkeepList;
    @FXML
    public ListView<Resource> upgradeCostList;
    public Button backButton;
    public Button close;
    @FXML
    Pane jobsContainer;

    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    IslandsService islandsService;
    @Inject
    public ExplanationService explanationService;
    @Inject
    App app;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;
    @Inject
    public JobsService jobsService;
    @Inject
    ImageCache imageCache;

    @Inject
    @SubComponent
    public IslandUpgradesJobProgressComponent jobProgressComponent;

    private Jobs.Job currentJob;

    private InGameController inGameController;
    private ObservableList<Jobs.Job> jobObservableList = FXCollections.observableArrayList();
    private enum BUTTON_STATES {ACTIVE, CANCEL_JOB, INACTIVE}
    private BUTTON_STATES currentButtonState = BUTTON_STATES.ACTIVE;
    private boolean updateButtonState = true;

    final Provider<ResourceComponent> resourceComponentProvider = ()-> new ResourceComponent(true, false, true, false, gameResourceBundle, this.imageCache);

    @Inject
    public OverviewUpgradeComponent() {

    }

    @OnInit
    public void setUpgradeJobUpdates() {
        this.jobsService.onGameTicked(this.jobProgressComponent::incrementJobProgress);
        this.jobsService.onJobsLoadingFinished("upgrade", this::setJobFinishers);
        this.jobsService.onJobsLoadingFinished(() ->
                this.jobObservableList = this.jobsService.getJobObservableListOfType("upgrade"));
    }

    @OnInit
    public void addRunnable() {
        this.resourcesService.setOnResourceUpdates(this::setUpgradeButton);
    }

    @OnRender
    public void render() {
        this.jobsContainer.getChildren().add(this.jobProgressComponent);
    }

    public void setUpgradeButton() {
        if (this.updateButtonState && Objects.nonNull(this.islandAttributes.getIsland())) {
            if (Objects.nonNull(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel()))) {
                if (resourcesService.hasEnoughResources(islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel())))
                    this.currentButtonState = BUTTON_STATES.ACTIVE;
                 else this.currentButtonState = BUTTON_STATES.INACTIVE;
            }
            this.setUpgradeButtonStyle();
        }
    }

    private void setUpgradeButtonStyle() {
        this.confirmUpgrade.getStyleClass().clear();
        switch (this.currentButtonState) {
            case ACTIVE -> confirmUpgrade.getStyleClass().add("upgradeButtonActive");
            case INACTIVE -> confirmUpgrade.getStyleClass().add("upgradeButtonInactive");
            case CANCEL_JOB -> this.confirmUpgrade.getStyleClass().add("upgradeButtonCancelJob");
        }
    }

    public void goBack() {
        inGameService.showOnly(inGameController.overviewContainer, inGameController.overviewSitesComponent);
    }

    public void closeOverview() {
        inGameController.overviewContainer.setVisible(false);
        inGameController.selectedIsland.rudderImage.setVisible(false);
        inGameController.selectedIsland.islandIsSelected = false;
        if(!islandsService.keyCodeFlag) {
            inGameController.selectedIsland.flagPane.setVisible(!inGameController.selectedIsland.flagPane.isVisible());
        }
        inGameController.selectedIsland = null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void setListViews() {
        setCosts();
        setConsumes();
    }

    public void buyUpgrade() {
        switch (this.currentButtonState) {
            case ACTIVE -> this.subscriber.subscribe(this.jobsService
                    .beginJob(Jobs.createIslandUpgradeJob(this.islandAttributes.getIsland().id())), job -> {
                this.updateButtonState = false;
                this.currentJob = job;
                this.jobsContainer.setVisible(true);
                this.jobProgressComponent.setJobProgress(job);
                this.jobProgressComponent.setShouldTick(this.jobsService.isCurrentIslandJob(job));
                this.setJobFinishers(job);
                this.currentButtonState = BUTTON_STATES.CANCEL_JOB;
                this.setUpgradeButtonStyle();
            });
            case CANCEL_JOB -> {
                this.updateButtonState = true;
                this.jobsContainer.setVisible(false);
                this.subscriber.subscribe(this.jobsService.stopJob(this.currentJob._id()));
            }
        }
    }

    public void setCosts(){
        upgradeCostList.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "systems", islandAttributes.getIsland().upgrade(), "cost"));
        Map<String, Double> resourceMapCost = islandAttributes.getNeededResources(islandAttributes.getIsland().upgradeLevel());
        ObservableList<Resource> resourceListCost = resourcesService.generateResourceList(resourceMapCost, upgradeCostList.getItems(), null, false);
        upgradeCostList.setItems(resourceListCost);
    }

    public void setConsumes(){
        upgradeUpkeepList.setCellFactory(list -> explanationService.addMouseHoverListener(new CustomComponentListCell<>(app, resourceComponentProvider), "systems", islandAttributes.getIsland().upgrade(), "upkeep"));
        Map<String, Double> resourceMapUpkeep = islandAttributes.getUpkeep(islandAttributes.getIsland().upgradeLevel());
        ObservableList<Resource> resourceListUpkeep = resourcesService.generateResourceList(resourceMapUpkeep, upgradeUpkeepList.getItems(), null, false);
        upgradeUpkeepList.setItems(resourceListUpkeep);
    }

    private void setJobFinishers(Jobs.Job job) {
        this.jobsService.onJobCompletion(job._id(), () -> {
            if (Objects.nonNull(this.islandAttributes.getIsland()))
                if (job.system().equals(this.islandAttributes.getIsland().id())) {
                    this.inGameController.showOverview();
                    this.jobsContainer.setVisible(false);
                }

        });
        this.jobsService.onJobDeletion(job._id(), () -> {
            if (Objects.nonNull(this.islandAttributes.getIsland()))
                if (job.system().equals(this.islandAttributes.getIsland().id()))
                    this.jobsContainer.setVisible(false);
        });
    }

    public void setUpgradeInf() {
        levelOne.setText(islandAttributes.getUpgradeTranslation(1));
        levelTwo.setText(islandAttributes.getUpgradeTranslation(2));
        levelThree.setText(islandAttributes.getUpgradeTranslation(3));
        levelFour.setText(islandAttributes.getUpgradeTranslation(4));
        levelTwoText.setText(islandAttributes.upgradeEffects.get(2));
        levelThreeText.setText(islandAttributes.upgradeEffects.get(3));
        levelFourText.setText(islandAttributes.upgradeEffects.get(4));

        this.updateButtonState = true;
        this.setUpgradeButton();

        FilteredList<Jobs.Job> filteredList = this.jobObservableList
                .filtered(job -> job.system().equals(this.islandAttributes.getIsland().id()));
        this.jobsContainer.setVisible(!filteredList.isEmpty());
        if (!filteredList.isEmpty()) {
            this.currentJob = filteredList.getFirst();
            this.jobProgressComponent.setJobProgress(this.currentJob);
            this.jobProgressComponent.setShouldTick(this.jobsService.isCurrentIslandJob(this.currentJob));
            this.updateButtonState = false;
            this.currentButtonState = BUTTON_STATES.CANCEL_JOB;
            this.setUpgradeButtonStyle();
        }
    }
}
