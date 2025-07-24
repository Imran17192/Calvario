package de.uniks.stp24.rest;

import de.uniks.stp24.dto.*;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

import javax.inject.Singleton;

@Singleton
public interface GameSystemsApiService {
    @PATCH("games/{game}/systems/{id}")
    Observable<CreateSystemsDto> updateBuildings(@Path("game") String gameID, @Path("id") String ownerID, @Body UpdateBuildingDto dto);

    @PATCH("games/{game}/systems/{id}")
    Observable<CreateSystemsDto> upgradeSystem(@Path("game") String gameID, @Path("id") String ownerID, @Body UpgradeSystemDto dto);

    @GET("games/{game}/systems")
    Observable<SystemDto[]> getSystems(@Path("game") String gameID);

    @GET("games/{game}/systems/{id}")
    Observable<SystemDto> getSystem(@Path("game") String gameID, @Path("id") String systemID);

    @PATCH("games/{game}/systems/{id}")
    Observable<SystemDto> updateIsland(@Path("game") String gameID, @Path("id") String islandID, @Body SystemsDto dto);

    @PATCH("games/{game}/systems/{id}")
    Observable<SystemDto> claimSystem(@Path("game") String gameID, @Path("id") String islandID, @Body SystemClaimDto dto);

    @PATCH("games/{game}/systems/{id}")
    Observable<SystemDto> renameSystem(@Path("game") String gameID, @Path("id") String islandID, @Body SystemRenameDto dto);

    @GET("presets/districts/{id}")
    Observable<SiteDto> getSite(@Path("id") String siteID);

    @GET("presets/buildings/{id}")
    Observable<BuildingDto> getBuilding(@Path("id") String buildingID);

}
