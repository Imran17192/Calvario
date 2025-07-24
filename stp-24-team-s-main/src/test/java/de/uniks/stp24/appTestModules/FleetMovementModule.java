package de.uniks.stp24.appTestModules;

import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class FleetMovementModule extends IngameModule {
    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>(List.of(TRAVEL_JOBS[1]))));

        when(this.jobsApiService.deleteJob(any(), any(), any()))
                .thenReturn(Observable.just(TRAVEL_JOBS[0]));

        when(this.empireApiService.getPrivate(any(), any())).thenReturn(Observable.empty());

        when(this.shipsApiService.getAllShips(any(), eq("testFleetID_1"))).thenReturn(Observable.just(FLEET_SHIPS_1));
        when(this.shipsApiService.getAllShips(any(), eq("testFleetID_2"))).thenReturn(Observable.just(FLEET_SHIPS_2));
        when(this.shipsApiService.getAllShips(any(), eq("testFleetID_5"))).thenReturn(Observable.just(FLEET_SHIPS_5));
    }
}
