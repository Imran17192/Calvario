package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.component.menu.GameComponent;
import de.uniks.stp24.component.menu.LogoutComponent;
import de.uniks.stp24.component.menu.WarningComponent;
import de.uniks.stp24.controllers.helper.JoinGameHelper;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.CreateGameService;
import de.uniks.stp24.service.menu.EditGameService;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.*;

@Title("%browse.game")
@Controller
public class
BrowseGameController extends BasicController {
    @FXML
    public Button load_game_b;
    @FXML
    public Button new_game_b;
    @FXML
    public Button edit_acc_b;
    @FXML
    public Button del_game_b;
    @FXML
    public Button log_out_b;
    @FXML
    public Button edit_game_b;
    @FXML
    public ListView<Game> gameList;
    @FXML
    public VBox browseGameVBoxButtons;
    @FXML
    public VBox browseGameVBoxList;
    @FXML
    public Label userName;
    @FXML
    StackPane logoutWarningContainer;
    @FXML
    AnchorPane gameListAnchorPane;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @FXML
    TextField searchLine;
    @FXML
    Pane captainContainer;
    @FXML
    StackPane warningWindowContainer;
    @FXML
    ImageView backgroundImage;
    @FXML
    ImageView portraitImage;
    @FXML
    ImageView frameImage;

    @SubComponent
    @Inject
    public WarningComponent warningComponent;
    @SubComponent
    @Inject
    public LogoutComponent logoutComponent;
    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Inject
    EmpireService empireService;
    @Inject
    public JoinGameHelper joinGameHelper;
    @Inject
    LobbyService lobbyService;
    @Inject
    GamesApiService gamesApiService;
    @Inject
    EventListener eventListener;
    @Inject
    public GameComponent gameComponent;
    @Inject
    public BrowseGameService browseGameService;
    @Inject
    EditGameService editGameService;
    @Inject
    PopupBuilder popupBuilder;
    @Inject
    CreateGameService createGameService;
    final PopupBuilder popup = new PopupBuilder();

    final Map<String, Integer> avatarMap = new HashMap<>();
    final ArrayList<Image> backgroundsList = new ArrayList<>();
    final ArrayList<Image> portraitsList = new ArrayList<>();
    final ArrayList<Image> framesList = new ArrayList<>();

    final Provider<GameComponent> gameComponentProvider = () -> new GameComponent(this.bubbleComponent, this.browseGameService, this.editGameService, this.tokenStorage, this.resources);

    @Inject
    public BrowseGameController() {
    }

    // the fxml has no containers (text, label) for errors;
    private
    Text textInfo;

    private final ObservableList<Game> games = FXCollections.observableArrayList();
    private BooleanBinding deleteWarningIsInvisible;

    @OnRender
    void createBindings() {
        gameList.setItems(games);
        gameList.setCellFactory(list -> new ComponentListCell<>(app, gameComponentProvider));
        this.searchLine.textProperty().addListener((observable, oldValue, newValue) -> {
            this.gameList.scrollTo(0);
            if (newValue.isEmpty()) sortNewGamesOnTop();
            else
                this.games.sort(Comparator.comparing(game -> !game.name().toLowerCase().contains(newValue.toLowerCase())));
        });
    }

    public void initializeAvatarImage(Map<String, Integer> avatarMap) {
        String resourcesPaths = "/de/uniks/stp24/assets/avatar/";
        String backgroundFolderPath = "backgrounds/background_";
        String frameFolderPath = "frames/frame_";
        String portraitsFolderPath = "portraits/portrait_";

        for (int i = 0; i <= 9; i++) {
            backgroundsList.add(this.imageCache.get(resourcesPaths + backgroundFolderPath + i + ".png"));
            framesList.add(this.imageCache.get(resourcesPaths + frameFolderPath + i + ".png"));
            portraitsList.add(this.imageCache.get(resourcesPaths + portraitsFolderPath + i + ".png"));
        }
        setImageCode(avatarMap.get("backgroundIndex"), avatarMap.get("portraitIndex"), avatarMap.get("frameIndex"));
    }

    private void setImageCode(int backgroundIndex, int potraitIndex, int frameIndex) {
        backgroundImage.setImage(backgroundsList.get(backgroundIndex));
        portraitImage.setImage(portraitsList.get(potraitIndex));
        frameImage.setImage(framesList.get(frameIndex));
    }

    private void sortNewGamesOnTop() {
        this.games.sort(Comparator.comparing(Game::createdAt).reversed());
        this.games.sort(Comparator.comparing(game -> !game.owner().equals(this.tokenStorage.getUserId())));
    }

    //Load list of games as soon as BrowseGame-Screen is shown
    @OnInit
    void init() {
        this.controlResponses = responseConstants.respDelGame;
        this.textInfo = new Text("");

        editGameService = (editGameService == null) ? new EditGameService() : editGameService;
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        browseGameService = (browseGameService == null) ? new BrowseGameService() : browseGameService;
        browseGameService.resetSelectedGame();
        subscriber.subscribe(gamesApiService.findAll(),
                gameList -> Platform.runLater(() -> {
                    games.setAll(gameList);
                    editGameService.setGamesList(games);
                    createGameService.setGamesList(games);
                    this.sortNewGamesOnTop();
                    this.userName.setText(tokenStorage.getName());
                }),
                error -> {
                    bubbleComponent.setErrorMode(true);
                    bubbleComponent.setCaptainText(getErrorInfoText(error));
                }
        );

        // Listener for updating list of games if games are created, deleted or updated
        subscriber.subscribe(eventListener.listen("games.*.*", Game.class),
                event -> Platform.runLater(() -> {
                    switch (event.suffix()) {
                        case "created" -> games.add(event.data());
                        case "update" -> games.replaceAll(g -> g._id().equals(event.data()._id()) ? event.data() : g);
                        case "deleted" -> games.removeIf(g -> g._id().equals(event.data()._id()));
                    }
                    this.sortNewGamesOnTop();
                }),
                error -> this.textInfo.setText(getErrorInfoText(error))
        );
        this.controlResponses = responseConstants.respGetGame;

    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
    }

    //Make list of games visible
    @OnRender
    void render() {
        this.deleteWarningIsInvisible = this.warningWindowContainer.visibleProperty().not();
        if (Objects.isNull(tokenStorage.getAvatar())) {
            avatarMap.put("backgroundIndex", 0);
            avatarMap.put("portraitIndex", 8);
            avatarMap.put("frameIndex", 8);
            tokenStorage.setAvatarMap(avatarMap);
            initializeAvatarImage(avatarMap);
        } else initializeAvatarImage(tokenStorage.getAvatarMap());

        logoutComponent.setParent(warningWindowContainer);
    }

    @OnRender
    public void changeDeleteButtonView() {
        // delete Button has red text and icon when selected and the captain says something different
        this.del_game_b.styleProperty().bind(Bindings.createStringBinding(() -> {
            if (deleteWarningIsInvisible.get()) {
                bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.which.game"));
            } else {
                bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.whiping.off.the.map"));
            }
            return "-fx-text-fill: White";
        }, this.deleteWarningIsInvisible));
    }

    /*
    ============================================= On-Action buttons =============================================
     */

    public void logOut() {
        popup.showPopup(logoutWarningContainer, logoutComponent);
        warningWindowContainer.setStyle("-fx-opacity: 0.5; -fx-background-color: black;");
        warningWindowContainer.setVisible(true);
    }

    public void newGame() {
        app.show("/createGameController");
    }

    public void editGame() {
        if (browseGameService.checkMyGame()) {
            app.show("/editgame");
        }
    }

    public void editAccount() {
        app.show("/editAcc");
    }

    @OnKey(code = KeyCode.SPACE)
    public void loadGame() {
        if (browseGameService.getGame() != null) {
            if (browseGameService.getGame().started()) {
                subscriber.subscribe(lobbyService.getMember(browseGameService.getGame()._id(), tokenStorage.getUserId()),
                        memberDto -> joinGameHelper.joinGame(browseGameService.getGame()._id(), false),
                        error -> bubbleComponent.setCaptainText(resources.getString("pirate.browseGame.game.started")));
            } else {
                app.show("/lobby", Map.of("gameid", browseGameService.getGame()._id()));
            }
        }
    }

    @OnKey(code = KeyCode.DELETE)
    public void deleteGame() {
        if (browseGameService.checkMyGame()) {
            warningComponent.setGameName();
            warningComponent.setView(this.warningWindowContainer);
            popup.showPopup(warningWindowContainer, warningComponent);
            popup.setBlur(gameListAnchorPane, browseGameVBoxButtons);
        } else {
            this.textInfo.setText(getErrorInfoText(403));
        }
    }

    @OnDestroy
    void destroy() {
        subscriber.dispose();
    }
}
