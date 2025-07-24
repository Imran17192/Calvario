package de.uniks.stp24.game.jobs;

import de.uniks.stp24.appTestModules.IslandClaimingModule;
import de.uniks.stp24.ws.Event;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;


@ExtendWith(MockitoExtension.class)
class TestIslandClaiming extends IslandClaimingModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.app.show(this.lobbyController);
        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void testClaimingJobWithFeet() {
        WaitForAsyncUtils.waitForFxEvents();
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.testFleetID_3.created", NEW_FLEET));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobsOverviewButton");
        clickOn("#ingameFleet_testFleetID_3");
        clickOn("#islandID_4_instance");
        clickOn("#exploreButton");
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.created", setJobProgress(0)));
        WaitForAsyncUtils.waitForFxEvents();

        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.updated", setJobProgress(1)));
        clickOn("#islandID_4_instance");
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.updated", setJobProgress(2)));
        clickOn("#islandID_4_instance");
        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.updated", setJobProgress(3)));
        clickOn("#islandID_4_instance");

        clickOn("#exploreButton");
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.created", setJobProgress(0)));
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#cancelJobButton");
        JOB_SUBJECT.onNext(new Event<>("games." + GAME_ID + "." + EMPIRE_ID + ".jobs.jobClaimingID_1.deleted", setJobProgress(0)));
    }
}