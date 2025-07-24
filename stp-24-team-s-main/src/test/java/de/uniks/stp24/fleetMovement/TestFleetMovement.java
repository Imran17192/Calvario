package de.uniks.stp24.fleetMovement;

import de.uniks.stp24.appTestModules.FleetMovementModule;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestFleetMovement extends FleetMovementModule {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        when(this.gameLogicApiService.getAggregate(any(),any(),any())).thenReturn(Observable.just(HEALTH_DEF_DTO));
        this.app.show(this.lobbyController);

        this.joinGameHelper.joinGame(GAME_ID, true);
    }

    @Test
    public void testFleetBehaviour() {
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#jobsOverviewButton");

        clickOn("#ingameFleet_testFleetID_1");
        clickOn("#islandID_5_instance");
        clickOn("#ingameFleet_testFleetID_2");
        clickOn("#ingameFleet_testFleetID_1");

        clickOn("#travelButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[4] + "created", TRAVEL_JOBS[0]));

        GAME_SUBJECT.onNext(tickGame(0));
        JOB_SUBJECT.onNext(progressJob(JOB_EVENT_PATHS[5] + "updated", TRAVEL_JOBS[1], 2));
        GAME_SUBJECT.onNext(tickGame(1));
        JOB_SUBJECT.onNext(progressJob(JOB_EVENT_PATHS[5] + "updated", TRAVEL_JOBS[1], 3));
        GAME_SUBJECT.onNext(tickGame(2));
        JOB_SUBJECT.onNext(progressJob(JOB_EVENT_PATHS[5] + "updated", TRAVEL_JOBS[1], 4));

        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#travelJobsButton");
        clickOn("#jobElementDeleteButton_travelJobID_1");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[4] + "deleted", TRAVEL_JOBS[0]));

        // Deleting a fleet removes it from the game map
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.testFleetID_1.deleted", FLEETS[0]));

        // Enemy fleet should change its location when it was changed on server
        FLEET_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".fleets.enemyFleetID_1.updated", FLEETS[2]));

        // Test IslandTravelComponent
        clickOn("#ingameFleet_testFleetID_5");
        clickOn("#islandID_1_instance");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#islandTravelButton");
    }
}
