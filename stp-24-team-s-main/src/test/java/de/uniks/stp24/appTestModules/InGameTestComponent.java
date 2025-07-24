package de.uniks.stp24.appTestModules;

import de.uniks.stp24.component.game.DistrictComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Provider;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

public class InGameTestComponent extends InGameTestInitializer {
    Button homeIsland;

    final Provider<DistrictComponent> districtComponentProvider = () -> {
        DistrictComponent districtComponent = new DistrictComponent();
        districtComponent.tokenStorage = tokenStorage;
        districtComponent.islandAttributeStorage = this.islandAttributeStorage;
        districtComponent.imageCache = this.imageCache;
        return districtComponent;
    };

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();

    final Map<String, Integer> siteSlots = Map.of("energy", 3, "city", 3, "mining", 4, "research", 4);
    final Map<String, Integer> sites = Map.of("energy", 2, "city", 3, "mining", 4, "research", 4);

    final IslandType myTestIsland = IslandType.valueOf("uninhabitable_0");
    final ArrayList<String> buildings = new ArrayList<>();
    final List<Island> islands = new ArrayList<>();

    final Map<String, Double> cost = Map.of("energy", 3.0, "fuel", 2.0);
    final Map<String, Double> upkeep = Map.of("energy", 3.0, "fuel", 8.0);

    final Map<String, Double> productionBuilding = Map.of("energy", 10.0, "fuel", 13.0);
    final Map<String, Double> productionSites = Map.of("energy", 13.0, "fuel", 12.0);
    final Map<String, Double> consumptionBuilding = Map.of("energy", 5.0, "fuel", 6.0);
    final Map<String, Double> consumptionSites = Map.of("energy", 20.0, "fuel", 19.0);


    final UpgradeStatus unexplored = new UpgradeStatus("unexplored", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus explored = new UpgradeStatus("explored", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus colonized = new UpgradeStatus("colonized", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus upgraded = new UpgradeStatus("upgraded", null, 0, 1, cost, upkeep, 1);
    final UpgradeStatus developed = new UpgradeStatus("developed", null, 0, 1, cost, upkeep, 1);

    final Map<String, Double> empireResourceStorage = new LinkedHashMap<>() {{
        put("energy", 100.0);
        put("fuel", 50.0);
    }};

    public final AggregateItemDto[] empireResources = new AggregateItemDto[]{
            new AggregateItemDto(
                    "energy",
                    100,
                    20
            ),
            new AggregateItemDto(
                    "fuel",
                    50,
                    -10
            ),
    };

    public final AggregateResultDto aggregateResult = new AggregateResultDto(
            0,
            empireResources
    );

    public final EmpireDto empireDto = new EmpireDto(
            null,
            null,
            "testEmpireID",
            "123456",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null,
            null,
            empireResourceStorage,
            null
    );

    public ReadEmpireDto readEmpireDto = new ReadEmpireDto(
            null,
            null,
            "testEmpireID",
            "123456",
            "testUserID",
            null,
            null,
            null,
            1,
            1,
            null
    );


    final BuildingAttributes buildingPreset1 = new BuildingAttributes(
            "testBuilding1",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final BuildingAttributes buildingPreset2 = new BuildingAttributes(
            "testBuilding2",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final BuildingAttributes buildingPreset3 = new BuildingAttributes(
            "testBuilding3",
            0,
            null,
            consumptionBuilding,
            productionBuilding
    );

    final DistrictAttributes districtPresets1 = new DistrictAttributes(
            "energy",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets2 = new DistrictAttributes(
            "city",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets3 = new DistrictAttributes(
            "mining",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final DistrictAttributes districtPresets4 = new DistrictAttributes(
            "research",
            0,
            null,
            null,
            consumptionSites,
            productionSites
    );

    final ArrayList<BuildingAttributes> buildingAttributes = new ArrayList<>();
    final ArrayList<DistrictAttributes> districtAttributes = new ArrayList<>();

    final SystemUpgrades systemUpgrades = new SystemUpgrades(unexplored, explored, colonized, upgraded, developed);

    public Island testIsland;

    SystemDto system = new SystemDto(
            "",
            "",
            "systemID",
            "testGameID",
            "agriculture",
            "name",
            siteSlots,
            sites,
            25,
            buildings,
            Upgrade.explored,
            20,
            null,
            50,
            50,
            "testEmpireID",
      100
    );

    Trait traitDto = new Trait("traitId", new EffectDto[]{new EffectDto("variable", 0.5, 1.3, 3)}, 3, new String[]{"conflicts"});



    public void initComponents(){
        initializeComponents();

        this.inGameController.subscriber = new Subscriber();

        this.islandAttributeStorage.systemUpgradeAttributes = systemUpgrades;
        this.islandAttributeStorage.empireDto = empireDto;
        this.inGameController.overviewSitesComponent.sitesComponent.districtComponentProvider = districtComponentProvider;

        doReturn("testUserID").when(this.tokenStorage).getUserId();
        doReturn("123456").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();
        doReturn(gameStatus).when(this.inGameService).getGameStatus();

        // Mock getEmpire
        doReturn(Observable.just(empireDto)).when(this.empireService).getEmpire(any(), any());
        doReturn(Observable.just(new Game("a", "a", "123456", "gameName", "gameOwner", 2, 1, true, 1, 1, null))).when(gamesApiService).getGame(any());
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.123456.empires.testEmpireID.updated"), eq(EmpireDto.class));

        // Mock getResourceAggregates
        doReturn(Observable.just(aggregateResult)).when(this.empireService).getResourceAggregates(any(), any());
        doReturn(Observable.just(new SystemDto[]{system})).when(this.gameSystemsApiService).getSystems(any());


        // Mock get Fleets and ships
        ArrayList<Fleets.ReadFleetDTO> fleets = new ArrayList<>(Collections.singleton(new Fleets.ReadFleetDTO("a", "a", "fleetID", "123456", "testEmpireID", "fleetName", "fleetLocation", 4, new HashMap<>(), new HashMap<>())));
        doNothing().when(this.fleetService).initializeShipListener();

        buildings.add("refinery");
        buildings.add("farm");
        buildings.add("mine");
        buildings.add("not Built");

        testIsland = new Island(
                "testEmpireID",
                1,
                50,
                50,
                myTestIsland,
                20,
                25,
                2,
                siteSlots,
                sites,
                buildings,
                "1",
                "explored",
                "TestIsland1",
          100
        );

        tokenStorage.setIsland(testIsland);

        system = new SystemDto(
                "",
                "",
                "systemID",
                "123456",
                "agriculture",
                "name",
                siteSlots,
                sites,
                25,
                buildings,
                Upgrade.explored,
                20,
                null,
                50,
                50,
                "testEmpireID",
          100
        );

        this.islandAttributeStorage.setIsland(testIsland);

        Map<String, Integer> variablesPresets = new HashMap<>();
        ArrayList<String> traits = new ArrayList<>();

        Empire empire = new Empire(
                "testEmpire",
                "justATest",
                "RED",
                0,
                2,
                traits,
                "uncharted_island0"

        );

        MemberDto member = new MemberDto(
                true,
                "testUser",
                empire,
                "123"
        );

        Map<String, ArrayList<String>> variablesEffect = new HashMap<>();
        ArrayList<Jobs.Job> jobList = new ArrayList<>();
        EffectSourceParentDto effectSourceParentDto = new EffectSourceParentDto(new EffectSourceDto[3]);

        doReturn(Observable.just(variablesPresets)).when(inGameService).getVariablesPresets();
        doReturn(Observable.just(effectSourceParentDto)).when(empireApiService).getEmpireEffect(any(), any());
        doReturn(Observable.just(new BuildingDto("a", 0, cost, productionBuilding, upkeep))).when(resourcesService).getResourcesBuilding(any());

        buildingAttributes.add(buildingPreset1);
        buildingAttributes.add(buildingPreset2);
        buildingAttributes.add(buildingPreset3);

        districtAttributes.add(districtPresets1);
        districtAttributes.add(districtPresets2);
        districtAttributes.add(districtPresets3);
        districtAttributes.add(districtPresets4);

        doReturn(districtAttributes).when(variableDependencyService).createVariableDependencyDistricts();
        islandAttributeStorage.setDistrictAttributes();

        this.islandAttributeStorage.buildingsAttributes = this.buildingAttributes;
        this.islandAttributeStorage.districtAttributes = this.districtAttributes;
        this.islandsService.isles = islands;

        doReturn(FXCollections.observableArrayList()).when(jobsService).getObservableListForSystem(any());
        doReturn(Observable.empty()).when(jobsService).beginJob(any());
        doReturn(testIsland).when(tokenStorage).getIsland();
        doReturn(Observable.empty()).when(gameSystemsApiService).updateIsland(any(), any(), any());
        doReturn(FXCollections.observableArrayList()).when(announcementsService).getAnnouncements();
        doReturn(Observable.empty()).when(marketService).getSeasonalTrades(any(), any());

        doReturn(Observable.empty()).when(empireApiService).getPrivate(any(), any());

        this.inGameController.buildingPropertiesComponent.certainBuilding = buildingPreset1;
        doNothing().when(contactsService).loadContactsData();
        doNothing().when(contactsService).createWarListener();
//        doNothing().when(contactsComponent).init();
        doReturn(Observable.just(new ArrayList<WarDto>())).when(warService).getWars(any(),any());

//        ArrayList<Fleets.ReadFleetDTO> fleets = new ArrayList<>(Collections.singleton(new Fleets.ReadFleetDTO("a", "a", "fleetID", "123456", "testEmpireID", "fleetName", "fleetLocation", new HashMap<>() ,new HashMap<>())));
//        doReturn(Observable.just(fleets)).when(this.fleetApiService).getGameFleets("123456");//,true);
        doNothing().when(this.fleetService).initializeFleetListeners();
        doNothing().when(this.fleetService).onFleetCreated(any());
        doNothing().when(this.fleetService).loadGameFleets();
//        doNothing().when(this.fleetService).initializeShipListener();


        this.app.show(this.inGameController);
        clearStyleSheets();

    }

    protected void createMap() {
        homeIsland = new Button();
        homeIsland.setLayoutX(500);
        homeIsland.setLayoutY(500);
        homeIsland.setPrefWidth(50);
        homeIsland.setPrefHeight(50);
        homeIsland.setId("homeIsland");
        homeIsland.setOnAction(this::openIslandOverview);
        Platform.runLater(() -> {
            inGameController.mapGrid.getChildren().add(homeIsland);
            waitForFxEvents();
        });
        waitForFxEvents();
    }

    protected void openIslandOverview(ActionEvent actionEvent) {
        this.inGameController.showOverview();
    }

}
