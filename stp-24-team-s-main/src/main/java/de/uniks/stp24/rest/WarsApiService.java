package de.uniks.stp24.rest;

import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.UpdateWarDto;
import de.uniks.stp24.dto.WarDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface WarsApiService {
    @POST("games/{game}/wars")
    Observable<WarDto> createWar(@Path("game") String gameID, @Body CreateWarDto createWarDto);

    @GET("games/{game}/wars")
    Observable<List<WarDto>> getWars(@Path("game") String gameID, @Query("empire")  String empireID);

    @GET("games/{game}/wars/{id}")
    Observable<WarDto> getWar(@Path("game") String gameID, @Path("id") String warID);

    @PATCH("games/{game}/wars/{id}")
    Observable<UpdateWarDto> updateWar(@Path("game") String gameID, @Path("id") String warID, @Body UpdateWarDto updateWarDto);

    @DELETE("games/{game}/wars/{id}")
    Observable<WarDto> deleteWar(@Path("game") String gameID, @Path("id") String warID);
}
