package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Game;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.EditGameService;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Game.fxml")
public class GameComponent extends HBox implements ReusableItemComponent<Game> {
    @FXML
    public Text game_name;


    @Inject
    TokenStorage tokenStorage;

    @Inject
    BrowseGameService browseGameService;
    @Inject
    EditGameService editGameService;
    @Inject
    @Resource
    ResourceBundle resources;

    private Game game;

    //Check if component is selected
    @Inject
    public GameComponent(BubbleComponent bubbleComponent, BrowseGameService browseGameService, EditGameService editGameService, TokenStorage tokenStorage, ResourceBundle resources){
        this.tokenStorage = tokenStorage;
        this.browseGameService = browseGameService;
        this.editGameService = editGameService;
        this.resources = resources;
        this.setOnMouseClicked(event -> {
            editGameService.setClickedGame(game);
            browseGameService.handleGameSelection(game);
            bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.which.game"));
        });
    }


    @Override
    public void setItem(@NotNull Game game) {
        game_name.setText(game.name());
        if(tokenStorage == null){
            setTestToken();
        }
        //Your game will be displayed in color green
        if (game.owner().equals(tokenStorage.getUserId())) {
            game_name.setFill(Color.BLACK);
        } else {
            game_name.setFill(Color.WHITE);
        }
        this.game = game;
    }



    //Set Token for testing
    private void setTestToken(){
        tokenStorage = new TokenStorage();
        tokenStorage.setName(null);
        tokenStorage.setToken(null);
        tokenStorage.setAvatar(null);
        tokenStorage.setUserId("testID");
    }

}