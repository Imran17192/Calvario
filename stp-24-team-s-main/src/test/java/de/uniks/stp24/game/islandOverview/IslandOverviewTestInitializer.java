package de.uniks.stp24.game.islandOverview;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.fleetManager.BlueprintsDetailsComponent;
import de.uniks.stp24.component.game.fleetManager.ChangeFleetComponent;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.fleetManager.NewFleetComponent;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.component.game.jobs.IslandUpgradesJobProgressComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
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
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static org.mockito.Mockito.spy;

public class IslandOverviewTestInitializer extends ControllerTest {
    @Spy
    GamesApiService gamesApiService;
    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;
    @Spy
    ImageCache imageCache;
    @Spy
    EventService eventService;
    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
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
    @Mock
    JobsService jobsService;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    ExplanationService explanationService;
    @Spy
    TechnologyService technologyService;
    @Spy
    TimerService timerService;
    @Spy
    GameLogicApiService gameLogicApiService;
    @Spy
    VariableDependencyService variableDependencyService;
    @Spy
    MarketService marketService;
    @Spy
    BattleService battleService;

    @Spy
    WarService warService;
    @Spy
    WarsApiService warsApiService;
    @Mock
    FleetService fleetService;
    @Mock
    ShipService shipService;
    @Mock
    FleetCoordinationService fleetCoordinationService;
    @Spy
    FleetApiService fleetApiService;
    @Spy
    FogOfWar fogOfWar;
    @Spy
    GameStatService gameStatService;


    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    CoolerBubbleComponent coolerBubbleComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    ClockComponent clockComponent;
    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;
    @InjectMocks
    public IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    public SitesComponent sitesComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;
    @InjectMocks
    EventComponent eventComponent;
    @InjectMocks
    public InGameController inGameController;
    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;
    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;
    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;
    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;
    @InjectMocks
    EmpireOverviewComponent empireOverviewComponent;
    @InjectMocks
    VariableService variableService;
    @InjectMocks
    AnnouncementsService announcementsService;
    @InjectMocks
    HelpComponent helpComponent;
    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;
    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;
    @InjectMocks
    PropertiesJobProgressComponent propertiesJobProgressComponent;
    @InjectMocks
    PropertiesJobProgressComponent siteJobProgress;
    @InjectMocks
    IslandClaimingComponent islandClaimingComponent;
    @InjectMocks
    TechnologyOverviewComponent technologyOverviewComponent;
    @InjectMocks
    BattleResultComponent battleResultComponent;

    @InjectMocks
    TechnologyCategoryComponent technologyCategoryComponent;
    @InjectMocks
    TechnologyResearchDetailsComponent technologyResearchDetailsComponent;

    @InjectMocks
    TechnologyEffectDetailsComponent technologyEffectDetailsComponent;

    @InjectMocks
    ResearchJobComponent researchJobComponent;
    @InjectMocks
    IslandUpgradesJobProgressComponent islandUpgradesJobProgressComponent;
    @InjectMocks
    MarketComponent marketComponent;
    @InjectMocks
    ContactsComponent contactsComponent;
    @InjectMocks
    ContactDetailsComponent contactDetailsComponent;
    @InjectMocks
    WarComponent warComponent;
    @InjectMocks
    FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    NewFleetComponent newFleetComponent;
    @InjectMocks
    ChangeFleetComponent changeFleetComponent;
    @InjectMocks
    IslandTravelComponent islandTravelComponent;
    @InjectMocks
    BlueprintsDetailsComponent blueprintsDetailsComponent;
    @InjectMocks
    GameResultPopup gameResultPopup;

    public void initializeComponents() {
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent = this.technologyCategoryComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.researchJobComponent = this.researchJobComponent;
        this.inGameController.technologiesComponent.technologyService = this.technologyService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyService = this.technologyService;

        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent = this.technologyResearchDetailsComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyEffectDetailsComponent = this.technologyEffectDetailsComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.resourcesService = this.resourcesService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.resourcesService.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyOverviewComponent = this.technologyOverviewComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent.tokenStorage = this.tokenStorage;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.presetsApiService = this.presetsApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.technologyService = this.technologyService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.empireApiService = this.empireApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.gameLogicApiService = this.gameLogicApiService;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.tokenStorage = this.tokenStorage;
        this.inGameController.technologiesComponent.technologyCategoryComponent.technologyResearchDetailsComponent.subscriber = this.subscriber;

        this.inGameController.technologiesComponent.technologyCategoryComponent.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.technologyService.subscriber = this.subscriber;
        this.inGameController.technologiesComponent.subscriber = this.subscriber;
        this.inGameController.fleetCoordinationService = this.fleetCoordinationService;

        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.eventService = this.eventService;
        this.inGameController.islandTravelComponent = this.islandTravelComponent;
        this.clockComponent.eventService = this.eventService;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameService.setGameStatus(gameStatus);
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.overviewUpgradeComponent = this.overviewUpgradeComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;
        this.inGameController.marketOverviewComponent = this.marketComponent;
        this.inGameController.marketOverviewComponent.variableService = this.variableService;
        this.coolerBubbleComponent.subscriber = this.subscriber;
        this.inGameController.coolerBubbleComponent = this.coolerBubbleComponent;

        this.overviewUpgradeComponent.jobProgressComponent = islandUpgradesJobProgressComponent;
        this.overviewUpgradeComponent.jobsService = this.jobsService;
        this.overviewUpgradeComponent.islandAttributes = this.islandAttributeStorage;

        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.overviewSitesComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.coolerBubbleComponent.announcementsService = this.announcementsService;
        this.inGameController.selectedIsland = new IslandComponent(this.islandsService);
        this.resourcesService.islandAttributes = islandAttributeStorage;
        this.resourcesService.tokenStorage = tokenStorage;
        this.resourcesService.empireService = empireService;
        this.marketService.empireApiService = empireApiService;
        this.inGameController.marketOverviewComponent.empireService = this.empireService;
        this.inGameController.selectedIsland.rudderImage = new ImageView();
        this.resourcesService.subscriber = subscriber;
        this.inGameController.overviewSitesComponent.buildingsComponent.islandAttributes = islandAttributeStorage;
        this.inGameController.selectedIsland.flagPane = new StackPane();
        this.variableDependencyService.variableService = this.variableService;

        this.inGameController.empireOverviewComponent = this.empireOverviewComponent;
        this.inGameController.variableService = this.variableService;
        this.inGameController.helpComponent = this.helpComponent;
        this.inGameController.variableService.inGameService.presetsApiService = this.presetsApiService;
        this.inGameController.lobbyService = this.lobbyService;
        this.inGameController.lobbyService.gameMembersApiService = this.gameMembersApiService;
        this.inGameController.jobsOverviewComponent = this.jobsOverviewComponent;
        this.inGameController.overviewSitesComponent.jobsComponent = this.islandOverviewJobsComponent;
        this.inGameController.overviewSitesComponent.jobsComponent.jobsService = this.jobsService;
        this.inGameController.clockComponent.timerService = this.timerService;
        this.inGameController.clockComponent.timerService.tokenStorage = this.tokenStorage;
        this.inGameController.clockComponent.timerService.gamesApiService = this.gamesApiService;
        this.inGameController.clockComponent.timerService.subscriber = this.subscriber;
        this.inGameController.buildingPropertiesComponent.propertiesJobProgressComponent = this.propertiesJobProgressComponent;
        this.inGameController.sitePropertiesComponent.siteJobProgress = this.siteJobProgress;
        this.inGameController.jobsService.tokenStorage = this.tokenStorage;
        this.inGameController.jobsService.jobsApiService = this.jobsApiService;
        this.inGameController.jobsService.subscriber = this.subscriber;
        this.inGameController.jobsService.eventListener = this.eventListener;
        this.inGameController.explanationService = this.explanationService;
        this.inGameController.explanationService.app = this.app;
        this.inGameController.overviewSitesComponent.buildingsComponent.imageCache = this.imageCache;
        this.inGameController.overviewSitesComponent.jobsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.detailsComponent.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService = this.variableService;
        this.inGameController.overviewUpgradeComponent.explanationService.variableService.technologyService.presetsApiService = this.presetsApiService;
        this.inGameController.islandClaimingComponent = this.islandClaimingComponent;
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameController.technologiesComponent = this.technologyOverviewComponent;
        this.inGameController.battleResultComponent = battleResultComponent;
        this.inGameController.technologiesComponent.technologyCategoryComponent = this.technologyCategoryComponent;
        this.technologyCategoryComponent.researchJobComponent = researchJobComponent;
        this.inGameController.gameResultPopup = this.gameResultPopup;

        this.inGameController.contextMenuButtons = new HBox();
        this.islandsService.tokenStorage = new TokenStorage();
        this.islandsService.gameSystemsService = gameSystemsApiService;
        this.islandsService.imageCache = this.imageCache;

        this.battleService.fleetService = this.fleetService;
        this.battleService.islandsService = this.islandsService;
        this.battleService.contactsService = this.contactsService;
        this.battleService.tokenStorage = this.tokenStorage;

        this.inGameController.fogOfWar.tokenStorage = this.tokenStorage;
        this.inGameController.fogOfWar.subscriber = this.subscriber;
        this.inGameController.fogOfWar.islandsService = this.islandsService;
        this.inGameController.fogOfWar.empireApiService = this.empireApiService;

        this.technologyService.eventListener = this.eventListener;

        this.inGameController.fleetManagerComponent = this.fleetManagerComponent;
        this.inGameController.fleetManagerComponent.blueprintsDetailsComponent = this.blueprintsDetailsComponent;
        this.inGameController.fleetManagerComponent.newFleetComponent = this.newFleetComponent;
        this.inGameController.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetCoordinationService.fleetService = this.fleetService;
        this.fleetCoordinationService.tokenStorage = this.tokenStorage;
        this.fleetService.tokenStorage = this.tokenStorage;
        this.fleetService.fleetApiService = this.fleetApiService;
        this.inGameController.contactsOverviewComponent = this.contactsComponent;
        this.inGameController.contactsOverviewComponent.warService = this.warService;
        this.inGameController.contactsOverviewComponent.warService.warsApiService = this.warsApiService;
        this.inGameController.contactsOverviewComponent.contactDetailsComponent = this.contactDetailsComponent;
        this.inGameController.contactsOverviewComponent.contactDetailsComponent.warComponent = this.warComponent;
        this.inGameController.warComponent = this.warComponent;
        this.inGameController.contactService.tokenStorage = this.tokenStorage;
        this.inGameController.contactService.islandsService = this.islandsService;
        this.inGameController.contactService.empireApiService = this.empireApiService;
        this.inGameController.contactService.subscriber = this.subscriber;
        this.inGameController.contactService.eventListener = this.eventListener;

        this.fleetService.subscriber = this.subscriber;
        this.fleetCoordinationService.subscriber = this.subscriber;
        this.technologyService.eventListener = this.eventListener;

        this.timerService.subscriber = this.subscriber;
        this.timerService.eventListener = this.eventListener;

        this.gameStatService.islandsService = this.islandsService;
        this.gameStatService.tokenStorage = this.tokenStorage;

        this.gameResultPopup.subscriber = new Subscriber();
        this.gameResultPopup.imageCache = this.imageCache;
        this.gameResultPopup.tokenStorage = this.tokenStorage;
        this.gameResultPopup.app = this.app;
        this.gameResultPopup.gamesApiService = this.gamesApiService;
        this.gameResultPopup.inGameService = this.inGameService;
    }

    public void clearStyleSheets() {
        this.storageOverviewComponent.getStylesheets().clear();
        this.pauseMenuComponent.getStylesheets().clear();
        this.clockComponent.getStylesheets().clear();
        this.eventComponent.getStylesheets().clear();
        this.storageOverviewComponent.getStylesheets().clear();
        this.overviewSitesComponent.getStylesheets().clear();
        this.overviewUpgradeComponent.getStylesheets().clear();
        this.buildingsComponent.getStylesheets().clear();
        this.sitesComponent.getStylesheets().clear();
        this.detailsComponent.getStylesheets().clear();
        this.deleteStructureComponent.getStylesheets().clear();
        this.sitePropertiesComponent.getStylesheets().clear();
        this.buildingsWindowComponent.getStylesheets().clear();
        this.buildingPropertiesComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.getStylesheets().clear();
        this.inGameController.overviewUpgradeComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.detailsComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.buildingsComponent.getStylesheets().clear();
        this.inGameController.overviewSitesComponent.sitesComponent.getStylesheets().clear();
        this.inGameController.islandClaimingComponent.getStylesheets().clear();
    }
}