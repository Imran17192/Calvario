package de.uniks.stp24.service.game;

import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.dto.UpdateSpeedDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.beans.PropertyChangeSupport;
import java.util.*;

@Singleton
public class TimerService {
    @Inject
    GameStatus gameStatus;
    @Inject
    public GamesApiService gamesApiService;
    @Inject
    public EventListener eventListener;
    @Inject
    public Subscriber subscriber;
    @Inject
    public TokenStorage tokenStorage;

    private Timer timer = new Timer();
    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_COUNTDOWN = "countdown";
    public static final String PROPERTY_SPEED = "speed";
    public static final String PROPERTY_SEASON = "season";
    public static final String PROPERTY_SHOWEVENT = "showEvent";
    private volatile boolean showEvent = false;
    private volatile boolean isRunning = false;
    private int countdown = 0, season, speed;

    private final List<Runnable> periodFunctions = new ArrayList<>();
    private final List<Runnable> speedFunctions = new ArrayList<>();
    private final List<Runnable> onLoadingFinishedFunctions = new ArrayList<>();
    private int listenerPeriod = -1, listenerSpeed = 0;

    public Game game;

    @Inject
    public TimerService() {

    }

    public void dispose() {
        this.subscriber.dispose();
        this.periodFunctions.clear();
        this.speedFunctions.clear();
    }

    public void initializeGameTickListener() {
        this.subscriber.subscribe(this.eventListener.listen(String.format(
                "games.%s.updated", this.tokenStorage.getGameId()), Game.class), result -> {
            Game data = result.data();
            if (this.listenerPeriod != data.period()) {
                this.listenerPeriod = data.period();
                periodFunctions.forEach(Runnable::run);
            }
            if (this.listenerSpeed != data.speed()) {
                this.listenerSpeed = data.speed();
                speedFunctions.forEach(Runnable::run);
            }
        }, Throwable::printStackTrace);
    }

    // getSpeed method doesn't update the speed when I set it to 0 and the service breaks when I try to change it lol
    public int getServerSpeed() {
        return this.listenerSpeed;
    }

    public int getServerSeasons() {
        return this.listenerPeriod;
    }

    public void onGameTicked(Runnable func) {
        this.periodFunctions.add(func);
    }

    public void onLoadingFinished(Runnable func) {
        this.onLoadingFinishedFunctions.add(func);
    }

    public void onSpeedChanged(Runnable func) {
        this.speedFunctions.add(func);
    }

    public void setShowEvent(boolean showEvent) {
        if (showEvent == this.showEvent) {
            return;
        }
        final boolean oldValue = this.showEvent;
        this.showEvent = showEvent;
        this.firePropertyChange(PROPERTY_SHOWEVENT, oldValue, showEvent);
    }

    public boolean getShowEvent() {
        return showEvent;
    }

    /**
     * After changing the speed,
     * the local countdown till next season will be updated
     */
    public Observable<UpdateGameResultDto> setSpeed(String gamesid, int speed) {
        return gamesApiService
                .editSpeed(gamesid, new UpdateSpeedDto(speed))
                .doOnNext(updateGameResultDto -> setSpeedLocal(updateGameResultDto.speed()));
    }

    public void start() {

        if (timer == null) {
            timer = new Timer();
        }

        subscriber.subscribe(gamesApiService.getGame(tokenStorage.getGameId()),
                gameResult -> {
                    game = gameResult;
                    listenerSpeed = gameResult.speed();
                    listenerPeriod = gameResult.period();
                    this.onLoadingFinishedFunctions.forEach(Runnable::run);
                    this.initializeGameTickListener();
                },
                error -> System.out.println("Error: " + error.getMessage())
        );

        if (speed == 0) {
            return;
        }

        isRunning = true;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isRunning) {
                    return;
                }

                countdown++;

                if ((countdown % (60 / speed) == 0) && (Objects.equals(game.owner(), tokenStorage.getUserId()))) {
                    subscriber.subscribe(gamesApiService.updateSeason(tokenStorage.getGameId(), new UpdateSpeedDto(speed), true),
                            gameResult -> {
                                setSeason(game.period());
                                reset();
                            },
                            error -> System.out.println("Error: " + error.getMessage())
                    );
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    public void stop() {
        isRunning = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void resume() {
        isRunning = true;
        timer = new Timer();
        start();
    }

    public void reset() {
        setCountdown(0);
    }

    public void setSeason(int value) {
        int oldValue;
        if (value == this.season) {
            return;
        }
        oldValue = this.season;
        this.season = value;
        this.firePropertyChange(PROPERTY_SEASON, oldValue, value);
    }

    public void setCountdown(int value) {
        int oldValue;
        if (value == this.countdown) {
            return;
        }
        oldValue = this.countdown;
        this.countdown = value;
        this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
    }

    public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.listeners != null) {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public PropertyChangeSupport listeners() {
        if (this.listeners == null) {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeedLocal(int value) {
        if (value == 0) {
            stop();
        } else {
            if (!isRunning) {
                resume();
            }
            int oldValue;
            if (value == this.speed) {
                return;
            }
            oldValue = this.speed;
            this.speed = value;

            stop();
            resume();

            this.firePropertyChange(PROPERTY_COUNTDOWN, oldValue, value);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}