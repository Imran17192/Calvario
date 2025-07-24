package de.uniks.stp24.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.stp24.Main;
import de.uniks.stp24.rest.*;
import de.uniks.stp24.service.TokenStorage;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;

@Module
public class HttpModule {
    @Provides
    @Singleton
    static OkHttpClient client(TokenStorage tokenStorage) {
        return new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                final String token = tokenStorage.getToken();
                if (token == null) {
                    return chain.proceed(chain.request());
                }
                final Request newRequest = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer " + token)
                    .build();
                return chain.proceed(newRequest);
            }).addInterceptor(chain -> {
                final Response response = chain.proceed(chain.request());
                if (response.code() >= 300) {
                    System.err.println(chain.request());
                    System.out.println(response.body().string());
                }
                return response;
            }).build();
    }

    @Provides
    @Singleton
    Retrofit retrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
            .baseUrl(Main.API_URL + "/")
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create(mapper))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();
    }

    @Provides
    @Singleton
    GameMembersApiService gameMembersApiService(Retrofit retrofit) {
        return retrofit.create(GameMembersApiService.class);
    }

    @Provides
    @Singleton
    AuthApiService authApiService(Retrofit retrofit) {
        return retrofit.create(AuthApiService.class);
    }

    @Provides
    @Singleton
    GamesApiService gamesApiService(Retrofit retrofit) {
        return retrofit.create(GamesApiService.class);
    }

    @Provides
    @Singleton
    UserApiService userApiService(Retrofit retrofit) {
        return retrofit.create(UserApiService.class);
    }

    @Provides
    @Singleton
    EmpireApiService empireApiService(Retrofit retrofit) {
        return retrofit.create(EmpireApiService.class);
    }

    @Provides
    @Singleton
    GameSystemsApiService gameSystemsApiService(Retrofit retrofit) {
        return retrofit.create(GameSystemsApiService.class);
    }

    @Provides
    @Singleton
    PresetsApiService presetsApiService(Retrofit retrofit) {
        return retrofit.create(PresetsApiService.class);
    }

    @Provides
    @Singleton
    GameLogicApiService gameLogicApiService(Retrofit retrofit) {
        return retrofit.create(GameLogicApiService.class);
    }

    @Provides
    @Singleton
    JobsApiService jobsApiService(Retrofit retrofit){
        return  retrofit.create(JobsApiService.class);
    }

    @Provides
    @Singleton
    FleetApiService fleetApiService(Retrofit retrofit) {return retrofit.create(FleetApiService.class);}

    @Provides
    @Singleton
    WarsApiService warsApiService(Retrofit retrofit) {return retrofit.create(WarsApiService.class);}

    @Provides
    @Singleton
    ShipsApiService shipsApiService(Retrofit retrofit) {return retrofit.create(ShipsApiService.class);}

}
