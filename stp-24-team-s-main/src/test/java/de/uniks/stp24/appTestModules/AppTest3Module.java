package de.uniks.stp24.appTestModules;

import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.MemberDto;
import de.uniks.stp24.model.Jobs;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class AppTest3Module extends IngameModule {

    @Override
    protected void initializeEventListenerMocks() {
        super.initializeEventListenerMocks();

        doReturn(MEMBER_DTO_SUBJECT).when(this.eventListener)
                .listen("games." + GAME_ID + ".members.*.*", MemberDto.class);
    }

    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        doReturn(Observable.just(RESOURCE_AGGREGATES)).when(this.gameLogicApiService).getTechnologyCostAndTime(any(), any(), any());

        doNothing().when(this.saveLoadService).saveGang(any());

        doAnswer(inv -> {this.joinGameHelper.joinGame(GAME_ID, false);return Observable.just(RESULT_DTO);})
                .when(this.gamesApiService).startGame(any(), any());

        when(this.shipsApiService.getAllShips(any(), eq("testFleetID_1"))).thenReturn(Observable.just(FLEET_SHIPS_1));

        when(this.presetsApiService.getVariablesEffects()).thenReturn(Observable.just(new HashMap<>()));
        when(this.presetsApiService.getVariables()).thenReturn(Observable.just(MARKET_VARIABLES));
        when(this.presetsApiService.getTechnology(any())).thenReturn(Observable.just(TECHNOLOGY));
        when(this.presetsApiService.getTechnologies()).thenReturn(Observable.just(TECHNOLOGIES));
        when(this.presetsApiService.getTraitsPreset()).thenReturn(Observable.just(TRAITS));

        doAnswer(inv -> {
            this.app.show("/lobby", Map.of("gameid", GAME_ID));
            return Observable.empty();
        }).when(this.gameMembersApiService).patchMember(any(), any(), any());

        when(this.gameSystemsApiService.updateBuildings(any(), any(), any())).thenReturn(Observable.just(CREATE_SYSTEM_DTO));

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));

        when(this.variableService.getFirstHalfOfVariables()).thenReturn(Observable.just(EXPLAINED_VARIABLES));
        when(this.variableService.getSecondHalfOfVariables()).thenReturn(Observable.just(EXPLAINED_VARIABLES));

        when(this.gameSystemsApiService.getSystem(any(), any())).thenReturn(Observable.just(GAME_SYSTEMS[0]));
        when(this.gameMembersApiService.getMember(any(), any())).thenReturn(Observable.just(MEMBER_DTO));
        when(this.gameSystemsApiService.getBuilding(any())).thenReturn(Observable.just(BUILDING_DTO));

        when(this.empireApiService.saveSeasonalComponents(any(), any(), any())).thenReturn(Observable.just(MARKET_UPDATE_DTO));
        when(this.empireApiService.updateEmpireMarket(any(), any(), any())).thenReturn(Observable.just(MARKET_UPDATE_DTO));
        when(this.empireApiService.getEmpiresDtos(anyString())).thenReturn(Observable.just(new EmpireDto[]{EMPIRE_DTO}));
        when(this.empireApiService.getPrivate(any(), any())).thenReturn(Observable.empty());

        when(this.jobsApiService.deleteJob(anyString(), anyString(), any())).thenReturn(Observable.just(JOBS[3]));

        when(this.jobsApiService.createNewJob(anyString(), anyString(), any(Jobs.JobDTO.class)))
                .thenReturn(Observable.just(JOBS[3])).thenReturn(Observable.just(JOBS[2]))
                .thenReturn(Observable.just(JOBS[0])).thenReturn(Observable.just(JOBS[1]))
                .thenReturn(Observable.just(JOBS[4]));

    }
}
