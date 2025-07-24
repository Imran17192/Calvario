package de.uniks.stp24.appTestModules;

import de.uniks.stp24.model.Jobs.Job;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

public class IslandClaimingModule extends IngameModule {

    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));

        when(empireApiService.getPrivate(any(), any())).thenReturn(Observable.empty());

        when(this.shipsApiService.getAllShips(any(), eq("testFleetID_3"))).thenReturn(Observable.just(FLEET_SHIPS_3));

        when(this.jobsApiService.createNewJob(any(), any(), any())).thenReturn(Observable.just(setJobProgress(0)));
        when(this.jobsApiService.deleteJob(any(), any(), any())).thenReturn(Observable.just(setJobProgress(3)));
    }

    protected Job setJobProgress(int progress) {
        return new Job(
                "0", "0",
                "jobClaimingID_1", progress, 3, GAME_ID, EMPIRE_ID, "islandID_4", 0,
                "upgrade", null, null, null, "", "",
                new LinkedList<>(), new HashMap<>(), null);
    }
}
