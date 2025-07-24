package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.rest.EmpireApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class EmpireService {
    @Inject
    public EmpireApiService empireApiService;

    @Inject
    public EmpireService() {
    }

    public Observable<EmpireDto> getEmpire(String gameID, String empireID) {
        return this.empireApiService.getEmpire(gameID, empireID);
    }
    public Observable<ReadEmpireDto[]> getEmpires(String gameID) {
        return this.empireApiService.getEmpires(gameID);
    }

    public Observable<AggregateResultDto> getResourceAggregates(String gameID, String empireID) {
        return this.empireApiService.getResourceAggregates(gameID, empireID);
    }
}
