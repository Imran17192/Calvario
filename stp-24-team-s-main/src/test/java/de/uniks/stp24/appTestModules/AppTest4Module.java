package de.uniks.stp24.appTestModules;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Ships.ReadShipDTO;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.model.Ships.ShipType;
import io.reactivex.rxjava3.core.Observable;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class AppTest4Module extends IngameModule {

    protected final List<ShipType> BLUEPRINTS = new ArrayList<>(Arrays.asList(
            new ShipType("fighter",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("explorer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("colonizer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("interceptor",0,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>())
    ));

    AggregateResultDto value = new AggregateResultDto(2, null);

    protected final String TEST_FLEET_ID = "testAppFleetID";

    protected final Fleet NEW_FLEET = new Fleet("a","a",
            TEST_FLEET_ID, GAME_ID, EMPIRE_ID, "TestAppFleet", GAME_SYSTEMS[0]._id(),
            4, new HashMap<>(), new HashMap<>(), new HashMap<>(),null);
    protected final Fleet NEW_FLEET_2 = new Fleet("a","a",
            TEST_FLEET_ID, GAME_ID, EMPIRE_ID, "TestAppFleet", GAME_SYSTEMS[0]._id(),
            4, Map.of("explorer", 1), new HashMap<>(), new HashMap<>(),null);

    protected final ReadShipDTO[] SHIPS_OF_FLEET = new ReadShipDTO[]{
            new ReadShipDTO("a", "b", "testShipID1", GAME_ID, EMPIRE_ID, TEST_FLEET_ID,
                    "explorer", 10, 10, new HashMap<>()),
            new ReadShipDTO("a", "b", "testShipID2", GAME_ID, EMPIRE_ID, TEST_FLEET_ID,
                    "colonizer", 10, 10, new HashMap<>())
    };

    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        when(this.fleetApiService.createFleet(any(), any())).thenReturn(Observable.just(NEW_FLEET));
        when(this.fleetApiService.deleteFleet(any(), any())).thenReturn(Observable.just(FLEETS[0]));

        when(this.shipsApiService.getAllShips(any(), any())).thenReturn(Observable.just(new ReadShipDTO[]{}))
                .thenReturn(Observable.just(SHIPS_OF_FLEET));

        doReturn(Observable.just(value)).when(gameLogicApiService).getCompare(any(),any(),any());

        when(this.empireApiService.getPrivate(any(), any())).thenReturn(Observable.empty());

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));
        when(this.jobsApiService.createTravelJob(any(), any(), any())).thenReturn(Observable.just(tickTravelJob(0)));
        when(this.jobsApiService.createShipJob(any(), any(), any())).thenReturn(Observable.just(tickShipJob(0)));

        when(this.fleetApiService.patchFleet(any(), any(), any())).thenReturn(Observable.just(NEW_FLEET_2));

        when(this.empireApiService.setEffect(any(), any(), any())).thenReturn(Observable.empty());

        doReturn(Observable.just(new WarDto(
                "0", "0", "warID", GAME_ID, EMPIRE_ID, ENEMY_EMPIRE_ID, "warName"
        ))).when(this.warsApiService).createWar(any(), any());

    }

    @Override
    protected void initializeEventListenerMocks() {
        super.initializeEventListenerMocks();

        doReturn(SHIP_SUBJECT).when(this.eventListener).listen("games." + GAME_ID + ".fleets."
                        + TEST_FLEET_ID + ".ships.*.*", Ship.class);
    }

    @Override
    protected void loadUnloadableData() {
        super.loadUnloadableData();

        this.shipService.shipTypesAttributes = new ArrayList<>(BLUEPRINTS);
    }

    protected Job tickTravelJob(int tick) {
        return new Job("0", "0",
                "testTravelJobID", tick, 4, GAME_ID, EMPIRE_ID, null, 0,
                "travel", null, null, null, TEST_FLEET_ID, null,
                new LinkedList<>(List.of("islandID_1", "enemyIslandID")), new HashMap<>(), null);
    }

    protected Job tickShipJob(int tick) {
        return new Job("0", "0",
                "shipJobID", tick, 4, GAME_ID, EMPIRE_ID, null, 0,
                "ship", null, null, null, TEST_FLEET_ID, "explorer",
                null, new HashMap<>(), null);
    }

}
