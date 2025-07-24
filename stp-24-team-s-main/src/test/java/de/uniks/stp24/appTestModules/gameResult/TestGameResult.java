package de.uniks.stp24.appTestModules.gameResult;

import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.ws.Event;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
public class TestGameResult extends GameResultModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);
        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void testLosing() {
        WaitForAsyncUtils.waitForFxEvents();

        SYSTEMDTO_SUBJECT.onNext(new Event<>(String.format("games.%s.systems.%s.updated", GAME_ID, "empireIslandID"),
                new SystemDto("0", "0", "empireIslandID", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50,
                        ENEMY_EMPIRE_ID, 0)));
    }

    @Test
    public void testWinning() {
        WaitForAsyncUtils.waitForFxEvents();

        SYSTEMDTO_SUBJECT.onNext(new Event<>(String.format("games.%s.systems.%s.updated", GAME_ID, "enemyIslandID"),
                new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50,
                        EMPIRE_ID, 0)));

    }
}
