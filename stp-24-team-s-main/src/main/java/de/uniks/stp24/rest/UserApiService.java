package de.uniks.stp24.rest;

import de.uniks.stp24.dto.AvatarDto;
import de.uniks.stp24.dto.CreateUserDto;
import de.uniks.stp24.dto.SignUpResultDto;
import de.uniks.stp24.dto.UpdateUserDto;
import de.uniks.stp24.model.User;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

public interface UserApiService {
    @POST("users")
    Observable<SignUpResultDto> signup(@Body CreateUserDto dto);

    @GET("users/{id}")
    Observable<User> getUser(@Path("id") String id);

    @DELETE("users/{id}")
    Observable<User> delete(@Path("id") String id);

    @PATCH("users/{id}")
    Observable<User> edit(@Path("id") String id, @Body UpdateUserDto dto);

    @PATCH("users/{id}")
    Observable<User> updateAvatar(@Path("id") String id, @Body AvatarDto dto);

}
