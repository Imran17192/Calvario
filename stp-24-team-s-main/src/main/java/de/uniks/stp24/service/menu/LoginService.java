package de.uniks.stp24.service.menu;

import de.uniks.stp24.dto.LoginDto;
import de.uniks.stp24.dto.RefreshDto;
import de.uniks.stp24.model.LoginResult;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TechnologyService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class LoginService {
    @Inject
    AuthApiService authApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    PrefService prefService;
    @Inject
    public TechnologyService technologyService;

    @Inject
    public LoginService() {
    }

    public Observable<LoginResult> autologin(String refreshToken) {
        return authApiService.refresh(new RefreshDto(refreshToken))
                .doOnNext(loginResult -> {
                    tokenStorage.setToken(loginResult.accessToken());
                    tokenStorage.setUserId(loginResult._id());
                    tokenStorage.setName(loginResult.name());
                    tokenStorage.setAvatar(loginResult.avatar());
                    prefService.setRefreshToken(loginResult.refreshToken());
                    tokenStorage.setAvatarMap(loginResult._public());
                    technologyService.initAllTechnologies();
                });
    }


    public Observable<LoginResult> login(String username, String password, boolean rememberMe) {
        return authApiService
                .login(new LoginDto(username, password))
                .doOnNext(loginResult -> {
                    tokenStorage.setToken(loginResult.accessToken());
                    tokenStorage.setUserId(loginResult._id());
                    tokenStorage.setName(loginResult.name());
                    tokenStorage.setAvatar(loginResult.avatar());
                    tokenStorage.setAvatarMap(loginResult._public());
                    if (rememberMe) {
                        prefService.setRefreshToken(loginResult.refreshToken());
                    }
                    technologyService.initAllTechnologies();
                });
    }

}
