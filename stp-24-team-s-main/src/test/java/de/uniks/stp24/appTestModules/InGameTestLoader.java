package de.uniks.stp24.appTestModules;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.fleetManager.BlueprintsDetailsComponent;
import de.uniks.stp24.component.game.fleetManager.ChangeFleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.fleetManager.NewFleetComponent;
import de.uniks.stp24.component.game.jobs.*;
import de.uniks.stp24.component.game.technology.*;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import javax.inject.Provider;

public class InGameTestLoader extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    InGameService inGameService;
    @Spy
    ImageCache imageCache;
    @Spy
    EventService eventService;
    @Spy
    protected ShipsApiService shipsApiService;
    @Spy
    Subscriber subscriber;
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Mock
    EventListener eventListener;
    @Spy
    EmpireService empireService;
    @Spy
    GameSystemsApiService gameSystemsApiService;
    @Spy
    PresetsApiService presetsApiService;
    @Spy
    LobbyService lobbyService;
    @Spy
    GameMembersApiService gameMembersApiService;
    @Spy
    EmpireApiService empireApiService;
    @Spy
    protected JobsApiService jobsApiService;
    @Spy
    ExplanationService explanationService;
    @Spy
    TechnologyService technologyService;
    @Spy
    TimerService timerService;
    @Spy
    BattleService battleService;
    @Spy
    public GameLogicApiService gameLogicApiService;
    @Spy
    VariableDependencyService variableDependencyService;
    @Spy
    AnnouncementsService announcementsService;
    @Spy
    GameStatus gameStatus;
    @Spy
    FleetCoordinationService fleetCoordinationService;
    @Spy
    FleetService fleetService;
    @Spy
    protected ShipService shipService;
    @Spy
    FleetApiService fleetApiService;
    @Spy
    WarService warService;
    @Spy
    WarsApiService warsApiService;
    @Spy
    FogOfWar fogOfWar;
    @Spy
    GameStatService gameStatService;



    @InjectMocks
    protected MarketService marketService;
    @InjectMocks
    protected PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    protected StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    protected ClockComponent clockComponent;
    @InjectMocks
    protected OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    protected OverviewUpgradeComponent overviewUpgradeComponent;
    @InjectMocks
    protected IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    protected DetailsComponent detailsComponent;
    @InjectMocks
    protected SitesComponent sitesComponent;
    @InjectMocks
    protected BuildingsComponent buildingsComponent;
    @InjectMocks
    protected EventComponent eventComponent;
    @InjectMocks
    protected InGameController inGameController;
    @InjectMocks
    protected MarketComponent marketComponent;
    @InjectMocks
    protected BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    protected SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    protected BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    protected DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    protected EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    protected VariableService variableService;
    @InjectMocks
    protected HelpComponent helpComponent;
    @InjectMocks
    protected JobsOverviewComponent jobsOverviewComponent;
    @InjectMocks
    protected IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    protected PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    protected PropertiesJobProgressComponent siteJobProgress;
    @InjectMocks
    protected IslandClaimingComponent islandClaimingComponent;
    @InjectMocks
    protected IslandUpgradesJobProgressComponent islandUpgradesJobProgressComponent;
    @InjectMocks
    protected TechnologyOverviewComponent technologyOverviewComponent;
    @InjectMocks
    protected TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    protected ResearchJobComponent researchJobComponent;
    @InjectMocks
    protected EditGameService editGameService;
    @InjectMocks
    protected JobsService jobsService;
    @InjectMocks
    protected CoolerBubbleComponent coolerBubbleComponent;
    @InjectMocks
    protected BattleResultComponent battleResultComponent;

    @InjectMocks
    protected ContactsComponent contactsComponent;
    @InjectMocks
    protected ContactDetailsComponent contactDetailsComponent;
    @InjectMocks
    protected WarComponent warComponent;

    @InjectMocks
    protected FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    protected NewFleetComponent newFleetComponent;
    @InjectMocks
    protected ChangeFleetComponent changeFleetComponent;
    @InjectMocks
    protected BlueprintsDetailsComponent blueprintsDetailsComponent;
    @InjectMocks
    protected TechnologyResearchDetailsComponent technologyResearchDetailsComponent;
    @InjectMocks
    protected TechnologyEffectDetailsComponent technologyEffectDetailsComponent;
    @InjectMocks
    protected IslandTravelComponent islandTravelComponent;
    @InjectMocks
    GameResultPopup gameResultPopup;



    final Provider<ClaimingSiteComponent> claimingComponentProvider = () -> {
        var component = new ClaimingSiteComponent();
        component.imageCache = this.imageCache;
        return component;
    };

    final Provider<JobElementComponent> jobElementComponentProvider = () -> {
        JobElementComponent comp = new JobElementComponent();
        comp.gameResourceBundle = gameResourceBundle;
        comp.islandsService = islandsService;
        comp.jobsService = jobsService;
        comp.imageCache = imageCache;
        comp.subscriber = subscriber;
        comp.fleetService = this.fleetService;
        return comp;
    };

    final Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent comp = new DistrictComponent();
        comp.islandAttributeStorage = this.islandAttributeStorage;
        comp.tokenStorage = this.tokenStorage;
        comp.imageCache = this.imageCache;
        return comp;
    };

    final Provider<IslandOverviewJobProgressComponent> islandOverviewJobProgressComponentProvider = () -> {
        IslandOverviewJobProgressComponent comp = new IslandOverviewJobProgressComponent();
        comp.islandAttributes = this.islandAttributeStorage;
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.jobsService = this.jobsService;
        comp.subscriber = this.subscriber;
        comp.imageCache = this.imageCache;
        comp.app = this.app;
        return comp;
    };

    final Provider<MarketSeasonComponent> marketSeasonComponentProvider = () -> {
        MarketSeasonComponent comp = new MarketSeasonComponent();
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.marketService = this.marketService;
        comp.imageCache = this.imageCache;
        return comp;
    };

    final Provider<TechnologyCategorySubComponent> technologyCategorySubComponentProvider = () ->
            new TechnologyCategorySubComponent(this.technologyCategoryComponent, this.technologyService,
                    this.app, this.technologiesResourceBundle, this.tokenStorage, this.subscriber, this.variablesResourceBundle, this.technologyEffectDetailsComponent,
                    this.technologyResearchDetailsComponent, this.imageCache);

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.setControllers();
        this.setServices();
    }

    protected void setControllers() {

        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.coolerBubbleComponent = this.coolerBubbleComponent;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.islandTravelComponent = this.islandTravelComponent;
        this.inGameController.fleetManagerComponent = this.fleetManagerComponent;
        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.helpComponent = this.helpComponent;
        this.inGameController.contactsOverviewComponent = this.contactsComponent;
        this.inGameController.contactsOverviewComponent.warService = this.warService;
        this.inGameController.warComponent = this.warComponent;
        this.inGameController.contactsOverviewComponent.contactDetailsComponent = this.contactDetailsComponent;
        this.inGameController.battleResultComponent = this.battleResultComponent;

        this.coolerBubbleComponent.announcementsService = this.announcementsService;
        this.coolerBubbleComponent.gameResourceBundle = this.gameResourceBundle;
        this.coolerBubbleComponent.empireService = this.empireService;
        this.coolerBubbleComponent.tokenStorage = this.tokenStorage;
        this.coolerBubbleComponent.jobsService = this.jobsService;
        this.coolerBubbleComponent.subscriber = this.subscriber;

        this.inGameController.fogOfWar.tokenStorage = this.tokenStorage;
        this.inGameController.fogOfWar.subscriber = this.subscriber;
        this.inGameController.fogOfWar.islandsService = this.islandsService;
        this.inGameController.fogOfWar.empireApiService = this.empireApiService;

        this.inGameController.marketOverviewComponent.variableService = this.variableService;
        this.variableService.inGameController = this.inGameController;

        this.announcementsService.technologiesResourceBundle = this.technologiesResourceBundle;
        this.announcementsService.gameResourceBundle = this.gameResourceBundle;
        this.announcementsService.islandsService = this.islandsService;
        this.announcementsService.jobsService = this.jobsService;

        this.technologyOverviewComponent.technologyCategoryComponent = this.technologyCategoryComponent;

        this.technologyCategoryComponent.technologyResearchDetailsComponent = this.technologyResearchDetailsComponent;
        this.technologyCategoryComponent.technologyEffectDetailsComponent = this.technologyEffectDetailsComponent;
        this.technologyCategoryComponent.researchJobComponent = this.researchJobComponent;
        this.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.overviewSitesComponent.sitesComponent = this.sitesComponent;

        this.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;

        this.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;

        this.jobsOverviewComponent.jobProvider = this.jobElementComponentProvider;

        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;

        this.overviewUpgradeComponent.jobProgressComponent = this.islandUpgradesJobProgressComponent;

        this.technologyCategoryComponent.provider = this.technologyCategorySubComponentProvider;

        this.contactsComponent.imageCache = this.imageCache;
        this.contactsComponent.tokenStorage = this.tokenStorage;
        this.contactsComponent.subscriber = new Subscriber();


        this.fleetManagerComponent.newFleetComponent = this.newFleetComponent;
        this.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetManagerComponent.blueprintsDetailsComponent = this.blueprintsDetailsComponent;
        this.fleetManagerComponent.jobsService = this.jobsService;

        this.inGameController.gameResultPopup = this.gameResultPopup;

    }

    protected void setServices() {
        this.inGameController.gameSystemsApiService = this.gameSystemsApiService;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.variableService = this.variableService;
        this.inGameController.inGameService = this.inGameService;
        this.inGameController.tokenStorage = this.tokenStorage;
        this.inGameController.eventService = this.eventService;
        this.inGameController.lobbyService = this.lobbyService;
        this.inGameController.jobsService = this.jobsService;
        this.inGameController.fleetService = this.fleetService;
        this.inGameController.fleetCoordinationService = this.fleetCoordinationService;
        this.inGameController.fleetCoordinationService.fleetService = this.fleetService;
        this.inGameController.contactService = this.contactsService;
        this.inGameController.subscriber = new Subscriber();
        this.islandClaimingComponent.componentProvider = this.claimingComponentProvider;
        this.islandClaimingComponent.islandAttributes = this.islandAttributeStorage;
        this.islandClaimingComponent.islandsService = this.islandsService;
        this.islandClaimingComponent.jobsService = this.jobsService;
        this.islandClaimingComponent.imageCache = this.imageCache;
        this.islandClaimingComponent.subscriber = new Subscriber();

        this.variableService.technologyService = this.technologyService;
        this.variableService.inGameService = this.inGameService;
        this.variableService.subscriber = new Subscriber();

        this.islandsService.gameSystemsService = this.gameSystemsApiService;
        this.islandsService.tokenStorage = this.tokenStorage;
        this.islandsService.subscriber = new Subscriber();
        this.islandsService.app = this.app;
        this.islandsService.imageCache = this.imageCache;

        this.inGameService.presetsApiService = this.presetsApiService;
        this.inGameService.gameStatus = this.gameStatus;

        this.empireService.empireApiService = this.empireApiService;

        this.lobbyService.gameMembersApiService = this.gameMembersApiService;

        this.editGameService.gamesApiService = this.gamesApiService;

        this.resourcesService.gameSystemsApiService = this.gameSystemsApiService;
        this.resourcesService.islandAttributes = this.islandAttributeStorage;
        this.resourcesService.empireService = this.empireService;
        this.resourcesService.tokenStorage = this.tokenStorage;
        this.resourcesService.subscriber = new Subscriber();

        this.explanationService.variablesResourceBundle = this.variablesResourceBundle;
        this.explanationService.gameResourceBundle = this.gameResourceBundle;
        this.explanationService.variableService = this.variableService;
        this.explanationService.app = this.app;

        this.clockComponent.subscriber = new Subscriber();

        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;
        this.islandOverviewJobsComponent.islandAttributes = this.islandAttributeStorage;

        this.buildingsComponent.islandAttributes = this.islandAttributeStorage;
        this.buildingsComponent.jobsService = this.jobsService;

        this.overviewSitesComponent.islandAttributes = this.islandAttributeStorage;

        this.buildingPropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.buildingPropertiesComponent.gameSystemsApiService = this.gameSystemsApiService;
        this.buildingPropertiesComponent.resourcesService = this.resourcesService;
        this.buildingPropertiesComponent.islandsService = this.islandsService;
        this.buildingPropertiesComponent.jobsService = this.jobsService;
        this.buildingPropertiesComponent.subscriber = new Subscriber();
        this.buildingPropertiesComponent.imageCache = this.imageCache;
        this.buildingPropertiesComponent.app = this.app;

        this.timerService.gamesApiService = this.gamesApiService;
        this.timerService.tokenStorage = this.tokenStorage;
        this.timerService.subscriber = new Subscriber();

        this.battleService.fleetService = this.fleetService;
        this.battleService.islandsService = this.islandsService;
        this.battleService.contactsService = this.contactsService;
        this.battleService.tokenStorage = this.tokenStorage;

        this.eventComponent.tokenStorage = this.tokenStorage;
        this.eventComponent.subscriber = new Subscriber();

        this.eventService.empireApiService = this.empireApiService;
        this.eventService.tokenStorage = this.tokenStorage;
        this.eventService.subscriber = new Subscriber();

        this.jobsService.jobsApiService = this.jobsApiService;
        this.jobsService.tokenStorage = this.tokenStorage;
        this.jobsService.subscriber = new Subscriber();

        this.storageOverviewComponent.resourcesService = this.resourcesService;
        this.storageOverviewComponent.empireService = this.empireService;
        this.storageOverviewComponent.tokenStorage = this.tokenStorage;
        this.storageOverviewComponent.subscriber = new Subscriber();

        this.marketService.presetsApiService = this.presetsApiService;
        this.marketService.empireApiService = this.empireApiService;
        this.marketService.subscriber = new Subscriber();

        this.marketComponent.marketSeasonComponentProvider = this.marketSeasonComponentProvider;
        this.marketComponent.explanationService = this.explanationService;
        this.marketComponent.presetsApiService = this.presetsApiService;
        this.marketComponent.variableService = this.variableService;
        this.marketComponent.marketService = this.marketService;
        this.marketComponent.tokenStorage = this.tokenStorage;
        this.marketComponent.subscriber = new Subscriber();
        this.marketComponent.imageCache = this.imageCache;

        this.sitesComponent.districtComponentProvider = this.districtComponentProvider;
        this.sitesComponent.attributeStorage = this.islandAttributeStorage;
        this.sitesComponent.jobsService = this.jobsService;

        this.researchJobComponent.jobsService = this.jobsService;

        this.overviewUpgradeComponent.islandAttributes = this.islandAttributeStorage;
        this.overviewUpgradeComponent.jobsService = this.jobsService;

        this.sitePropertiesComponent.islandAttributeStorage = this.islandAttributeStorage;
        this.sitePropertiesComponent.gameSystemsApiService = this.gameSystemsApiService;
        this.sitePropertiesComponent.gameResourceBundle = this.gameResourceBundle;
        this.sitePropertiesComponent.resourcesService = this.resourcesService;
        this.sitePropertiesComponent.jobsService = this.jobsService;
        this.sitePropertiesComponent.subscriber = new Subscriber();
        this.sitePropertiesComponent.imageCache = this.imageCache;
        this.sitePropertiesComponent.app = this.app;

        this.islandOverviewJobsComponent.jobsService = this.jobsService;

        this.propertiesJobProgressComponent.jobsService = this.jobsService;

        this.siteJobProgress.jobsService = this.jobsService;

        this.timerService.eventListener = this.eventListener;

        this.jobsOverviewComponent.jobsService = this.jobsService;

        this.technologyService.gameLogicApiService = this.gameLogicApiService;
        this.technologyService.presetsApiService = this.presetsApiService;

        this.technologyService.empireApiService = this.empireApiService;
        this.technologyService.eventListener = this.eventListener;
        this.technologyService.tokenStorage = this.tokenStorage;
        this.technologyService.subscriber = new Subscriber();

        this.variableDependencyService.variableService = this.variableService;

        this.technologyOverviewComponent.technologiesResourceBundle = this.technologiesResourceBundle;

        this.fleetCoordinationService.contactsService = this.contactsService;

        this.fleetService.tokenStorage = this.tokenStorage;
        this.fleetService.fleetApiService = this.fleetApiService;

        this.contactsService.tokenStorage = this.tokenStorage;
        this.contactsService.warService = this.warService;
        this.contactsService.empireApiService = this.empireApiService;
        this.contactsService.subscriber = new Subscriber();
        this.contactsService.eventListener = this.eventListener;
        this.contactsService.islandsService = this.islandsService;
        this.contactsService.islandsService.gameLogicApiService = this.gameLogicApiService;
        this.contactsService.contactsComponent = this.contactsComponent;
        this.contactsService.contactsComponent.contactDetailsComponent = this.contactDetailsComponent;
        this.inGameController.contactsOverviewComponent.contactDetailsComponent.warComponent = this.warComponent;

        this.warService.warsApiService = this.warsApiService;

        this.technologyCategoryComponent.jobsService = this.jobsService;

        this.fleetCoordinationService.islandsService = this.islandsService;
        this.fleetCoordinationService.timerService = this.timerService;
        this.fleetCoordinationService.tokenStorage = this.tokenStorage;
        this.fleetCoordinationService.shipService = this.shipService;
        this.fleetCoordinationService.jobsService = this.jobsService;
        this.fleetCoordinationService.subscriber = new Subscriber();
        this.fleetCoordinationService.imageCache = this.imageCache;
        this.fleetCoordinationService.app = this.app;

        this.fleetService.jobsApiService = this.jobsApiService;
        this.fleetService.eventListener = this.eventListener;
        this.fleetService.jobsService = this.jobsService;
        this.fleetService.subscriber = new Subscriber();

        this.islandTravelComponent.fleetCoordinationService = this.fleetCoordinationService;
        this.islandTravelComponent.fleetService = this.fleetService;
        this.islandTravelComponent.jobsService = this.jobsService;
        this.islandTravelComponent.shipService = this.shipService;
        this.islandTravelComponent.subscriber = new Subscriber();

        this.fleetManagerComponent.resourcesService = this.resourcesService;
        this.fleetManagerComponent.variableService = this.variableService;
        this.fleetManagerComponent.islandsService = this.islandsService;
        this.fleetManagerComponent.tokenStorage = this.tokenStorage;
        this.fleetManagerComponent.fleetService = this.fleetService;
        this.fleetManagerComponent.shipService = this.shipService;
        this.fleetManagerComponent.jobsService = this.jobsService;
        this.fleetManagerComponent.subscriber = new Subscriber();
        this.fleetManagerComponent.app = this.app;

        this.shipService.variableDependencyService = this.variableDependencyService;
        this.shipService.shipsApiService = this.shipsApiService;
        this.shipService.eventListener = this.eventListener;
        this.shipService.tokenStorage = this.tokenStorage;
        this.shipService.jobsApiService = this.jobsApiService;
        this.shipService.subscriber = new Subscriber();

        this.gameStatService.islandsService = this.islandsService;
        this.gameStatService.tokenStorage = this.tokenStorage;

        this.gameResultPopup.subscriber = new Subscriber();
        this.gameResultPopup.imageCache = this.imageCache;
        this.gameResultPopup.tokenStorage = this.tokenStorage;
        this.gameResultPopup.app = this.app;
        this.gameResultPopup.gamesApiService = this.gamesApiService;
        this.gameResultPopup.inGameService = this.inGameService;
    }

    protected void clearStyles() {
        this.technologyOverviewComponent.getStylesheets().clear();
        this.technologyCategoryComponent.getStylesheets().clear();
        this.buildingPropertiesComponent.getStylesheets().clear();
        this.inGameController.rootPane.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.islandClaimingComponent.getStylesheets().clear();
        this.sitePropertiesComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.jobsOverviewComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.marketComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
    }
}
