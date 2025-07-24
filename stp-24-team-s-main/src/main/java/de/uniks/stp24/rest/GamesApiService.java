package de.uniks.stp24.rest;

import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Game;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public interface GamesApiService {
    @PATCH("games/{id}")
    Observable<UpdateGameResultDto> editGame(@Path("id") String id, @Body UpdateGameDto dto);

    @PATCH("games/{id}")
    Observable<UpdateGameResultDto> editSpeed(@Path("id") String id, @Body UpdateSpeedDto dto);

    @PATCH("games/{id}")
    Observable<UpdateGameResultDto> updateSeason(@Path("id") String id, @Body UpdateSpeedDto dto, @Query("tick") boolean tick);

    @PATCH("games/{id}")
    Observable<UpdateGameResultDto> startGame(@Path("id") String id, @Body StartGameDto dto);

    @POST("games")
    Observable<CreateGameResultDto> createGame(@Body CreateGameDto dto);

    @POST("games")
    Observable<Game> create(@Body CreateGameDto dto);

    @GET("games")
    Observable<List<Game>> findAll();

    @GET("games")
    Observable<List<Game>> findAll(@Query("ids") List<String> ids);

    @GET("games/{id}")
    Observable<Game> getGame(@Path("id") String gameID);

    @DELETE("games/{id}")
    Observable<Game> deleteGame(@Path("id") String id);

}