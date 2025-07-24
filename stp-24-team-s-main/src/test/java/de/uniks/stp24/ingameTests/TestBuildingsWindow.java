package de.uniks.stp24.ingameTests;

import de.uniks.stp24.ControllerTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TestBuildingsWindow extends ControllerTest {
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
    OverviewSitesComponent overviewSitesComponent;
    @InjectMocks
    OverviewUpgradeComponent overviewUpgradeComponent;

    @InjectMocks
    BuildingPropertiesComponent buildingPropertiesComponent;

    @InjectMocks
    SitePropertiesComponent sitePropertiesComponent;

    @InjectMocks
    BuildingsWindowComponent buildingsWindowComponent;

    @InjectMocks
    DeleteStructureComponent deleteStructureComponent;

    @InjectMocks
    EventComponent eventComponent;

    @InjectMocks
    IslandAttributeStorage islandAttributeStorage;
    @InjectMocks
    DetailsComponent detailsComponent;
    @InjectMocks
    SitesComponent sitesComponent;
    @InjectMocks
    BuildingsComponent buildingsComponent;

    @InjectMocks
    InGameController inGameController;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    Map<String, Integer> siteSlots = new HashMap<>();
    Map<String, Integer> sites = new HashMap<>();
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
        this.inGameController.eventComponent = this.eventComponent;
        this.inGameService.setGameStatus(gameStatus);
        this.inGameService.presetsApiService = this.presetsApiService;
        Map<String , Integer> chance = new HashMap<>();
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();

        Map<String, Integer> resources1 = Map.of("energy",3);
        Map<String, Integer> resources2 = Map.of("energy",3, "population", 4);
        Map<String, Integer> resources3 = Map.of("energy",5, "population", 6);

        UpgradeStatus upgradeStatus = new UpgradeStatus("test", 20, production, consumption, 20);
        Island island = new Island("testOwner", 1, 500.0, 500.0, IslandType.mining,
                20, 20, 1, siteSlots, sites, buildings, "testID", "explored");

        // Mock TokenStorage
        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(island).when(this.tokenStorage).getIsland();

        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        doReturn(Observable.just(new Game("a","a","testGameID", "gameName", "gameOwner", true,1,1,null ))).when(gamesApiService).getGame(any());

        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        doReturn(Observable.just(new SystemUpgrades(upgradeStatus,upgradeStatus, upgradeStatus, upgradeStatus, upgradeStatus ))).when(inGameService).loadUpgradePresets();
        doReturn(Observable.just(new ArrayList<BuildingPresets>())).when(inGameService).loadBuildingPresets();
        doReturn(Observable.just(new ArrayList<DistrictPresets>())).when(inGameService).loadDistrictPresets();


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
        sitePropertiesComponent.setVisible(false);
        waitForFxEvents();
        Platform.runLater(() -> {
            inGameController.showBuildingWindow();
            waitForFxEvents();
        });
    }

    @Test
    public void buyExchange(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingExchange");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyPowerPlant(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingPowerPlant");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyMine(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingMine");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyFarm(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingFarm");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyResearchLab(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingResearchLab");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyFoundry(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingFoundry");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyFactory(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingFactory");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }

    @Test
    public void buyRefinery(){
        Map<String , Integer> required = new HashMap<>();
        Map<String, Integer> production = new HashMap<>();
        Map<String, Integer> consumption = new HashMap<>();
        doReturn(Observable.just(new BuildingDto("a",required,production, consumption))).when(resourcesService).getResourcesBuilding(any());
        waitForFxEvents();
        clickOn("#buildingRefinery");
        waitForFxEvents();
        assertTrue(buildingPropertiesComponent.isVisible());

    }





     */


}
