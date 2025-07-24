package de.uniks.stp24.rest;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.model.Trait;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Map;

@Singleton
public interface PresetsApiService {
    @GET("presets/variables")
    Observable<Map<String, Integer>> getVariablesPresets();

    @GET("presets/traits")
    Observable<Trait[]> getTraitsPreset();

    @GET("presets/technologies")
    Observable<ArrayList<TechnologyExtended>> getTechnologies();

    @GET("presets/technologies/{id}")
    Observable<TechnologyExtended> getTechnology(@Path("id") String id);

    @GET("presets/variables/effects")
    Observable<Map<String, ArrayList<String>>> getVariablesEffects();

    @GET("presets/variables")
    Observable<Map<String, Double>> getVariables();

}
