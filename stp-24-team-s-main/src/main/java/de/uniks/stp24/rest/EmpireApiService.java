package de.uniks.stp24.rest;

import de.uniks.stp24.dto.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public interface EmpireApiService {
    @GET("games/{game}/empires")
    Observable<ReadEmpireDto[]> getEmpires(@Path("game") String gameID);

    @GET("games/{game}/empires")
    Observable<EmpireDto[]> getEmpiresDtos(@Path("game") String gameID);

    @GET("games/{game}/empires/{empire}")
    Observable<EmpireDto> getEmpire(@Path("game") String game, @Path("empire") String empire);

    @GET("games/{game}/empires/{empire}")
    Observable<EffectSourceParentDto> getEmpireEffect(@Path("game") String game, @Path("empire") String empire);

    @PATCH("games/{game}/empires/{empire}")
    Observable<EmpireDto> updateEmpire(@Path("game") String game, @Path("empire") String empire, @Body UpdateEmpireDto updateEmpireDto);

    @PATCH("games/{game}/empires/{empire}")
    Observable<EmpireDto> setEffect(@Path("game") String gameId, @Path("empire") String empireId, @Body EffectSourceParentDto effect);

    @GET("presets/resources")
    Observable<List<ResourceDto>> getResources();

    @GET("games/{game}/empires/{empire}/aggregates/resources.periodic")
    Observable<AggregateResultDto> getResourceAggregates(@Path("game") String game, @Path("empire") String empire);

    @PATCH("games/{game}/empires/{empire}")
    Observable<UpdateEmpireMarketDto> updateEmpireMarket(@Path("game") String game, @Path("empire") String empire, @Body UpdateEmpireMarketDto updateEmpireMarketDto);

    //TODO Use privates instead of seaonsalTrades

    @PATCH("games/{game}/empires/{empire}")
    Observable<UpdateEmpireMarketDto> saveSeasonalComponents(@Path("game") String game, @Path("empire") String empire, @Body SeasonalTradeDto seasonalTradeDto);

    @GET("games/{game}/empires/{empire}")
    Observable<SeasonalTradeDto> getSeasonalTrades(@Path("game") String game, @Path("empire") String empire);

    @PATCH("games/{game}/empires/{empire}")
    Observable<UpdateEmpireMarketDto> savePrivate(@Path("game") String game, @Path("empire") String empire, @Body EmpirePrivate empirePrivate);

    @GET("games/{game}/empires/{empire}")
    Observable<EmpirePrivate> getPrivate(@Path("game") String game, @Path("empire") String empirePrivate);

    @PATCH("games/{game}/empires/{empire}")
    Observable<EmpirePrivate> savePrivateContact(@Path("game") String game, @Path("empire") String empire, @Body EmpirePrivate empirePrivate);
}
