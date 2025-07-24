package de.uniks.stp24.service.menu;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;

public class GamesService {
    @Inject
    public GamesService() {

    }

    @Inject
    public GamesApiService gamesApiService;

    public Observable<Game> getGame(String gameID) {
        return this.gamesApiService.getGame(gameID);
    }
}
