package de.uniks.stp24.rest;

import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Jobs.JobDTO;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public interface JobsApiService {
    @GET("games/{game}/empires/{empire}/jobs")
    Observable<ArrayList<Job>> getEmpireJobs(@Path("game") String gameID, @Path("empire") String empireID);

    @POST("games/{game}/empires/{empire}/jobs")
    Observable<Job> createNewJob(@Path("game") String gameID, @Path("empire") String empireID,
                                 @Body JobDTO jobDTO);

    @GET("games/{game}/empires/{empire}/jobs/{id}")
    Observable<Job> getJobByID(@Path("game") String gameID, @Path("empire") String empireID,
                               @Path("id") String jobID);

    @DELETE("games/{game}/empires/{empire}/jobs/{id}")
    Observable<Job> deleteJob(@Path("game") String gameID, @Path("empire") String empireID,
                              @Path("id") String jobID);

    @POST("games/{game}/empires/{empire}/jobs")
    Observable<Job> createTravelJob(@Path("game") String gameID, @Path("empire") String empireID,
                                    @Body Jobs.TravelJobDTO travelJobDTO);

    @POST("games/{game}/empires/{empire}/jobs")
    Observable<Job> createShipJob(@Path("game") String gameID, @Path("empire") String empireID,
                                    @Body Jobs.ShipJobDTO shipJobDTO);

}
