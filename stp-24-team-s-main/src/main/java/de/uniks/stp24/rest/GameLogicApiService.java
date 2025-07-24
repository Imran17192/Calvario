package de.uniks.stp24.rest;

import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.ExplainedVariable;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public interface GameLogicApiService {

    @GET("games/{game}/empires/{empire}/variables")
    Observable<ArrayList<ExplainedVariableDTO>> getVariablesExplanations(@Path("empire") String empireID, @Query("variables") ArrayList<String> variables);

    @GET("games/{gameID}/empires/{empireID}/variables/{variable}")
    Observable<ExplainedVariable> getVariable(@Path("gameID") String gameID, @Path("empireID") String empireID, @Path("variable") String variable);

    @GET("games/{game}/empires/{empire}/aggregates/{aggregate}")
    Observable<AggregateResultDto> getTechnologyCostAndTime(@Path("empire") String empireID, @Path("aggregate") String aggregate, @Query("technology") String techID);

    // generic allows flexible usage for "system" queries!
    @GET("games/{game}/empires/{empire}/aggregates/{aggregate}")
    Observable<AggregateResultDto> getAggregate(@Path("empire") String empireID, @Path("aggregate") String aggregate, @Query("system") String systemID);

    // generic allows flexible usage for "compare" queries!
    @GET("games/{game}/empires/{empire}/aggregates/{aggregate}")
    Observable<AggregateResultDto> getCompare(@Path("empire") String empireID, @Path("aggregate") String aggregate, @Query("compare") String enemyID);
}

