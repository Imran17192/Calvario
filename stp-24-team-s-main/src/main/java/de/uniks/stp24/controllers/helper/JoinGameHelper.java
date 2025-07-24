package de.uniks.stp24.controllers.helper;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.BasicController;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.LobbyService;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

public class JoinGameHelper extends BasicController {

    @Inject
    App app;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EmpireService empireService;
    @Inject
    LobbyService lobbyService;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public IslandsService islandsService;

    @Inject
    JobsService jobsService;

    @Inject
    public JoinGameHelper() {
    }

    public void joinGame(String gameId, boolean sleep) {
        app.show("/loading-screen");

        // Go through all empires of the game and save the empireId and gameId for the user
        // If there is no empire which belongs to the user, the user is a spectator.
        subscriber.subscribe(empireService.getEmpires(gameId), dto -> {
            for (ReadEmpireDto data : dto) {
                islandsService.saveEmpire(data._id(), data);
                if (data.user().equals(tokenStorage.getUserId()))
                    startGame(gameId, data._id(), false);
            }
            if (tokenStorage.getEmpireId() == null)
                startGame(gameId, null, true);

            islandsService.retrieveIslands(gameId, sleep);
        }, error -> System.out.println(error.getMessage()));

    }

    private void startGame(String gameId, String empireId, boolean isSpectator) {
        this.tokenStorage.setGameId(gameId);
        this.tokenStorage.setEmpireId(empireId);
        this.tokenStorage.setIsSpectator(isSpectator);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}