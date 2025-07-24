package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ContactDetailsComponent;
import de.uniks.stp24.component.game.ContactsComponent;
import de.uniks.stp24.component.game.WarComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.WarsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.WarService;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.application.Platform;

import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class ContactComponentTest extends ControllerTest {

    @InjectMocks
    ContactsComponent contactsComponent;
    @InjectMocks
    WarComponent warComponent;
    @Spy
    ContactDetailsComponent contactDetailsComponent;
    @Spy
    ImageCache imageCache;
    @Spy
    ObjectMapper objectMapper;

    // for ContactsService

    @Spy
    EmpireApiService empireApiService;
    @Spy
    IslandsService islandsService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    GameLogicApiService gameLogicApiService;
    @Spy
    WarService warService;
    @Spy
    WarsApiService warsApiService;
    @Spy
    final de.uniks.stp24.ws.EventListener eventListener = new EventListener(tokenStorage, objectMapper);


    final EmpirePrivateDto emptyPrivateDto = new EmpirePrivateDto(new HashMap<>());
    EmpirePrivateDto empirePrivateDto;
    EmpirePrivateDto falsePrivateDto;
    ReadEmpireDto enemy;
    List<WarDto> warDtoList = new ArrayList<>();



    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        contactsComponent.app = app;
        contactsComponent.contactsService = contactsService;
        contactsComponent.imageCache = imageCache;
        contactsComponent.contactDetailsComponent = contactDetailsComponent;
        contactsComponent.subscriber = new Subscriber();

        contactsService.islandsService = islandsService;
        contactsService.empireApiService = empireApiService;
        contactsService.subscriber = new Subscriber();
        contactsService.tokenStorage = tokenStorage;
        contactsService.warService = warService;
        contactsService.warService.warsApiService = warsApiService;
        contactsService.contactsComponent = contactsComponent;

        contactDetailsComponent.islandsService = islandsService;
        contactDetailsComponent.imageCache = imageCache;
        contactDetailsComponent.resources = gameResourceBundle;
        contactDetailsComponent.warComponent = warComponent;
        contactDetailsComponent.contactsService = contactsService;

        islandsService.gameLogicApiService = gameLogicApiService;
        islandsService.subscriber = new Subscriber();

        ReadEmpireDto player = new ReadEmpireDto("today", "today", "gameOwner", "game1",
          "player", "boss", "no description", "color", 2, 2, "bossSystem");

        enemy = new ReadEmpireDto("today", "today", "empire1", "game1",
          "user1", "brotherhood", "no description", "color", 1, 1, "enemySystem");


        islandsService.saveEmpire("gameOwner", player);
        islandsService.saveEmpire("empire1", enemy);

        doReturn("game1").when(tokenStorage).getGameId();
        doReturn("gameOwner").when(tokenStorage).getEmpireId();
        doReturn(enemy).when(islandsService).getEmpire(any());
        doReturn(Observable.just(warDtoList)).when(warService).getWars(any(),any());
        doNothing().when(contactsService).createWarListener();

        ArrayList<String> listOfIsles = new ArrayList<>();
        listOfIsles.add("isleNr1");
        Object list = new ArrayList<>(listOfIsles);
        Map<String,Object> mapPrivate = new HashMap<>();
        mapPrivate.put("empire1", list);
        Map<String,Object> falsePrivate = new HashMap<>();
        falsePrivate.put("emp",false);
        empirePrivateDto = new EmpirePrivateDto(mapPrivate);
        falsePrivateDto = new EmpirePrivateDto(falsePrivate);
        ArrayList<String> buildings = new ArrayList<>(Arrays.asList("power_plant", "mine", "farm",
          "research_lab", "foundry", "factory", "refinery"));

        ShortSystemDto short1 = new ShortSystemDto("empire1", "isleNr1", "regular","isleName",
          Map.of("city",2, "industry", 3, "mining",4, "energy",5, "agriculture",6),
          Map.of("city",2, "industry", 2, "mining",3, "energy",4, "agriculture",6)
          ,30 ,buildings, Upgrade.developed,34,100);
        List<ShortSystemDto> listShort = new ArrayList<>();
        listShort.add(short1);
        doReturn(listShort).when(islandsService).getDevIsles();
        AggregateResultDto value = new AggregateResultDto(2, null);

        doReturn(Observable.just(value)).when(gameLogicApiService).getCompare(any(),any(),any());
        contactDetailsComponent.setParent(contactsComponent);
        app.show(this.contactsComponent);
        contactsComponent.getStylesheets().clear();

        doNothing().when(contactDetailsComponent).updateWarButtonText();
        doNothing().when(contactDetailsComponent).checkWarSituation();
    }

    @Test
    public void showContacts() {
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());

        doNothing().when(contactsService).saveContacts();
        contactsService.getEmpiresInGame();
        sleep(1000);
        contactsService.addEnemy("empire1","isleNr1");
        waitForFxEvents();
        Contact cont = contactsComponent.contactsListView.getItems().getFirst();
        cont.setPane(contactDetailsComponent);
        sleep(1000);
        Platform.runLater(() -> {
            app.show(contactDetailsComponent);

        });
//        contactDetailsComponent.setVisible(true);
        contactDetailsComponent.getStylesheets().clear();

        waitForFxEvents();
        sleep(1000);
        contactDetailsComponent.setContactInformation(cont);
        waitForFxEvents();
        sleep(1000);
    }


}


