package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.fleetManager.*;
import de.uniks.stp24.dto.ShortSystemDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.rest.FleetApiService;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.rest.ShipsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static de.uniks.stp24.model.Fleets.Fleet;
import static de.uniks.stp24.model.Fleets.ReadFleetDTO;
import static de.uniks.stp24.model.Ships.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestFleetManager extends ControllerTest {
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @InjectMocks
    FleetService fleetService;
    @InjectMocks
    ShipService shipService;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    VariableService variableService;
    @Spy
    JobsService jobsService;
    @Spy
    FleetApiService fleetApiService;
    @Spy
    ShipsApiService shipsApiService;

    @InjectMocks
    FleetManagerComponent fleetManagerComponent;
    @InjectMocks
    NewFleetComponent newFleetComponent;
    @InjectMocks
    ChangeFleetComponent changeFleetComponent;
    @InjectMocks
    BlueprintsDetailsComponent blueprintsDetailsComponent;

    protected final String
            GAME_ID = "123456",
            EMPIRE_ID = "testEmpireID",
            FLEET_ID = "fleetID1",
            LOCATION = "homeIsland";

    final Subject<Event<Ship>> SHIP_SUBJECT = BehaviorSubject.create();
    final Subject<Event<Fleet>> FLEET_SUBJECT = BehaviorSubject.create();

    protected final List<ReadFleetDTO> FLEETS = new ArrayList<>(Arrays.asList(
            new ReadFleetDTO("a", "a", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 3, new HashMap<>(Map.of("explorer", 1, "colonizer", 2)), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID2", GAME_ID, EMPIRE_ID, "fleetName2", LOCATION, 4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID3", GAME_ID, EMPIRE_ID, "fleetName3", LOCATION, 1, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID4", GAME_ID, EMPIRE_ID, "fleetName4", "somewhereElse", 4, new HashMap<>(), new HashMap<>()),
            new ReadFleetDTO("a", "a", "fleetID5", GAME_ID, "enemyEmpire", "fleetName5", "somewhereElse", 4, new HashMap<>(), new HashMap<>())
            ));

    protected final List<ShipType> BLUEPRINTS = new ArrayList<>(Arrays.asList(
            new ShipType("fighter",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("explorer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("colonizer",4,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>()),
            new ShipType("interceptor",0,100,5, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>())
           ));

    protected final List<Island> ISLANDS = new ArrayList<>(Arrays.asList(
            new Island(EMPIRE_ID,1,1,1,null,10,10,3,new HashMap<>(),new HashMap<>(), new ArrayList<>(Collections.singleton("shipyard")),"homeIsland","upgraded",LOCATION, 100),
            new Island(EMPIRE_ID,1,1,1,null,10,10,3,new HashMap<>(),new HashMap<>(), new ArrayList<>(Collections.singleton("shipyard")),"islandID2","upgraded","island2", 100),
            new Island(EMPIRE_ID,1,1,1,null,10,10,3,new HashMap<>(),new HashMap<>(),  new ArrayList<>(Collections.singleton("farm")),"islandID3","upgraded","island3", 100),
            new Island("enemy",1,1,1,null,10,10,3,new HashMap<>(),new HashMap<>(), new ArrayList<>(Collections.singleton("shipyard")),"islandID4","upgraded","island4", 100)
 ));

    protected final ReadShipDTO[] SHIPS = new ReadShipDTO[]{
            new ReadShipDTO("a","a","shipID1", GAME_ID, EMPIRE_ID, FLEET_ID, "explorer", 4,4, null),
            new ReadShipDTO("a","a","shipID2", GAME_ID, EMPIRE_ID, FLEET_ID, "colonizer", 4,4, null),
            new ReadShipDTO("a","a","shipID3", GAME_ID, EMPIRE_ID, FLEET_ID, "colonizer", 4,4, null)
    };




    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.newFleetComponent.islandsService = this.islandsService;
        this.newFleetComponent.fleetService = this.fleetService;
        this.changeFleetComponent.fleetService = this.fleetService;
        this.changeFleetComponent.shipService = this.shipService;
        this.fleetManagerComponent.fleetService = this.fleetService;
        this.fleetManagerComponent.shipService = this.shipService;
        this.fleetManagerComponent.changeFleetComponent = this.changeFleetComponent;
        this.fleetManagerComponent.newFleetComponent = this.newFleetComponent;
        this.fleetManagerComponent.subscriber = this.subscriber;
        this.fleetService.subscriber = new Subscriber();
        this.shipService.subscriber = new Subscriber();
        this.newFleetComponent.subscriber = new Subscriber();
        this.changeFleetComponent.subscriber = new Subscriber();
        this.resourcesService.tokenStorage = this.tokenStorage;
        this.shipService.tokenStorage = this.tokenStorage;
        this.fleetManagerComponent.variableService = this.variableService;
        this.fleetManagerComponent.jobsService = this.jobsService;
        this.fleetManagerComponent.blueprintsDetailsComponent = this.blueprintsDetailsComponent;

        // Mock TokenStorage
        doReturn("123456").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();


        // Mock fleet and ship listener
        when(this.eventListener.listen("games." + GAME_ID + ".fleets.*.*", Fleet.class)).thenReturn(FLEET_SUBJECT);
        when(this.eventListener.listen("games." + GAME_ID + ".fleets.*.ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);

        // Mock getting all fleets in a game
        when(this.fleetApiService.getGameFleets(GAME_ID, true)).thenReturn(Observable.just((ArrayList<ReadFleetDTO>) FLEETS));

        // Mock ship types
        this.shipService.shipTypesAttributes = (ArrayList<ShipType>) BLUEPRINTS;

        // Mock Islands
        doReturn(ISLANDS).when(this.islandsService).getIsles();

        this.app.show(this.fleetManagerComponent);
    }

    @BeforeEach
    public void initializeListener(){
        this.fleetService.loadGameFleets();
        this.fleetService.initializeFleetListeners();
        this.fleetService.initializeShipListener();
        this.fleetManagerComponent.showFleets();
    }


    @Test
    public void deleteFleet(){
        final Fleet fleet2 = new Fleet("a", "a", "fleetID2", GAME_ID, EMPIRE_ID, "fleetName3",
                LOCATION, 1, new HashMap<>(), new HashMap<>(), new HashMap<>(), null);
        when(this.fleetApiService.deleteFleet(any(),any())).thenReturn(Observable.just(fleet2));
        this.jobsService.jobCollections.put("travel", FXCollections.observableArrayList());
        this.jobsService.jobCollections.put("ship", FXCollections.observableArrayList());

        waitForFxEvents();
        assertEquals(4, fleetManagerComponent.fleets.size());
        assertEquals(3, fleetManagerComponent.blueprintsListView.getItems().size());
        clickOn("#deleteFleetButton_fleetID2");
        waitForFxEvents();
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.deleted", fleet2));
        waitForFxEvents();
        assertEquals(3, fleetManagerComponent.fleets.size());
    }

    @Test
    public void createFleet(){
        final Fleet fleet6 = new Fleet("a", "a", "fleetID6", GAME_ID, EMPIRE_ID, "fleetName5",
                LOCATION, 1, new HashMap<>(), new HashMap<>(), new HashMap<>(), null);
        //final CreateFleetDTO fleet5_created = new CreateFleetDTO("newFleet", LOCATION, new HashMap<>(), new HashMap<>(), new HashMap<>(), null);
        when(this.fleetApiService.createFleet(any(), any())).thenReturn(Observable.just(fleet6));

        waitForFxEvents();
        assertEquals(4, fleetManagerComponent.fleets.size());
        clickOn("#createFleetButton");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.newFleetComponent.isVisible());
        assertEquals(2, fleetManagerComponent.newFleetComponent.islandList.size());
        assertEquals("homeIsland (1 " + this.gameResourceBundle.getString("building.shipyard") + ")", fleetManagerComponent.newFleetComponent.islandNameLabel.getText());
        clickOn("#lastIslandButton");
        waitForFxEvents();
        assertEquals("island2 (1 " + this.gameResourceBundle.getString("building.shipyard") + ")", fleetManagerComponent.newFleetComponent.islandNameLabel.getText());
        clickOn("#nextIslandButton");
        waitForFxEvents();
        assertEquals("homeIsland (1 " + this.gameResourceBundle.getString("building.shipyard") + ")", fleetManagerComponent.newFleetComponent.islandNameLabel.getText());

        clickOn("#confirmIslandButton");
        waitForFxEvents();
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.created", fleet6));
        waitForFxEvents();
        assertEquals(5, fleetManagerComponent.fleets.size());
    }



    @Test
    public void editFleet(){
        this.fleetManagerComponent.blueprintsListView.setPrefWidth(350);
        final Fleet[] modFleets = new Fleet[]{
                new Fleet("a", "b", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 3, new HashMap<>(Map.of("explorer",2, "colonizer",2)), new HashMap<>(), new HashMap<>(), null),
                new Fleet("a", "c", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 3, new HashMap<>(Map.of("explorer",1, "colonizer",2)), new HashMap<>(), new HashMap<>(), null),
                new Fleet("a", "c", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 3, new HashMap<>(Map.of("explorer",1, "colonizer",2, "fighter",1)), new HashMap<>(), new HashMap<>(), null),
                new Fleet("a", "c", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 2, new HashMap<>(Map.of("explorer",0, "colonizer",2, "fighter",1)), new HashMap<>(), new HashMap<>(), null)

        };
        final Ship ship = new Ship("a","a","shipID1", GAME_ID, EMPIRE_ID, FLEET_ID, "explorer", 4,4, null, null);

        when(this.shipsApiService.getAllShips(any(),any())).thenReturn(Observable.just(SHIPS));
        when(this.shipsApiService.deleteShip(any(),any(),any())).thenReturn(Observable.just(ship));
        when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);
        when(this.fleetApiService.patchFleet(any(),any(),any()))
                .thenReturn(Observable.just(modFleets[0]))
                .thenReturn(Observable.just(modFleets[1]))
                .thenReturn(Observable.just(modFleets[2]))
                .thenReturn(Observable.just(modFleets[3]));

        // edit fleet
        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.blueprintsVBox.isVisible());
        assertTrue(fleetManagerComponent.fleetsOverviewVBox.isVisible());
        assertTrue(fleetManagerComponent.fleetBuilderVBox.isVisible());
        assertEquals(2, fleetManagerComponent.blueprintsInFleetList.size());
        assertEquals(3, fleetManagerComponent.blueprintsListView.getItems().size());

        // increment planned size:
        clickOn("#incrementSizeButton_explorer");
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", modFleets[0]));
        waitForFxEvents();
        assertEquals("Command Limit \n3/4", fleetManagerComponent.commandLimitLabel.getText());

        // decrement planned size:
        clickOn("#decrementSizeButton_explorer");
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", modFleets[1]));
        waitForFxEvents();
        assertEquals("Command Limit \n3/3", fleetManagerComponent.commandLimitLabel.getText());

        // add blueprint:
        // the button does not do anything if CI=true
        // clickOn("#addBlueprintButton_fighter");
        this.fleetManagerComponent.addBlueprintToFleet(BLUEPRINTS.getFirst());
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", modFleets[2]));
        waitForFxEvents();
        assertEquals(3, fleetManagerComponent.blueprintsInFleetList.size());

        // show ships
        clickOn("#shipsButton");
        waitForFxEvents();
        assertTrue( fleetManagerComponent.shipsVBox.isVisible());
        assertEquals(3, fleetManagerComponent.ships.size());

        // delete ship
        clickOn("#deleteShipButton");

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.deleted", ship));
        waitForFxEvents();
        assertEquals(2, fleetManagerComponent.ships.size());

        // remove blueprint
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", modFleets[3]));
        fleetManagerComponent.blueprintInFleetListView.refresh();
        waitForFxEvents();
        clickOn("#decrementSizeButton_explorer");
        waitForFxEvents();
        assertEquals(2, fleetManagerComponent.blueprintsInFleetList.size());
        this.fleetService.dispose();
    }

    @Test
    public void buildShips() {
        final Fleet[] modFleets = new Fleet[]{
                new Fleet("a", "b", FLEET_ID, GAME_ID, EMPIRE_ID, "fleetName1", LOCATION, 3, new HashMap<>(Map.of("explorer", 2, "colonizer", 2)), new HashMap<>(), new HashMap<>(), null),
        };
                Jobs.Job shipJob = new Jobs.Job("a","a", "shipJobID", 0, 4, GAME_ID, EMPIRE_ID, LOCATION, 1, "ship", "", "", "", FLEET_ID, "newShipID", null, Map.of("energy", 4), null);
        when(this.jobsApiService.createShipJob(any(),any(),any())).thenReturn(Observable.just(shipJob));

        when(this.fleetApiService.patchFleet(any(),any(),any())).thenReturn(Observable.just(modFleets[0]));
        doReturn(false).when(this.resourcesService).hasEnoughResources(any());
        when(this.shipsApiService.getAllShips(any(),any())).thenReturn(Observable.just(SHIPS));
        when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);


        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();

        // Fails because of resources
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals("You have already built all planned ships!", fleetManagerComponent.buildShipErrorLabel.getText());
        doReturn(true).when(this.resourcesService).hasEnoughResources(any());

        // Fails because of planned Size
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals("You have already built all planned ships!", fleetManagerComponent.buildShipErrorLabel.getText());

        // increment planned size:
        clickOn("#incrementSizeButton_explorer");
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", modFleets[0]));
        waitForFxEvents();

        // Build ship
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals("The construction of your new ship has started!", fleetManagerComponent.buildShipErrorLabel.getText());
    }

    @Test
    public void buildShipOnWrongIsland(){
        final Island[] ISLAND = new Island[]{
                new Island(EMPIRE_ID, 1, 1, 1, null, 10, 10, 3, new HashMap<>(), new HashMap<>(), new ArrayList<>(Collections.singleton("farm")), "homeIsland", "upgraded", LOCATION, 100),
                new Island("enemy", 1, 1, 1, null, 10, 10, 3, new HashMap<>(), new HashMap<>(), new ArrayList<>(Collections.singleton("shipyard")), "homeIsland", "upgraded", LOCATION, 100),
        };
        when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);
        when(this.shipsApiService.getAllShips(any(),any())).thenReturn(Observable.just(SHIPS));
        doReturn(true).when(this.resourcesService).hasEnoughResources(any());
        doReturn(Collections.singletonList(ISLAND[0])).when(this.islandsService).getIsles();

        // No shipyard on the island
        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertEquals("homeIsland\n0 / 0 " + this.gameResourceBundle.getString("shipyards.occupied"), fleetManagerComponent.islandLabel.getText());
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals(this.gameResourceBundle.getString("buildShipError.shipyard"), fleetManagerComponent.buildShipErrorLabel.getText());
        clickOn("#showFleetsButton");
        waitForFxEvents();

        // Enemies Island
        doReturn(Collections.singletonList(ISLAND[1])).when(this.islandsService).getIsles();

        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertEquals("homeIsland\n" + this.gameResourceBundle.getString("not.your.island"), fleetManagerComponent.islandLabel.getText());
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals(this.gameResourceBundle.getString("not.your.island"), fleetManagerComponent.buildShipErrorLabel.getText());
        clickOn("#showFleetsButton");
        waitForFxEvents();

        // Island don't belong to anyone
        List<ShortSystemDto> systemList = new ArrayList<>();
        doReturn(systemList).when(this.islandsService).getIsles();

        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertEquals(this.gameResourceBundle.getString("unknown.seas"), fleetManagerComponent.islandLabel.getText());
        clickOn("#buildShipButton");
        waitForFxEvents();
        assertEquals(this.gameResourceBundle.getString("not.your.island"), fleetManagerComponent.buildShipErrorLabel.getText());
        clickOn("#showFleetsButton");
    }

    @Test
    public void shipCreated(){
        final Ship ship = new Ship("a","a","newShipID", GAME_ID, EMPIRE_ID, FLEET_ID, "explorer", 4,4, null, null);

        when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);
        when(this.shipsApiService.getAllShips(any(),any())).thenReturn(Observable.just(SHIPS));

        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertEquals(2, fleetManagerComponent.blueprintsInFleetList.size());
        assertEquals(3, fleetManagerComponent.blueprintsListView.getItems().size());

        // show ships
        clickOn("#shipsButton");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.shipsVBox.isVisible());
        assertEquals(3, fleetManagerComponent.ships.size());

        // ship is created
        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.created", ship));
        waitForFxEvents();
        assertEquals(4, fleetManagerComponent.ships.size());
    }
    
    @Test
    public void changeFleetOfShip(){
        final Ship ship = new Ship("a","a","shipID1", GAME_ID, EMPIRE_ID, "fleetID2", "explorer", 4,4, null, null);
        final Fleet fleet = new Fleet("a", "a", "fleetID2", GAME_ID, EMPIRE_ID, "fleetName2", LOCATION, 4, new HashMap<>(Map.of("explorer",1)), new HashMap<>(), new HashMap<>(), null);

        when(this.shipsApiService.getAllShips(any(),any())).thenReturn(Observable.just(SHIPS));
        when(this.eventListener.listen("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.*", Ship.class)).thenReturn(SHIP_SUBJECT);
        when(this.fleetApiService.patchFleet(any(),any(),any())).thenReturn(Observable.just(fleet));
        when(this.shipsApiService.patchShip(any(),any(),any(),any())).thenReturn(Observable.just(ship));

        waitForFxEvents();
        clickOn("#editFleetButton_fleetID1");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.blueprintsVBox.isVisible());
        assertTrue(fleetManagerComponent.fleetsOverviewVBox.isVisible());
        assertTrue(fleetManagerComponent.fleetBuilderVBox.isVisible());
        assertEquals(2, fleetManagerComponent.blueprintsInFleetList.size());
        assertEquals(3, fleetManagerComponent.blueprintsListView.getItems().size());

        // show ships
        clickOn("#shipsButton");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.shipsVBox.isVisible());
        assertEquals(3, fleetManagerComponent.ships.size());

        // change fleet
        clickOn("#changeFleetButton");
        waitForFxEvents();
        assertTrue(fleetManagerComponent.changeFleetComponent.isVisible());
        assertEquals(2, fleetManagerComponent.changeFleetComponent.fleetsOnIslandList.size());
        assertEquals("fleetName2", fleetManagerComponent.changeFleetComponent.newFleetOfShipNameLabel.getText());

        // switch through possible fleets
        clickOn("#showLastFleetButton");
        waitForFxEvents();
        assertEquals("fleetName3", fleetManagerComponent.changeFleetComponent.newFleetOfShipNameLabel.getText());

        // switch through possible fleets
        clickOn("#showNextFleetButton");
        waitForFxEvents();
        assertEquals("fleetName2", fleetManagerComponent.changeFleetComponent.newFleetOfShipNameLabel.getText());

        // confirm fleet change
        clickOn("#confirmFleetChangeButton");
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.*.updated", fleet));
        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets." + FLEET_ID + ".ships.*.updated", ship));
        waitForFxEvents();
        assertFalse(fleetManagerComponent.changeFleetComponent.isVisible());
        assertEquals(2, fleetManagerComponent.ships.size());
    }


}
