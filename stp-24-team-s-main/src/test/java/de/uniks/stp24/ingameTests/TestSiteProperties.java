package de.uniks.stp24.ingameTests;

import de.uniks.stp24.ControllerTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)

public class TestSiteProperties extends ControllerTest {
    /*
    @Spy
    GamesApiService gamesApiService;

    @Spy
    PresetsApiService presetsApiService;


    @Spy
    GameStatus gameStatus;
    @Spy
    InGameService inGameService;

    @Spy
    EventService eventService;
    @Spy
    TimerService timerService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    LanguageService languageService;
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;

    @Spy
    IslandAttributeStorage islandAttributeStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EmpireService empireService;
    @Spy
    public ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ROOT);

    @InjectMocks
    PauseMenuComponent pauseMenuComponent;
    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;
    @InjectMocks
    ClockComponent clockComponent;

    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;

    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;

    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;

    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;

    @InjectMocks
    OverviewSitesComponent overviewSitesComponent;

    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;

    @InjectMocks
    InGameController inGameController;

    @InjectMocks
    BuildingsComponent buildingsComponent;

    @InjectMocks
    SitesComponent sitesComponent;

    @InjectMocks
    DetailsComponent detailsComponent;

    @InjectMocks
    EventComponent eventComponent;




    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = new HashMap<>();
    Map<String, Integer> sites = new HashMap<>();

    Map<String, String> sitesPath = new HashMap<>();

    Map<String, Integer> links = new HashMap<>();
    ArrayList<String> buildings = new ArrayList<>();

/*
    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.clockComponent.timerService = this.timerService;
        this.clockComponent.subscriber = this.subscriber;
        this.islandsService.app = this.app;
        this.inGameController.pauseMenuComponent = this.pauseMenuComponent;
        this.inGameController.settingsComponent = this.settingsComponent;
        this.inGameController.clockComponent = this.clockComponent;
        this.inGameController.overviewSitesComponent = this.overviewSitesComponent;
        this.inGameController.storageOverviewComponent = this.storageOverviewComponent;
        this.inGameController.buildingPropertiesComponent = this.buildingPropertiesComponent;
        this.inGameController.buildingsWindowComponent = this.buildingsWindowComponent;
        this.inGameController.sitePropertiesComponent = this.sitePropertiesComponent;
        this.inGameController.deleteStructureComponent = this.deleteStructureComponent;
        this.inGameController.islandAttributes = this.islandAttributeStorage;
        this.inGameController.overviewSitesComponent.sitesComponent = this.sitesComponent;
        this.inGameController.overviewSitesComponent.buildingsComponent = this.buildingsComponent;
        this.inGameController.overviewSitesComponent.detailsComponent = this.detailsComponent;
        this.inGameController.overviewUpgradeComponent= this.overviewUpgradeComponent;
        this.inGameController.eventComponent= this.eventComponent;
        this.inGameService.setGameStatus(gameStatus);
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();


        Map<String, Integer> resources1 = Map.of("energy",3);
        Map<String, Integer> resources2 = Map.of("energy",3, "population", 4);
        Map<String, Integer> resources3 = Map.of("energy",5, "population", 6);
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");

        UpgradeStatus upgradeStatus = new UpgradeStatus("test", 20, production, consumption, 20);

        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();

        doReturn(Observable.just(new SiteDto("a",chance, required,production, consumption))).when(resourcesService).getResourcesSite(any());
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        doReturn(Observable.just(new Game("a","a","testGameID", "gameName", "gameOwner", true,1,1,null ))).when(gamesApiService).getGame(any());
        doReturn(Observable.just(new SystemUpgrades(upgradeStatus,upgradeStatus, upgradeStatus, upgradeStatus, upgradeStatus ))).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(new ArrayList<BuildingPresets>())).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(new ArrayList<DistrictPresets>())).when(inGameService).loadDistrictPresets();

        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));
        buildings.add("mine");

        this.app.show(this.inGameController);
        eventComponent.getStylesheets().clear();
        storageOverviewComponent.getStylesheets().clear();
        clockComponent.getStylesheets().clear();
        pauseMenuComponent.getStylesheets().clear();
        settingsComponent.getStylesheets().clear();
        overviewSitesComponent.getStylesheets().clear();
        overviewUpgradeComponent.getStylesheets().clear();
        sitePropertiesComponent.getStylesheets().clear();
        buildingsWindowComponent.getStylesheets().clear();
        buildingPropertiesComponent.getStylesheets().clear();
        deleteStructureComponent.getStylesheets().clear();
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showSiteOverview();
            waitForFxEvents();
        });
        buildingPropertiesComponent.setVisible(false);
        buildingsWindowComponent.setVisible(false);
    }

    @Test
    public void buildSite(){
        waitForFxEvents();
        sitePropertiesComponent.setVisible(true);
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).buildSite(any(), any(),any());
        doReturn(new Island(island.owner(),1, island.posX(), island.posY(), island.type(), island.crewCapacity(),
                island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings(), island.id(), "explored")).when(islandsService).updateIsland(any());
        clickOn("#buildSiteButton");

        verify(this.resourcesService, times(1)).buildSite(any(), any(), any());
    }
    @Test
    public void destroySite(){
        Platform.runLater(() -> {
            inGameController.handleDeleteStructure("mining");
            waitForFxEvents();
        });
        waitForFxEvents();
        sites.put("mining", 1);
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");
        doReturn(island).when(this.tokenStorage).getIsland();
        doReturn(Observable.just(new SystemDto("", "", "testID2", "testGame", "testType",
                "", siteSlots, sites, 20, buildings, Upgrade.explored, 20, links, 500.0, 500.0,
                "testOwner"))).when(resourcesService).destroySite(any(), any(),any());
        doReturn(new Island(island.owner(),1, island.posX(), island.posY(), island.type(), island.crewCapacity(),
                island.resourceCapacity(), island.upgradeLevel(), island.sitesSlots(),
                island.sites(), island.buildings(), island.id(), "explored")).when(islandsService).updateIsland(any());
        
        clickOn("#confirmButton");
        waitForFxEvents();
        verify(this.resourcesService, times(1)).destroySite(any(), any(), any());
    }

 */
}
