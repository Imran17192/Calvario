package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.GameResultPopup;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.TokenStorage;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnInit;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class GameStatService {
    @Inject
    public IslandsService islandsService;
    @Inject
    public TokenStorage tokenStorage;

    GameResultPopup gameResultPopup;

    private String gameWinner;
    private List<String> gameLosers = new ArrayList<>();
    private List<String> allEmpires = new ArrayList<>();
    public Map<String, List<Island>> empireIslands = new HashMap<>();

    @Inject
    public GameStatService() {
    }

    @OnInit
    public void init() {

    }

    public void checkState() {
        this.allEmpires = islandsService.getEmpiresID();
        this.empireIslands = islandsService.getEmpireIslands();
        System.out.println("alleEmpires" +allEmpires);
        System.out.println("empireIslands" +empireIslands);

        System.out.println("gameResult: 4");
        for (String empire : empireIslands.keySet()) {
            if (empireIslands.get(empire).isEmpty()) {
                System.out.println("gameResult: 5 we found a loser");
                addLoser(empire);
                System.out.println(gameLosers);
            }
        }
        checkForWinner();
    }

    public void addLoser(String gameLoser) {
        System.out.println("6");
        this.gameLosers.add(gameLoser);
        empireIslands.remove(gameLoser);
        checkForLoser();
    }

    private void checkForLoser() {
        System.out.println("7");
        for (String loser : gameLosers) {
            if (tokenStorage.getEmpireId().equals(loser)){
                showLoserScreen();
            }
        }
    }

    private void checkForWinner() {
        if(empireIslands.size() == 1){
            setWinner(empireIslands.keySet().iterator().next());
            if(tokenStorage.getEmpireId().equals(getGameWinner())){
                showWinnerScreen();
                System.out.println("9");
            }
        }
    }

    private void showWinnerScreen() {
        //TODO Text for winner
        gameResultPopup.open(false);
    }

    private void showLoserScreen() {
        System.out.println("8");
        //TODO Text for Loser and spectator mode
        gameResultPopup.open(true);
    }

    public void setGameResultPopup(GameResultPopup gameResultPopup) {
        this.gameResultPopup = gameResultPopup;
    }

    public void setWinner(String gameWinner) {
        this.gameWinner = gameWinner;
    }

    public String getGameWinner() {
        return gameWinner;
    }
}
