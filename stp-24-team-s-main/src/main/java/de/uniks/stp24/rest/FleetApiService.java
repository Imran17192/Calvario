package de.uniks.stp24.rest;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.ArrayList;

import static de.uniks.stp24.model.Fleets.*;

public interface FleetApiService {
    @POST("games/{game}/fleets")
    Observable<Fleet> createFleet(@Path("game") String gameID, @Body CreateFleetDTO fleet);

    @GET("games/{game}/fleets")
    Observable<ArrayList<ReadFleetDTO>> getGameFleets(@Path("game") String gameID, @Query("ships") boolean ships);

    @GET("games/{game}/fleets")
    Observable<ArrayList<ReadFleetDTO>> getEmpireFleets(@Path("game") String gameID, @Query("empire") String empireID);

    @GET("games/{game}/fleets/{id}")
    Observable<ReadFleetDTO> getFleet(@Path("game") String gameID, @Path("id") String fleetID);

    @PATCH("games/{game}/fleets/{id}")
    Observable<Fleet> patchFleet(@Path("game") String gameID, @Path("id") String fleetID, @Body UpdateFleetDTO updateFleet);

    @DELETE("games/{game}/fleets/{id}")
    Observable<Fleet> deleteFleet(@Path("game") String gameID, @Path("id") String fleetID);
}
