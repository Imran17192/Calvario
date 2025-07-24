package de.uniks.stp24.service.menu;

import de.uniks.stp24.dto.LogoutDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.LogoutResult;
import de.uniks.stp24.rest.AuthApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BrowseGameService {
    @Inject
    GamesApiService gamesApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    PrefService prefService;
    @Inject
    ErrorService errorService;

    @Inject
    AuthApiService authApiService;

    private Game game;

    @Inject
    public BrowseGameService() {
    }

    public void setGame(Game game){
        this.game = game;
    }

    public Game getGame(){
        return this.game;
    }

    public void resetSelectedGame(){
        this.game = null;
    }

    public void handleGameSelection(Game game) {
        this.game = game;
    }

    //Check if selected game is yours
    public boolean checkMyGame(){
        if(game != null) {
            return game.owner().equals(tokenStorage.getUserId());
        }
        return false;
    }

    //Set a token for testing BrowseGameController
    public void setTokenStorage(){
        tokenStorage = new TokenStorage();
        tokenStorage.setName(null);
        tokenStorage.setToken(null);
        tokenStorage.setAvatar(null);
        tokenStorage.setUserId("testID");
    }
    //Calls Api DELETE if the game is from the user
    public Observable<Game> deleteGame() {
        if (checkMyGame()) {
            return gamesApiService.deleteGame(game._id());
        } else {
            return null;
        }
    }

    // refreshToken will be removed from device
    // this way the app shouldn't try to autologin on next start
    public Observable<LogoutResult> logout(String any) {
        return authApiService.logout(new LogoutDto(any))
                .doOnDispose(() -> prefService.removeRefreshToken());
    }

    public String getGameName() {
        if (checkMyGame()) {
            return this.game.name();
        } else {
            return "";
        }
    }
}
