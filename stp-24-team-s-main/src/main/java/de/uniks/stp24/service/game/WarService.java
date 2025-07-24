package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.UpdateWarDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.rest.WarsApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class WarService {

    @Inject
    public  WarsApiService warsApiService;

    @Inject
    public WarService(){

    }

    public Observable<WarDto> createWar(String gameID, CreateWarDto createWarDto) {
        return this.warsApiService.createWar(gameID, createWarDto);
    }

    public Observable<List<WarDto>> getWars(String gameID, String empireID) {
        return this.warsApiService.getWars(gameID, empireID);
    }

    public Observable<WarDto> getWar(String gameID, String warID) {
        return this.warsApiService.getWar(gameID, warID);
    }

    public Observable<UpdateWarDto> updateWar(String gameID, String warID, UpdateWarDto updateWarDto) {
        return this.warsApiService.updateWar(gameID, warID, updateWarDto);
    }

    public Observable<WarDto> deleteWar(String gameID, String warID) {
        return this.warsApiService.deleteWar(gameID, warID);
    }

}
