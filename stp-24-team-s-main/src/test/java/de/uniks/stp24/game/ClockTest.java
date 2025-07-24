package de.uniks.stp24.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ClockComponent;
import de.uniks.stp24.component.game.EventComponent;
import de.uniks.stp24.dto.UpdateGameResultDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GamesApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.EventService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class ClockTest extends ControllerTest {

    public static int testOrder = 0;

    @InjectMocks
    ClockComponent clockComponent;
    @InjectMocks
    EventComponent eventComponent;

    @Spy
    TimerService timerService;
    @Spy
    EventService eventService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    final
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    GamesApiService gameApiService;
    @Spy
    EmpireApiService empireApiService;
    @Spy
    EmpireService empireService;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.clockComponent.timerService = this.timerService;
        this.clockComponent.eventService = this.eventService;
        this.clockComponent.subscriber = this.subscriber;

        this.clockComponent.gamesApiService = this.gameApiService;
        this.clockComponent.empireApiService = this.empireApiService;

        this.clockComponent.islandsService = this.islandsService;
        this.clockComponent.eventComponent = this.eventComponent;

        this.timerService.gamesApiService = this.gameApiService;
        this.timerService.subscriber = this.subscriber;
        this.timerService.tokenStorage = this.tokenStorage;

        this.eventService.subscriber = this.subscriber;
        this.eventService.empireApiService = this.empireApiService;
        this.eventService.tokenStorage = this.tokenStorage;

        this.empireService.empireApiService = this.empireApiService;

        Game game0 = new Game("yesterday", "today", "gameID", "gameName", "owner", 3, 1, true, 0, 1, null);
        Game game1 = new Game("yesterday", "today", "gameID", "gameName", "owner", 3, 1, true, 1, 1, null);
        Game game2 = new Game("yesterday", "today", "gameID", "gameName", "owner", 3, 1, true, 2, 1, null);
        Game game3 = new Game("yesterday", "today", "gameID", "gameName", "owner", 3, 1, true, 3, 1, null);

        switch(ClockTest.testOrder) {
            case 0:
                when(gameApiService.getGame(any())).thenReturn(Observable.just(game0));
                when(tokenStorage.getUserId()).thenReturn("owner");
                break;
            case 1:
                when(gameApiService.getGame(any())).thenReturn(Observable.just(game1));
                when(tokenStorage.getUserId()).thenReturn("owner");
                break;
            case 2:
                when(gameApiService.getGame(any())).thenReturn(Observable.just(game2));
                when(tokenStorage.getUserId()).thenReturn("owner");
                break;
            case 3:
                when(gameApiService.getGame(any())).thenReturn(Observable.just(game3));
                when(tokenStorage.getUserId()).thenReturn("owner");
                break;
            default:
                when(gameApiService.getGame(any())).thenReturn(Observable.just(game0));
                when(tokenStorage.getUserId()).thenReturn("spectator");
                break;
        }


        when(tokenStorage.getGameId()).thenReturn("gameID");
        when(tokenStorage.isSpectator()).thenReturn(true);

        app.show(clockComponent);

        clockComponent.getStylesheets().clear();
        eventComponent.getStylesheets().clear();
    }

    @Test
    @Order(2)
    public void startx1() {
        ClockTest.testOrder = 2;
        assertTrue(clockComponent.x1Button.isSelected());
    }

    @Test
    @Order(3)
    public void startx2() {
        ClockTest.testOrder = 3;
        assertTrue(clockComponent.x2Button.isSelected());
    }

    @Test
    @Order(4)
    public void startx3() {
        ClockTest.testOrder = 4;
        assertTrue(clockComponent.x3Button.isSelected());
    }

    @Test
    @Order(1)
    public void changeSpeed() {
        ClockTest.testOrder = 1;

        UpdateGameResultDto gameResult = new UpdateGameResultDto("now", "now", "gameID", "gameName", "owner", true, 0, 1, null);
        when(gameApiService.editSpeed(any(), any())).thenReturn(Observable.just(gameResult))
                .thenReturn(Observable.just(new UpdateGameResultDto("now", "now", "gameID", "gameName", "owner", true, 2, 2, null)))
                .thenReturn(Observable.just(new UpdateGameResultDto("now", "now", "gameID", "gameName", "owner", true, 3, 3, null)))
                .thenReturn(Observable.just(new UpdateGameResultDto("now", "now", "gameID", "gameName", "owner", true, 0, 3, null)));

        assertTrue(clockComponent.pauseClockButton.isSelected());

        WaitForAsyncUtils.waitForFxEvents();
        clickOn(clockComponent.x1Button);
        assertEquals(timerService.getSpeed(), 1);
        clickOn(clockComponent.x2Button);
        assertEquals(timerService.getSpeed(), 2);
        clickOn(clockComponent.x3Button);
        assertEquals(timerService.getSpeed(), 3);
    }

    @Test
    @Order(5)
    public void showFlag() {
        timerService.start();
        ToggleButton toggleButton = clockComponent.flagToggle;
        assertFalse(toggleButton.isSelected());
        press(KeyCode.SHIFT).press(KeyCode.H).release(KeyCode.H).release(KeyCode.SHIFT);
        assertTrue(toggleButton.isSelected());
        press(KeyCode.SHIFT).press(KeyCode.H).release(KeyCode.H).release(KeyCode.SHIFT);
        assertFalse(toggleButton.isSelected());
    }
}
