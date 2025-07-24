package de.uniks.stp24.service;

import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

@Singleton
public class InGameService {

    @Inject
    TimerService timerService;
    @Inject
    public GameStatus gameStatus;
    @Inject
    EventService eventService;
    @Inject
    GameSystemsApiService gameSystemsApiService;
    @Inject
    Subscriber subscriber;
    @Inject
    public PresetsApiService presetsApiService;

    private String gameOwnerID = "";

    @Inject
    public InGameService() {

    }

    public void setGameOwnerID(String gameOwnerID) {
        this.gameOwnerID = gameOwnerID;
    }

    public String getGameOwnerID() {
        return gameOwnerID;
    }

    public Observable<Map<String, Integer>> getVariablesPresets() {
        return presetsApiService.getVariablesPresets();
    }

    public void setPaused(Boolean isPaused) {
       gameStatus.setPaused(isPaused);
    }

    public Boolean getPaused() {
        return gameStatus.getPaused();
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public void setShowSettings(boolean show) {
        gameStatus.setShowSettings(show);
    }

    public void showOnly(StackPane stackPane, Node nodeToShow) {
        for (Node node : stackPane.getChildren()) node.setVisible(node == nodeToShow);
    }
}
