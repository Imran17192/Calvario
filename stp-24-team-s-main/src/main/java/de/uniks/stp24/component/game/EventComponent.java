package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.EffectSourceDto;
import de.uniks.stp24.dto.EffectSourceParentDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.DAY;
import static de.uniks.stp24.service.Constants.NIGHT;

@Component(view = "Event.fxml")
public class EventComponent extends AnchorPane {
    @FXML
    TextFlow descriptionTextFlow;
    @FXML
    AnchorPane anchor;
    @FXML
    ScrollPane descriptionScrollPane;
    @FXML
    Text eventName;
    @FXML
    public ImageView eventImage;
    @FXML
    Button closeEvent;

    @FXML
    Pane shadow;
    @FXML
    StackPane container;

    @Inject
    App app;
    @Inject
    EventService eventService;
    @Inject
    TimerService timerService;

    ClockComponent clockComponent;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;
    @Inject
    EventListener eventListener;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public TokenStorage tokenStorage;

    Pane gameBackground;

    String gameId;

    final ImageCache imageCache = new ImageCache();
    private String lastUpdate = "";

    boolean eventOccured = false;

    ColorAdjust fadeAdjust;
    ColorAdjust unfadeAdjust;
    ColorAdjust brightenAdjust;
    Timeline nightTimeLine;
    Timeline dayTimeLine;
    boolean isDay = true;
    private InGameController inGameController;

    @Inject
    public EventComponent() {

    }

    @OnInit
    public void init() {
        PropertyChangeListener callHandleEventChanged = this::handleEventChanged;
        eventService.listeners().addPropertyChangeListener(EventService.PROPERTY_EVENT, callHandleEventChanged);

        PropertyChangeListener callHandleShowEventChanged = this::handleShowEventChanged;
        timerService.listeners().addPropertyChangeListener(TimerService.PROPERTY_SHOWEVENT, callHandleShowEventChanged);

        createUpdateSeasonsListener();
    }

    @OnDestroy
    public void destroy() {
        eventService.listeners().removePropertyChangeListener(EventService.PROPERTY_EVENT, this::handleEventChanged);
        timerService.listeners().removePropertyChangeListener(TimerService.PROPERTY_SHOWEVENT, this::handleShowEventChanged);
    }

    public void createUpdateSeasonsListener() {
        subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".ticked", Game.class),
                event -> {
                    if (!Objects.equals(lastUpdate, event.data().updatedAt())) {
                        eventService.setNextEventTimer(eventService.getNextEventTimer() - 1);
                        EffectSourceParentDto activeEvent = eventService.getEvent();
                        if (activeEvent != null && !eventOccured) {
                            eventOccured = true;
                            if (activeEvent.effects()[0].id().equals("solarEclipse") && isDay) {
                                this.inGameController.darkenFog();
                                changeToNight();
                            }
                            subscriber.subscribe(eventService.sendEffect(),
                                    result -> {
                                    },
                                    error -> System.out.println("Error beim Senden von Effect: " + error));
                            setRandomEventInfos(activeEvent);
                            show();
                        } else if (activeEvent == null) {
                            eventOccured = false;
                            if (!isDay) {
                                this.inGameController.brightenFog();
                                changeToDay();
                            }
                        }
                        lastUpdate = event.data().updatedAt();
                    }
                },
                Throwable::printStackTrace
        );
    }

    private void handleEventChanged(PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(eventService.getEvent())) {
            setRandomEventInfos(eventService.getEvent());
            show();
        }
    }

    @OnRender
    public void render() {
        String css = Objects.requireNonNull(this.getClass().getResource("/de/uniks/stp24/style/event.css")).toExternalForm();
        this.getStylesheets().add(css);

        gameId = tokenStorage.getGameId();

        fadeAdjust = new ColorAdjust();
        fadeAdjust.setBrightness(0);

        unfadeAdjust = new ColorAdjust();
        unfadeAdjust.setBrightness(0);

        brightenAdjust = new ColorAdjust();
        brightenAdjust.setBrightness(0);

        nightTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(fadeAdjust.brightnessProperty(), fadeAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(7), new KeyValue(fadeAdjust.brightnessProperty(), -0.8, Interpolator.LINEAR)
                ));
        nightTimeLine.setCycleCount(1);
        nightTimeLine.setAutoReverse(false);

        dayTimeLine = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        new KeyValue(unfadeAdjust.brightnessProperty(), unfadeAdjust.brightnessProperty().getValue(), Interpolator.LINEAR)),
                new KeyFrame(Duration.seconds(3), new KeyValue(unfadeAdjust.brightnessProperty(), 0.3, Interpolator.LINEAR)
                ));
        dayTimeLine.setCycleCount(1);
        dayTimeLine.setAutoReverse(false);
        if (this.tokenStorage.isSpectator()) return;
        subscriber.subscribe(empireApiService.getEmpireEffect(gameId, tokenStorage.getEmpireId()),
                event -> {
                    if (event.effects().length > 0) {
                        EffectSourceDto effect = event.effects()[0];
                        EffectSourceParentDto activeEvent = eventService.readEvent(effect.id());
                        if (Objects.nonNull(activeEvent)) {
                            eventOccured = true;
                            if (effect.id().equals("solarEclipse") && isDay) {
                                inGameController.darkenFogNotAnimated();
                                changeToNightNotAnimated();
                            }
                            eventService.setEvent(activeEvent);
                            setRandomEventInfos(activeEvent);
                            show();
                        }
                    }
                },
                error -> System.out.println("Error while loading event: " + error));
    }

    private void handleShowEventChanged(PropertyChangeEvent propertyChangeEvent) {
        if (timerService.getShowEvent()) {
            show();
        } else {
            close();
        }
    }

    // changes String to camelCase
    private @NotNull String convert(@NotNull String id) {
        String[] word = id.split("_");
        for (int i = 1; i < word.length; i++) {
            word[i] = word[i].substring(0, 1).toUpperCase() + word[i].substring(1);
        }
        return String.join("", word);
    }

    public void setRandomEventInfos(@NotNull EffectSourceParentDto event) {
        String id = convert(event.effects()[0].id());

        checkSize(id);

        eventImage.setImage(imageCache.get("icons/events/" + id + "Event.png"));
        eventName.setText(resources.getString("event." + id + ".name"));
        String description = resources.getString("event." + id + ".description");
        TextFlow descriptionTextFlow = createTextFlow(description);
        descriptionTextFlow.getStyleClass().clear();
        this.descriptionTextFlow.getChildren().setAll(descriptionTextFlow.getChildren());
    }

    private TextFlow createTextFlow(String text) {
        TextFlow textFlow = new TextFlow();
        textFlow.getStyleClass().clear();
        String[] lines = text.split("\\s+");

        for (String line : lines) {
            Text textNode = new Text(line + " ");
            textNode.getStyleClass().add("eventDescription");
            if (line.contains("Gain") || line.contains("doubled") ||
                    line.contains("Erhalte") || line.contains("verdoppelt") ||
                    line.matches(".*\\+.*") || line.matches(".*x.*")) {
                textNode.setFill(Color.GREEN);
            } else if (line.contains("Lose") || line.contains("halved") || line.contains("decreased") ||
                    line.contains("Verliere") || line.contains("halbiert") ||
                    line.matches(".*-.*")) {
                textNode.setFill(Color.RED);
            } else {
                textNode.setFill(Color.BLACK);
            }

            textFlow.getChildren().add(textNode);
        }

        return textFlow;
    }

    // reduce size of eventName if it's too long
    private void checkSize(@NotNull String id) {
        if (id.length() - 15 > 0) {
            int num = id.length() % 15;
            int size = 32 - (num / 2);
            eventName.setStyle("-fx-font-size: " + size);
            eventName.setTranslateY(eventName.getTranslateY() + num - 3);
            descriptionScrollPane.setTranslateY(descriptionScrollPane.getTranslateY() + num - 3);
        }

    }

    public void close() {
        container.setVisible(false);
        shadow.setVisible(false);
        timerService.setShowEvent(false);
    }

    public void show() {
        container.setVisible(true);
        shadow.setVisible(true);
        shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
    }

    public void setParent(Pane shadow, StackPane container) {
        this.shadow = shadow;
        this.container = container;
    }

    public void setClockComponent(ClockComponent clockComponent) {
        this.clockComponent = clockComponent;
    }

    public void changeToNight() {
        nightTimeLine.stop();

        gameBackground.setEffect(fadeAdjust);

        nightTimeLine.setOnFinished(event -> {
            gameBackground.setEffect(brightenAdjust);
            changeToNightNotAnimated();
        });

        nightTimeLine.play();
    }

    private void changeToNightNotAnimated() {
        gameBackground.setStyle(NIGHT);
        isDay = false;
    }

    public void changeToDay() {
        dayTimeLine.stop();

        gameBackground.setEffect(unfadeAdjust);

        dayTimeLine.setOnFinished(event -> {
            gameBackground.setEffect(brightenAdjust);
            changeToDayNotAnimated();
        });

        dayTimeLine.play();
    }

    private void changeToDayNotAnimated() {
        gameBackground.setStyle(DAY);
        isDay = true;
    }

    public void setBackground(Pane gameBackground) {
        this.gameBackground = gameBackground;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
