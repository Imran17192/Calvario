package de.uniks.stp24.rest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import static de.uniks.stp24.model.Ships.*;

public interface ShipsApiService {
    @GET("games/{game}/fleets/{fleet}/ships")
    Observable<ReadShipDTO[]> getAllShips(@Path("game") String gameID, @Path("fleet") String fleetID);

    @PATCH("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> patchShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID, @Body UpdateShipDTO updateShipDTO);

    @DELETE("games/{game}/fleets/{fleet}/ships/{id}")
    Observable<Ship> deleteShip(@Path("game") String gameID, @Path("fleet") String fleetID, @Path("id") String shipID);

}

