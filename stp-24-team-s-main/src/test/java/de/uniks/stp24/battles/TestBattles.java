package de.uniks.stp24.battles;

import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets.Fleet;
import de.uniks.stp24.model.Ships.Ship;
import de.uniks.stp24.ws.Event;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class TestBattles extends BattlesModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);
        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void testLosingAgainstWildFleet() {
        WaitForAsyncUtils.waitForFxEvents();

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToWild.ships.wildShip.deleted",
                new Ship("a", "a",
                "wildShip", GAME_ID, EMPIRE_ID, "empireFleetToWild", "explorer", 0,
                0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToWild.deleted",
                new Fleet("a", "a",
                        "empireFleetToWild", GAME_ID, EMPIRE_ID, "EmpireFleetUno", GAME_SYSTEMS[2]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(((Label) lookup("#youLostBox").queryAs(VBox.class).getChildren().getFirst()).getText().contains("1"));

        clickOn("#closeResultButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(this.battleResultComponent.isVisible());
    }

    @Test
    public void testWinningAgainstWildFleet() {
        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.wildFleet.ships.wildShip.deleted",
                new Ship("a", "a",
                        "wildShip", GAME_ID, null, "wildFleet", "explorer", 0,
                        0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.wildFleet.deleted",
                new Fleet("a", "a",
                        "wildFleet", GAME_ID, null, "EmpireFleetUno", GAME_SYSTEMS[2]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(((Label) lookup("#theyLostBox").queryAs(VBox.class).getChildren().getFirst()).getText().contains("1"));
    }

    @Test
    public void testLosingAgainstEnemyFleet() {
        WaitForAsyncUtils.waitForFxEvents();

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToEnemy.ships.enemyShip.deleted",
                new Ship("a", "a",
                "enemyShip", GAME_ID, EMPIRE_ID, "empireFleetToEnemy", "explorer", 0,
                0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToEnemy.deleted",
                new Fleet("a", "a",
                        "empireFleetToEnemy", GAME_ID, EMPIRE_ID, "EmpireFleetUno", GAME_SYSTEMS[3]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(((Label) lookup("#youLostBox").queryAs(VBox.class).getChildren().getFirst()).getText().contains("1"));
    }

    @Test
    public void testWinningAgainstEnemyFleet() {
        WaitForAsyncUtils.waitForFxEvents();

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.enemyFleetBattle.ships.enemyShip.deleted",
                new Ship("a", "a",
                        "enemyShip", GAME_ID, ENEMY_EMPIRE_ID, "enemyFleetBattle", "explorer", 0,
                        0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.enemyFleetBattle.deleted",
                new Fleet("a", "a",
                        "enemyFleetBattle", GAME_ID, ENEMY_EMPIRE_ID, "EmpireFleetUno", GAME_SYSTEMS[3]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(((Label) lookup("#theyLostBox").queryAs(VBox.class).getChildren().getFirst()).getText().contains("1"));
    }

    @Test
    public void testLosingIsland() {
        WaitForAsyncUtils.waitForFxEvents();

        SYSTEMDTO_SUBJECT.onNext(new Event<>(String.format("games.%s.systems.%s.updated", GAME_ID, "empireIslandID"),
                new SystemDto("0", "0", "empireIslandID", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50,
                        ENEMY_EMPIRE_ID, 0)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(lookup("#coloredText").queryText().getText().contains("EnemyEmpire"));
    }

    @Test
    public void testWinningIsland() {
        WaitForAsyncUtils.waitForFxEvents();

        SYSTEMDTO_SUBJECT.onNext(new Event<>(String.format("games.%s.systems.%s.updated", GAME_ID, "enemyIslandID"),
                new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50,
                        EMPIRE_ID, 0)));

        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.battleResultComponent.isVisible());
        assertTrue(lookup("#coloredText").queryText().getText().contains("EnemyEmpire"));
    }

    @Test
    public void testFleeFromBattle() {
        WaitForAsyncUtils.waitForFxEvents();

        SHIP_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToEnemy.ships.enemyShip.deleted",
                new Ship("a", "a",
                        "enemyShip", GAME_ID, EMPIRE_ID, "empireFleetToEnemy", "explorer", 0,
                        0, null, null)));

        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.empireFleetToEnemy.updated",
                new Fleet("a", "a",
                        "empireFleetToEnemy", GAME_ID, EMPIRE_ID, "EmpireFleetUno", GAME_SYSTEMS[0]._id(),
                        4, new HashMap<>(), new HashMap<>(), null, null)));

        assertFalse(this.battleResultComponent.isVisible());
    }


    @Test
    public void testStartingAndFinishingBattlesOnWarDeclaration() {
        WaitForAsyncUtils.waitForFxEvents();

        WAR_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".wars.warID.deleted", new WarDto(
                "0", "0", "warID", GAME_ID, EMPIRE_ID, ENEMY_EMPIRE_ID, "War")));
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(lookup("#empireIslandID_instance").queryAs(IslandComponent.class).sableImage.isVisible());


        WAR_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".wars.warID.created", new WarDto(
                "0", "0", "warID", GAME_ID, EMPIRE_ID, ENEMY_EMPIRE_ID, "War")));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(lookup("#empireIslandID_instance").queryAs(IslandComponent.class).sableImage.isVisible());
    }
}
