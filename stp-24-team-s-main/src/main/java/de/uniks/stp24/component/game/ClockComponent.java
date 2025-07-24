package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.GameStatService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

@Component(view = "Clock.fxml")
public class ClockComponent extends AnchorPane {

    @FXML
    Label otherSpeedLabel;
    @FXML
    ImageView spectatorImage;
    @FXML
    VBox clockVBox;
    @FXML
    public ToggleButton flagToggle;
    @FXML
    public Button randomEventButton;
    @FXML
    public Label remainingSeasonsLabel;
    @FXML
    AnchorPane anchor;
    @FXML
    ToggleGroup speed;
    @FXML
    public RadioButton x1Button;
    @FXML
    public RadioButton x2Button;
    @FXML
    public RadioButton x3Button;
    @FXML
    public RadioButton pauseClockButton;
    @FXML
    Label seasonLabel;

    String gameId;

    Game game;

    final ImageCache imageCache = new ImageCache();
    @Inject
    public EventService eventService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public TimerService timerService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public GamesApiService gamesApiService;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public GameStatService gameStatService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;
    @Inject
    public InGameService inGameService;

    InGameController inGameController;

    private int lastUpdateSeason = -1;
    private String lastUpdateSpeed = "";
    private boolean updateSeasonLabel = false;

    @Inject
    public EventComponent eventComponent;

    @Inject
    public ClockComponent() {

    }

    @OnInit
    public void init() {
        timerService.onLoadingFinished(this::setSeasonLabelSize);
        timerService.start();

        gameId = tokenStorage.getGameId();

        PropertyChangeListener callHandleSpeedChanged = this::handleSpeedChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SPEED, callHandleSpeedChanged);

        PropertyChangeListener callHandleRemainingSeasons = this::handleRemainingSeasonChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_REMAININGSEASONS, callHandleRemainingSeasons);

        PropertyChangeListener callHandleEventChanged = this::handleEventChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_EVENT, callHandleEventChanged);

        subscriber.subscribe(gamesApiService.getGame(tokenStorage.getGameId()), gameResult -> {
            this.inGameService.setGameOwnerID(gameResult.owner());
            game = gameResult;
            }, error -> System.out.println("Error: " + error.getMessage())
        );

        createUpdateSeasonListener();
        createUpdateSpeedListener();

        timerService.resume();
    }

    public void setClockButtons(Game game) {

        if (Objects.equals(game.owner(), tokenStorage.getUserId())) {
            return;
        }

        otherSpeedLabel.setVisible(false);
        pauseClockButton.setVisible(false);
        x1Button.setVisible(false);
        x2Button.setVisible(false);
        x3Button.setVisible(false);

        switch (game.speed()) {
            case 0:
                pauseClockButton.setDisable(true);
                pauseClockButton.setStyle("-fx-opacity: 1;");
                pauseClockButton.setVisible(true);
                break;
            case 1:
                x1Button.setDisable(true);
                x1Button.setStyle("-fx-opacity: 1;");
                x1Button.setVisible(true);
                break;
            case 2:
                x2Button.setDisable(true);
                x2Button.setStyle("-fx-opacity: 1;");
                x2Button.setVisible(true);
                break;
            case 3:
                x3Button.setDisable(true);
                x3Button.setStyle("-fx-opacity: 1;");
                x3Button.setVisible(true);
                break;
            default:
                otherSpeedLabel.setVisible(true);
                otherSpeedLabel.setStyle("-fx-opacity: 1");
                otherSpeedLabel.setText("x" + game.speed());
                break;
        }
    }

    @OnRender
    public void render() {

        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/clock.css")).toExternalForm();
        this.getStylesheets().add(css);

        // adding spectator sign
        if (tokenStorage.isSpectator()) {
            spectatorImage.setImage(imageCache.get("icons/spectatorSign.png"));
        }

        subscriber.subscribe(gamesApiService.getGame(tokenStorage.getGameId()),
                game -> {

                    otherSpeedLabel.setVisible(false);

                    setClockButtons(game);
                    // Set Clock and Season for the current Game
                    if (game.owner().equals(tokenStorage.getUserId())) {
                        switch (game.speed()) {
                            case 0:
                                pauseClockButton.setSelected(true);
                                break;
                            case 1:
                                x1Button.setSelected(true);
                                break;
                            case 2:
                                x2Button.setSelected(true);
                                break;
                            case 3:
                                x3Button.setSelected(true);
                                break;
                        }
                    }
                    timerService.setSpeedLocal(game.speed());
                    timerService.setSeason(game.period());

                    },
                error -> System.out.println("Error on getting game: " + error)
        );

        setSeasonLabelSize();
        timerService.onGameTicked(this::setSeasonLabelSize);


        remainingSeasonsLabel.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        subscriber.dispose();

        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_SPEED, this::handleSpeedChanged);
        timerService.stop();
        timerService.dispose();
    }

/////////////---------------------------------------WS-EventListeners---------------------------------//////////////

    public void createUpdateSeasonListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + gameId + ".ticked", Game.class),
                event -> {
                    if (!(lastUpdateSeason == event.data().period())) {
                        Game game = event.data();
                        timerService.setSeason(game.period());
                        if (!Objects.equals(tokenStorage.getUserId(), game.owner())) {
                            timerService.reset();
                        }
                        lastUpdateSeason = game.period();
                        inGameController.updateVariableDependencies();
//                        System.out.println("season");
//                        islandsService.refreshListOfColonizedSystems();
                        gameStatService.checkState();
                    }
                },
                Throwable::printStackTrace
        );
    }

    public void createUpdateSpeedListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + gameId + ".updated", Game.class),
                event -> {
                    if (!lastUpdateSpeed.equals(event.data().updatedAt())) {
                        Game game = event.data();
                        timerService.setSpeedLocal(game.speed());

                        setClockButtons(game);

                        lastUpdateSpeed = event.data().updatedAt();
                    }
                },
               Throwable::printStackTrace
        );
    }

///////////////--------------------------------------------onAction------------------------------------/////////////

    public void showFlags() {
        islandsService.setFlag(flagToggle.isSelected());
    }

    @OnKey(code = KeyCode.H, shift = true)
    public void setSelected() {
        flagToggle.setSelected(!flagToggle.isSelected());
        islandsService.keyCodeFlag = !islandsService.keyCodeFlag;
    }

    public void pauseClock() {
        if (timerService.isRunning()) {
            subscriber.subscribe(timerService.setSpeed(gameId, 0),
                    result -> {},
                    error -> System.out.println("Error on pause: " + error)
            );
            timerService.stop();
        }
    }

    public void x3() {
        changingSpeed(3);
    }

    public void x2() {
        changingSpeed(2);
    }

    public void x1() {
        changingSpeed(1);
    }

    public void showEvent() {
        timerService.setShowEvent(true);
    }

////////////--------------------------------Auxiliary Methods-----------------------------------------//////////////

    // Set size of seasonLabel in case of long season number
    private void setSeasonLabelSize() {
        int season = this.timerService.getServerSeasons();

        if (season < 10 && !updateSeasonLabel) seasonLabel.setStyle("-fx-font-size: 25px;");
        if (season < 100 && !updateSeasonLabel) seasonLabel.setStyle("-fx-font-size: 20px;");
        if (season > 999 && !updateSeasonLabel) seasonLabel.setStyle("-fx-font-size: 18px;");

        seasonLabel.setText(season + "");
    }

    public void changingSpeed(int speed) {
        if (!timerService.isRunning()) {
            timerService.resume();
        }
        timerService.setSpeedLocal(speed);
        subscriber.subscribe(timerService.setSpeed(gameId, speed),
                result -> {},
                error -> System.out.println("Error when changing speed: " + error)
        );
    }

////////////--------------------------------PropertyChangeListener--------------------------------------////////////

    private void handleEventChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {

        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            randomEventButton.setVisible(true);
            remainingSeasonsLabel.setVisible(true);

            String eventType = eventService.getEvent().effects()[0].eventType();
            String medallionPath = "/de/uniks/stp24/assets/events/" + eventType + "Event.png";
            randomEventButton.setStyle("-fx-background-image: url('[MEDALLION]')".replace("[MEDALLION]", medallionPath));
        }
    }

    private void handleRemainingSeasonChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int remainingSeasons = (int) propertyChangeEvent.getNewValue();
            if (remainingSeasons <= 0) {

                // Delete event on Server
                eventService.setEvent(null);
                subscriber.subscribe(eventService.sendEffect(),
                        result -> {},
                        error -> {}
                );
                randomEventButton.setVisible(false);
                remainingSeasonsLabel.setVisible(false);
            } else {
                Platform.runLater(() -> remainingSeasonsLabel.setText(String.valueOf(remainingSeasons)));
            }
        }
    }

    private void handleSpeedChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            int speed = (int) propertyChangeEvent.getNewValue();
            timerService.setSpeedLocal(speed);
        }
    }

    public void setToggle(boolean visibility) {
        this.flagToggle.setSelected(visibility);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}