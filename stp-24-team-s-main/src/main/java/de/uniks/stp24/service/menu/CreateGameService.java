package de.uniks.stp24.service.menu;

import de.uniks.stp24.controllers.CreateGameController;
import de.uniks.stp24.dto.CreateGameDto;
import de.uniks.stp24.dto.CreateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class CreateGameService {
    @Inject
    GamesApiService gamesApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    ErrorService errorService;
    private ObservableList<Game> games = FXCollections.observableArrayList();
    CreateGameController createGameController;

    @Inject
    public CreateGameService() {
    }

    public void setGamesList(ObservableList<Game> games){
        this.games = games;
    }

    // class was modified! some code was deleted
    // error handle occurs now in controller
    public Observable<CreateGameResultDto> createGame(String name, GameSettings settings, String password, int maxMembers) {
            return gamesApiService
                    .createGame(new CreateGameDto(name, maxMembers, false, 1, settings, password))
                    .doOnError(error -> createGameController.showError(error));
    }
    public void setCreateGameController(CreateGameController createGameController){
        this.createGameController = createGameController;
    }

    public boolean nameIsAvailable(String name) {
        for (Game it : games) {
            if (it.name().equals(name)){
                return false;
            }
        }
        return true;
    }

}
