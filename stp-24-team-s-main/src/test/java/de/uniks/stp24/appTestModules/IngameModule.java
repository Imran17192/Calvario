package de.uniks.stp24.appTestModules;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.*;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Fleets.ReadFleetDTO;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;

import java.util.*;

import static org.mockito.Mockito.*;

/**
 * An essential module needed to properly load the InGame from joinGameHelper.
 * Contains all mocks and variables that are required for the successful game mock.
 * Other modules can inherit from it and override provided methods to resolve the unnecessary stubbing issue
 */
public class IngameModule extends LobbyTestLoader {

    protected final String
            GAME_ID = "FFFFFFFFFFFFFFFFFFF",
            EMPIRE_ID = "testEmpireID",
            ENEMY_EMPIRE_ID = "enemyEmpireID",
            USER_ID = "testUserID",
            ENEMY_ID = "enemyID";

    protected final Map<String, Double> DEV_RESOURCES = Map.of(
            "credits", 1000000000.0,
            "energy", 1000000000.0,
            "minerals", 1000000000.0,
            "food", 1000000000.0,
            "fuel", 1000000000.0,
            "research", 1000000000.0,
            "alloys", 1000000000.0,
            "consumer_goods", 1000000000.0
    );

    protected final BuildingDto BUILDING_DTO = new BuildingDto("refinery", 3.0,
            Map.of("minerals", 100.0),
            Map.of("minerals", 10.0, "energy", 15.0),
            Map.of("fuel", 10.0));

    protected final DistrictAttributes DISTRICT_ATTRIBUTES = new DistrictAttributes("energy", 3.0,
            new HashMap<>(),
            Map.of("minerals", 75.0),
            Map.of("minerals", 2.0),
            Map.of("energy", 30.0));

    protected final String[] JOB_EVENT_PATHS = new String[]{
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.jobClaimingID_1.",
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.jobClaimingID_2.",
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.jobBuildingID.",
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.jobSiteID.",
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.travelJobID_1.",
            "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.travelJobID_2."
    };

    protected final String JOB_EVENT_PATH = "games.FFFFFFFFFFFFFFFFFFF.empires.testEmpireID.jobs.*.";

    protected final GameStatus GAME_STATUS = new GameStatus();

    protected final Game GAME = new Game("0", "0", GAME_ID, "TestGameName", USER_ID,
            1, 1, true, 0, 0, null);

    protected final UpdateGameResultDto RESULT_DTO = new UpdateGameResultDto(GAME.createdAt(), GAME.updatedAt(),
            GAME_ID, GAME.name(), GAME.owner(), GAME.started(), GAME.speed(), GAME.period(), GAME.settings());

    protected final Empire EMPIRE = new Empire("TestEmpire", "This is a test empire",
            "#FF00FF", 3, 4, new ArrayList<>(), "regular");
    protected final Empire EMPIRE2 = new Empire("TestEmpire", "This is a test empire",
            "#FF00FF", 3, 4, new ArrayList<>(List.of("__dev__")), "regular");
    protected final Empire ENEMY_EMPIRE = new Empire("EnemyEmpire", "This is a test empire",
            "#50C878", 3, 4, new ArrayList<>(), "regular");

    protected final EmpireDto EMPIRE_DTO = new EmpireDto("0", "0", EMPIRE_ID, GAME_ID, USER_ID,
            EMPIRE.name(), EMPIRE.description(), EMPIRE.color(), EMPIRE.flag(), EMPIRE.portrait(),
            EMPIRE.homeSystem(), EMPIRE.traits().toArray(new String[]{}), DEV_RESOURCES, new String[]{});

    protected final ReadEmpireDto READ_EMPIRE_DTO = new ReadEmpireDto(EMPIRE_DTO.createdAt(), EMPIRE_DTO.updatedAt(),
            EMPIRE_ID, GAME_ID, USER_ID, EMPIRE.name(), EMPIRE.description(), EMPIRE.color(), EMPIRE.flag(), EMPIRE.portrait(),
            EMPIRE.homeSystem());

    protected final ReadEmpireDto READ_ENEMY_EMPIRE_DTO = new ReadEmpireDto(EMPIRE_DTO.createdAt(), EMPIRE_DTO.updatedAt(),
            ENEMY_EMPIRE_ID, GAME_ID, USER_ID, ENEMY_EMPIRE.name(), ENEMY_EMPIRE.description(), ENEMY_EMPIRE.color(),
            ENEMY_EMPIRE.flag(), ENEMY_EMPIRE.portrait(), ENEMY_EMPIRE.homeSystem());

    protected final Trait[] TRAITS = new Trait[]{
            new Trait("__dev__", new EffectDto[]{
                    new EffectDto("resources.credits.starting", 0, 0, 1000000000),
                    new EffectDto("resources.energy.starting", 0, 0, 1000000000),
                    new EffectDto("resources.minerals.starting", 0, 0, 1000000000),
                    new EffectDto("resources.food.starting", 0, 0, 1000000000),
                    new EffectDto("resources.fuel.starting", 0, 0, 1000000000),
                    new EffectDto("resources.research.starting", 0, 0, 1000000000),
                    new EffectDto("resources.alloys.starting", 0, 0, 1000000000),
                    new EffectDto("resources.consumer_goods.starting", 0, 0, 1000000000)
            }, 0, null),
            new Trait("prepared",
                    new EffectDto[]{new EffectDto("resources.energy.starting", 0, 0, 200)},
                    1, new String[]{"unprepared"}),
            new Trait("unprepared",
                    new EffectDto[]{new EffectDto("resources.energy.starting", 0, 0, -200)},
                    1, new String[]{"prepared"})
    };

    protected final Map<String, Integer> VARIABLE_PRESETS = Map.of(
            "districts.energy.cost.minerals", 75,
            "districts.energy.upkeep.minerals", 2,
            "districts.energy.production.energy", 30,
            "buildings.refinery.cost.minerals", 100,
            "buildings.refinery.upkeep.minerals", 10,
            "buildings.refinery.upkeep.energy", 15,
            "buildings.refinery.production.fuel", 10,
            "systems.colonized.cost.energy", 100
    );

    protected final Map<String, Double> MARKET_VARIABLES = Map.of(
            "resources.energy.credit_value", 1.0,
            "resources.minerals.credit_value", 1.0,
            "resources.food.credit_value", 1.0,
            "resources.fuel.credit_value", 5.0,
            "resources.alloys.credit_value", 8.0,
            "resources.consumer_goods.credit_value", 6.0,
            "empire.market.fee", 0.3
    );

    protected final ArrayList<ExplainedVariableDTO> VARIABLE_EXPLANATIONS = new ArrayList<>(List.of(
            new ExplainedVariableDTO("districts.energy.cost.minerals", 75, new ArrayList<>(), 75),
            new ExplainedVariableDTO("districts.energy.upkeep.minerals", 2, new ArrayList<>(), 2),
            new ExplainedVariableDTO("districts.energy.production.energy", 30, new ArrayList<>(), 30),
            new ExplainedVariableDTO("buildings.refinery.cost.minerals", 100, new ArrayList<>(), 100),
            new ExplainedVariableDTO("buildings.refinery.upkeep.minerals", 10, new ArrayList<>(), 10),
            new ExplainedVariableDTO("buildings.refinery.upkeep.energy", 15, new ArrayList<>(), 15),
            new ExplainedVariableDTO("buildings.refinery.production.fuel", 10, new ArrayList<>(), 10),
            new ExplainedVariableDTO("systems.colonized.cost.energy", 100, new ArrayList<>(), 100),
            new ExplainedVariableDTO("resources.energy.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.minerals.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.food.credit_value", 1.0, new ArrayList<>(), 1.0),
            new ExplainedVariableDTO("resources.fuel.credit_value", 5.0, new ArrayList<>(), 5.0),
            new ExplainedVariableDTO("resources.alloys.credit_value", 8.0, new ArrayList<>(), 8.0),
            new ExplainedVariableDTO("resources.consumer_goods.credit_value", 6.0, new ArrayList<>(), 6.0),
            new ExplainedVariableDTO("resources.consumer_goods.empire.market.fee", 0.3, new ArrayList<>(), 0.3)));

    protected SystemDto[] GAME_SYSTEMS = new SystemDto[]{
            new SystemDto("0", "0", "islandID_1", GAME_ID, "regular", "TestIslandOne",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of("energy",
                    "shipyard", "mine", "research")), Upgrade.colonized, 13, Map.of("islandID_2", 20,
                    "islandID_3", 20, "enemyIslandID", 20), 50, 50, EMPIRE_ID, 0),

            new SystemDto("0", "0", "islandID_2", GAME_ID, "regular", "TestIslandTwo",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.unexplored, 13, Map.of("islandID_1", 20, "islandID_3", 20, "islandID_4", 20), 63, 52, null, 100),

            new SystemDto("0", "0", "islandID_3", GAME_ID, "regular", "TestIslandThree",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.explored, 13, Map.of("islandID_1", 20, "islandID_2", 20), 55, 62, null, 100),

            new SystemDto("0", "0", "islandID_4", GAME_ID, "regular", "TestIslandFour",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.explored, 13, Map.of("islandID_2", 20, "islandID_5", 20), 74, 45, null, 100),

            new SystemDto("0", "0", "islandID_5", GAME_ID, "regular", "TestIslandFive",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.explored, 13, Map.of("islandID_4", 20), 85, 62, null, 0),

            new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "EnemyIsland",
                    Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                    Upgrade.colonized, 13, Map.of("islandID_1", 20), 35, 60, ENEMY_EMPIRE_ID, 0)

    };

    protected final CreateSystemsDto CREATE_SYSTEM_DTO = new CreateSystemsDto(
            "0", "0", "islandID_1", GAME_ID, IslandType.regular, "TestIslandOne",
            Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                    "shipyard", "mine", "research")), "colonized", 13, Map.of("islandID_2", 3,
            "islandID_3", 3), 50, 50, EMPIRE_ID);

    protected final Job[] JOBS = new Job[]{
            new Job("0", "0",
                    "jobClaimingID_1", 0, 3, GAME_ID, EMPIRE_ID, "islandID_2", 0,
                    "upgrade", null, null, null, "", "", new LinkedList<>(), new HashMap<>(), null),
            new Job("0", "0",
                    "jobClaimingID_2", 0, 12, GAME_ID, EMPIRE_ID, "islandID_3", 0,
                    "upgrade", null, null, null, "", "", new LinkedList<>(), new HashMap<>(), null),
            new Job("0", "0",
                    "jobBuildingID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "building", "refinery", null, null, "", "", new LinkedList<>(), new HashMap<>(), null),
            new Job("0", "0",
                    "jobSiteID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "district", null, "energy", null, "", "", new LinkedList<>(), new HashMap<>(), null),
            new Job("0", "0",
                    "jobTechnologyID", 0, 12, GAME_ID, EMPIRE_ID, "islandID_1", 0,
                    "technology", null, null, "society", "", "", new LinkedList<>(), new HashMap<>(), null)
    };

    protected final Job[] TRAVEL_JOBS = new Job[]{
            new Job("0", "0",
                    "travelJobID_1", 0, 10, GAME_ID, EMPIRE_ID, null, 0,
                    "travel", null, null, null, "testFleetID_1", null,
                    new LinkedList<>(List.of("islandID_1", "islandID_2", "islandID_4", "islandID_5")), new HashMap<>(), null),
            new Job("0", "0",
                    "startedTravelJobID_1", 1, 4, GAME_ID, EMPIRE_ID, null, 0,
                    "travel", null, null, null, "testFleetID_2", null,
                    new LinkedList<>(List.of("islandID_1", "islandID_2", "islandID_4", "islandID_5")), new HashMap<>(), null)
    };

    protected ArrayList<ReadFleetDTO> FLEET_DTOS = new ArrayList<>(List.of(
            new ReadFleetDTO("a", "a",
                    "testFleetID_1", GAME_ID, EMPIRE_ID, "fleetName", GAME_SYSTEMS[0]._id(),
                    4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a",
                    "testFleetID_2", GAME_ID, EMPIRE_ID, "fleetName", GAME_SYSTEMS[0]._id(),
                    4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a",
                    "testFleetID_5", GAME_ID, EMPIRE_ID, "fleetName", GAME_SYSTEMS[4]._id(),
                    4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a",
                    "enemyFleetID_1", GAME_ID, ENEMY_EMPIRE_ID, "fleetName", GAME_SYSTEMS[2]._id(),
                    4, new HashMap<>(), new HashMap<>())
    ));

    protected final Fleet[] FLEETS = new Fleet[]{
            new Fleet("a","a",
                  "testFleetID_1",GAME_ID, EMPIRE_ID, "fleetName",GAME_SYSTEMS[0]._id(),
                    4,new HashMap<>(),new HashMap<>(), new HashMap<>(),null),
            new Fleet("a","a",
                  "testFleetID_2",GAME_ID, EMPIRE_ID, "fleetName",GAME_SYSTEMS[0]._id(),
                    4,new HashMap<>(),new HashMap<>(), new HashMap<>(),null),
            new Fleet("a","a",
                    "enemyFleetID_1",GAME_ID, ENEMY_EMPIRE_ID, "fleetName", GAME_SYSTEMS[1]._id(),
                    4,new HashMap<>(),new HashMap<>(), new HashMap<>(),null),
    };

    protected final Fleet NEW_FLEET = new Fleet("a", "a",
            "testFleetID_3", GAME_ID, EMPIRE_ID, "fleetName", GAME_SYSTEMS[3]._id(),
            4, new HashMap<>(), new HashMap<>(), new HashMap<>(), null);

    protected final ReadShipDTO[] FLEET_SHIPS_1 = new ReadShipDTO[]{
            new ReadShipDTO("a", "b", "testShipID1", GAME_ID, EMPIRE_ID, "testFleetID_1",
                    "explorer", 10, 10, new HashMap<>())
    };

    protected final ReadShipDTO[] FLEET_SHIPS_2 = new ReadShipDTO[]{
            new ReadShipDTO("a", "b", "testShipID2", GAME_ID, EMPIRE_ID, "testFleetID_2",
                    "colonizer", 10, 10, new HashMap<>())
    };

    protected final ReadShipDTO[] FLEET_SHIPS_3 = new ReadShipDTO[]{
            new ReadShipDTO("a", "b", "testShipID3", GAME_ID, EMPIRE_ID, "testFleetID_3",
                    "colonizer", 10, 10, new HashMap<>())
    };

    protected final ReadShipDTO[] FLEET_SHIPS_5 = new ReadShipDTO[]{
            new ReadShipDTO("a", "b", "testShipID3", GAME_ID, EMPIRE_ID, "testFleetID_5",
                    "colonizer", 10, 10, new HashMap<>())
    };

    protected final TechnologyExtended TECHNOLOGY = new TechnologyExtended("society", new Effect[]{
        new Effect("technologies.society.cost_multiplier", 0, 0.9, 0)},
            new String[]{"society"}, 2, null, null);

    protected final ExplainedVariableDTO EXPLAINED_VARIABLE = new ExplainedVariableDTO(
            "technologies.society.cost_multiplier", 0.9, new ArrayList<>(), 0.9);

    protected final ArrayList<TechnologyExtended> TECHNOLOGIES = new ArrayList<>(List.of(TECHNOLOGY));

    protected final ArrayList<ExplainedVariableDTO> EXPLAINED_VARIABLES = new ArrayList<>(List.of(EXPLAINED_VARIABLE));

    protected final UpdateEmpireMarketDto MARKET_UPDATE_DTO = new UpdateEmpireMarketDto(
            DEV_RESOURCES, null, null, null);

    protected final AggregateResultDto RESOURCE_AGGREGATES = new AggregateResultDto(-437, new AggregateItemDto[]{
            new AggregateItemDto("resources.credits.periodic", 1, -112),
            new AggregateItemDto("resources.population.periodic", 1, 0),
            new AggregateItemDto("resources.energy.periodic", 1, -122),
            new AggregateItemDto("resources.minerals.periodic", 1, -99),
            new AggregateItemDto("resources.food.periodic", 1, -156),
            new AggregateItemDto("resources.fuel.periodic", 1, 14),
            new AggregateItemDto("resources.research.periodic", 1, 10),
            new AggregateItemDto("resources.alloys.periodic", 1, -1),
            new AggregateItemDto("resources.consumer_goods.periodic", 1, 29),
    });

    protected final EffectSourceParentDto SOURCE_PARENT_DTO = new EffectSourceParentDto(new EffectSourceDto[]{});

    protected final MemberDto MEMBER_DTO = new MemberDto(true, USER_ID, EMPIRE, "password");
    protected final MemberDto MEMBER_DTO2 = new MemberDto(true, USER_ID, EMPIRE2, "password");
    protected final MemberDto ENEMY_MEMBER_DTO = new MemberDto(true, ENEMY_ID, ENEMY_EMPIRE, "password");

    protected final Subject<Event<MemberDto>> MEMBER_DTO_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Game>> GAME_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<SystemDto>> SYSTEMDTO_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<EmpireDto>> EMPIRE_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Job>> JOB_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Fleet>> FLEET_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Ship>> SHIP_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<WarDto>> WAR_SUBJECT = BehaviorSubject.create();

    protected int gameTicks = 0;

    protected final AggregateResultDto HEALTH_DEF_DTO = new AggregateResultDto(200,null);
    protected final List<WarDto> EMPTY_WARDTO_LIST = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.reassignData();
        this.initializeApiMocks();
        this.initializeEventListenerMocks();
        this.loadUnloadableData();
    }

    protected void reassignData() {}

    protected void initializeEventListenerMocks() {
        doReturn(GAME_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".updated", Game.class);
        doReturn(GAME_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".ticked", Game.class);
        doReturn(EMPIRE_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".empires." + EMPIRE_ID + ".updated", EmpireDto.class);
        doReturn(SYSTEMDTO_SUBJECT).when(this.eventListener)
                .listen(String.format("games.%s.systems.%s.updated", GAME_ID, "*"),  SystemDto.class);
        doReturn(FLEET_SUBJECT).when(this.eventListener)
                .listen(String.format("games.%s.fleets.*.*", GAME_ID), Fleet.class);
        doReturn(SHIP_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".fleets.*.ships.*.*", Ship.class);

        when(this.eventListener.listen(JOB_EVENT_PATH + "*", Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen("games." + GAME_ID + ".wars.*.*", WarDto.class)).thenReturn(WAR_SUBJECT);

    }

    protected void initializeApiMocks() {
        when(this.tokenStorage.getEmpireId()).thenReturn(this.EMPIRE_ID);
        when(this.tokenStorage.getGameId()).thenReturn(GAME_ID);
        when(this.tokenStorage.getUserId()).thenReturn(USER_ID);

        when(this.inGameService.getGameStatus()).thenReturn(GAME_STATUS);

        when(this.gamesApiService.getGame(any())).thenReturn(Observable.just(GAME));

        when(this.empireApiService.getSeasonalTrades(any(), any())).thenReturn(Observable.just(new SeasonalTradeDto(new HashMap<>())));

        when(this.presetsApiService.getVariablesPresets()).thenReturn(Observable.just(VARIABLE_PRESETS));

        when(this.gameLogicApiService.getVariablesExplanations(any(), any())).thenReturn(Observable.just(VARIABLE_EXPLANATIONS));

        when(this.gameMembersApiService.getMembers(any())).thenReturn(Observable.just(new MemberDto[]{MEMBER_DTO2, ENEMY_MEMBER_DTO}));
        when(this.gameSystemsApiService.getSystems(any())).thenReturn(Observable.just(GAME_SYSTEMS));

        when(this.empireApiService.getSeasonalTrades(any(), any())).thenReturn(Observable.just(new SeasonalTradeDto(null)));
        when(this.empireApiService.getResourceAggregates(any(), any())).thenReturn(Observable.just(RESOURCE_AGGREGATES));
        when(this.empireApiService.getEmpires(any())).thenReturn(Observable.just(new ReadEmpireDto[]{READ_EMPIRE_DTO, READ_ENEMY_EMPIRE_DTO}));
        when(this.empireApiService.getEmpireEffect(any(), any())).thenReturn(Observable.just(SOURCE_PARENT_DTO));
        when(this.empireApiService.getEmpire(any(), eq(EMPIRE_ID))).thenReturn(Observable.just(EMPIRE_DTO));

        when(this.fleetApiService.getGameFleets(any(), eq(true))).thenReturn(Observable.just(FLEET_DTOS));

        doAnswer(inv -> this.app.show(this.gangCreationController)).when(this.app).show(eq("/creation"), any());
        doAnswer(inv -> this.app.show(this.inGameController)).when(this.app).show(eq("/ingame"), any());
        doAnswer(inv -> this.app.show(this.lobbyController)).when(this.app).show(eq("/lobby"), any());

        when(this.warService.getWars(any(),any())).thenReturn(Observable.just(EMPTY_WARDTO_LIST));
        when(this.empireApiService.getPrivate(any(),any()))
                .thenReturn(Observable.just(new EmpirePrivate(new HashMap<>())));
    }

    protected void loadUnloadableData() {
        this.islandAttributeStorage.systemUpgradeAttributes = new SystemUpgrades(
                null, null, new UpgradeStatus("0","upgraded", 1, 0, Map.of("energy", 100.0),
                Map.of("energy", 100.0), 0), null, null);
        this.islandAttributeStorage.buildingsAttributes = new ArrayList<>(List.of(new BuildingAttributes(
                BUILDING_DTO.id(), BUILDING_DTO.build_time(), BUILDING_DTO.cost(), BUILDING_DTO.upkeep(),
                BUILDING_DTO.production())));
        this.islandAttributeStorage.districtAttributes = new ArrayList<>(List.of(DISTRICT_ATTRIBUTES));
        this.shipService.shipSpeeds = Map.of("explorer", 10.0, "colonizer", 2.0);
    }



    protected Event<Game> tickGame(int speed) {
        return new Event<>("games." + GAME_ID + ".ticked",
                new Game("0", "0", GAME_ID, "TestGameName", USER_ID,
                1, 1, true, speed, ++this.gameTicks, null));
    }

    protected Event<Job> progressJob(String path, Job job, int progress) {
        return new Event<>(path,
                new Job(job.createdAt(), job.updatedAt(), job._id(), progress, job.total(), job.game(),
                        job.empire(), job.system(), job.priority(), job.type(), job.building(), job.district(),
                        job.technology(), job.fleet(), job.ship(), job.path(), job.cost(), job.result()));
    }
}
