package de.uniks.stp24;

import de.uniks.stp24.appTestModules.AppTest4Module;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Ships;
import de.uniks.stp24.ws.Event;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class AppTest4 extends AppTest4Module {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);

        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void v4() {
        this.shipService.shipTypesAttributes = new ArrayList<>(BLUEPRINTS);

        this.createNewFleet();
        this.travelFleetToEnemyIsland();
        this.startWar();
        this.battleForIsland();
    }

    public void createNewFleet() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#fleetManagerButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#createFleetButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#confirmIslandButton");
        FLEET_SUBJECT.onNext(new Event<>(String.format("games.%s.fleets.%s.created", GAME_ID, TEST_FLEET_ID), NEW_FLEET));

        clickOn("#deleteFleetButton_testFleetID_1");
        FLEET_SUBJECT.onNext(new Event<>(String.format("games.%s.fleets.%s.deleted", GAME_ID, "testFleetID_1"), FLEETS[0]));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#editFleetButton_" + TEST_FLEET_ID);

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#addBlueprintButton_explorer");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildShipButton");
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.shipJobID.created", GAME_ID, EMPIRE_ID),
                tickShipJob(0)));

        clickOn("#closeFleetManagerButton");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobsOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#shipJobsButton");

        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.shipJobID.updated", GAME_ID, EMPIRE_ID),
                tickShipJob(1)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.shipJobID.updated", GAME_ID, EMPIRE_ID),
                tickShipJob(2)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.shipJobID.updated", GAME_ID, EMPIRE_ID),
                tickShipJob(3)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.shipJobID.updated", GAME_ID, EMPIRE_ID),
                tickShipJob(4)));
    }

    public void travelFleetToEnemyIsland() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeButton");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#enemyIslandID_instance");

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#ingameFleet_"+TEST_FLEET_ID);
        clickOn("#travelButton");
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.created",
                GAME_ID, EMPIRE_ID), tickTravelJob(0)));

        clickOn("#jobsOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#travelJobsButton");

        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.updated",
                GAME_ID, EMPIRE_ID), tickTravelJob(1)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.updated",
                GAME_ID, EMPIRE_ID), tickTravelJob(2)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.updated",
                GAME_ID, EMPIRE_ID), tickTravelJob(3)));
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.updated",
                GAME_ID, EMPIRE_ID), tickTravelJob(4)));
        JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.testTravelJobID.deleted",
                GAME_ID, EMPIRE_ID), tickTravelJob(4)));
        FLEET_SUBJECT.onNext(new Event<>(String.format("games.%s.fleets.%s.updated", GAME_ID, TEST_FLEET_ID),
                new Fleet("a","a",
                        TEST_FLEET_ID, GAME_ID, EMPIRE_ID, "TestAppFleet", GAME_SYSTEMS[5]._id(),
                        4, Map.of("explorer", 1), new HashMap<>(), new HashMap<>(),null)));

        WaitForAsyncUtils.waitForFxEvents();
        Platform.runLater(() -> this.contactsService.addEnemyInPeace(ENEMY_EMPIRE_ID));
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeButton");
    }

    public void startWar() {
        clickOn("#contactsOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#contact_" + ENEMY_EMPIRE_ID);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#warButton");

        WAR_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".wars.warID.created", new WarDto(
                "0", "0", "warID", GAME_ID, EMPIRE_ID, ENEMY_EMPIRE_ID, "War")));
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeContactsComponent");
    }

    public void battleForIsland() {
        FLEET_SUBJECT.onNext(new Event<>(String.format("games.%s.fleets.%s.created", GAME_ID, "newEnemyFleet"),
                new Fleet("a","a",
                        "newEnemyFleet", GAME_ID, ENEMY_EMPIRE_ID, "EnemySuperFleet", GAME_SYSTEMS[5]._id(),
                        4, Map.of("explorer", 1), new HashMap<>(), new HashMap<>(),null)));

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.newEnemyFleet.ships.enemyShip.deleted",
                new Ships.Ship("a", "a",
                        "enemyShip", GAME_ID, ENEMY_EMPIRE_ID, "newEnemyFleet", "explorer", 0,
                        0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.newEnemyFleet.deleted",
                new Fleet("a", "a",
                        "newEnemyFleet", GAME_ID, ENEMY_EMPIRE_ID, "EnemySuperFleet", GAME_SYSTEMS[5]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        SYSTEMDTO_SUBJECT.onNext(new Event<>(String.format("games.%s.systems.%s.updated", GAME_ID, "enemyIslandID"),
                new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "EnemyIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(),
                        Upgrade.colonized, 13, Map.of("islandID_1", 20), 35, 60, EMPIRE_ID, 0)));

        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#closeResultButton");
    }
}
