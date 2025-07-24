package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.ContactDetailsComponent;
import de.uniks.stp24.component.game.ContactsComponent;
import de.uniks.stp24.dto.*;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.WarsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.WarService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ContactsServiceTest extends ControllerTest {

    @Spy
    EmpireApiService empireApiService;
    @Spy
    IslandsService islandsService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    final EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    WarService warService;
    @Spy
    ContactsComponent contactsComponent;
    @Spy
    ContactDetailsComponent contactDetailsComponent;
    @Spy
    WarsApiService warsApiService;

    final EmpirePrivateDto emptyPrivateDto = new EmpirePrivateDto(new HashMap<>());
    EmpirePrivate empirePrivateDto, falsePrivateDto;
    ReadEmpireDto player, enemy, enemy2;
    WarDto meAsDef, meAsAtt;
    protected final Subject<Event<WarDto>> WAR_SUBJECT = BehaviorSubject.create();
    protected final String GAME_ID = "game1";
    protected final String CLIENT_OWNER = "gameOwner";
    List<WarDto> warDtoList = new ArrayList<>();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        contactsService.islandsService = islandsService;
        contactsService.empireApiService = empireApiService;
        contactsService.subscriber = new Subscriber();
        contactsService.tokenStorage = tokenStorage;
        contactsService.warService = warService;
        contactsService.eventListener = eventListener;
        contactsService.contactsComponent = contactsComponent;
        contactsService.contactsComponent.tokenStorage = tokenStorage;
        contactsService.contactsComponent.warService = warService;
        contactsService.contactsComponent.subscriber = new Subscriber();
        contactsService.contactsComponent.warService.warsApiService = warsApiService;
        contactsService.contactsComponent.contactDetailsComponent = contactDetailsComponent;
        contactsService.contactsComponent.contactDetailsComponent.contactsService = contactsService;


        enemy2 = new ReadEmpireDto("today", "today", "empire2", GAME_ID,
          "user2", "dudes", "no description", "color", 3, 3, "enemySystem2");

        enemy = new ReadEmpireDto("today", "today", "empire1", GAME_ID,
          "user1", "brotherhood", "no description", "color", 1, 1, "enemySystem");

        player = new ReadEmpireDto("today", "today", "gameOwner", GAME_ID,
          "player", "boss", "no description", "color", 2, 2, "bossSystem");

        islandsService.saveEmpire(player._id(), player);
        islandsService.saveEmpire(enemy._id(), enemy);
        islandsService.saveEmpire(enemy2._id(), enemy2);

        doReturn(GAME_ID).when(tokenStorage).getGameId();
        doReturn(CLIENT_OWNER).when(tokenStorage).getEmpireId();
        doReturn(enemy).when(islandsService).getEmpire(eq(enemy._id()));
        doReturn(enemy2).when(islandsService).getEmpire(eq(enemy2._id()));



        ArrayList<String> listOfIsles = new ArrayList<>();
        listOfIsles.add("isleNr1");
        Object list = new ArrayList<>(listOfIsles);
        Map<String,Object> mapPrivate = new HashMap<>();
        mapPrivate.put("empire1", list);
        mapPrivate.put("empire2",new ArrayList<String>());
        Map<String,Object> falsePrivate = new HashMap<>();
        falsePrivate.put("emp",false);
        empirePrivateDto = new EmpirePrivate(mapPrivate);
        falsePrivateDto = new EmpirePrivate(falsePrivate);

        meAsDef = new WarDto("today", "today", "defWar", GAME_ID, enemy2._id(), player._id(), "" );
        meAsAtt = new WarDto("today", "today", "attWar", GAME_ID, player._id(), enemy._id(),"" );

        warDtoList.add(meAsDef);
        warDtoList.add(meAsAtt);
        doNothing().when(contactsComponent).loadEmpireWars();
    }

    @Test
    public void addEnemyTest() {
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());
        doNothing().when(contactsService).saveContacts();
        doNothing().when(contactsService).createWarListener();
        doNothing().when(contactDetailsComponent).checkWarSituation();
        assertTrue(contactsService.hiddenEmpires.isEmpty());
        assertNull(contactsService.gameID);
        assertNull(contactsService.myOwnEmpireID);
        contactsService.getEmpiresInGame();
        assertTrue(contactsService.seenEnemies.isEmpty());
        assertEquals("gameOwner", contactsService.myOwnEmpireID);
        assertEquals("game1", contactsService.gameID);

        contactsService.addEnemy("empire1","isleNr1");
        assertEquals(1, contactsService.seenEnemies.size());

        contactsService.addEnemy("empire1", "isleNr2");
        assertEquals(1, contactsService.seenEnemies.size());

        contactsService.addEnemyInPeace("empire2");
        assertEquals(2, contactsService.seenEnemies.size());

    }

    @Test
    public void AddEnemyAfterWarDeclarationTest() {
        System.out.println(islandsService.getEmpire("empire2"));
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());
        doNothing().when(contactsService).saveContacts();
//        doNothing().when(contactsService).createWarListener();
        when(this.eventListener.listen("games." + GAME_ID + ".wars.*.*", WarDto.class)).thenReturn(WAR_SUBJECT);
        CreateWarDto createWarDto = new CreateWarDto(player._id(), enemy2._id(),"" );
        WarDto meAsAtt2 = new WarDto("", "", "attWar2", GAME_ID, player._id(), enemy2._id(),"" );
        doReturn(Observable.just(meAsDef)).when(warService).deleteWar(eq(GAME_ID),eq("defWar"));
        doReturn(Observable.just(meAsAtt2)).when(warService).createWar(eq(GAME_ID),eq(createWarDto));
        doNothing().when(contactsService.contactsComponent.contactDetailsComponent)
          .setWarMessagePopup(any(),any(),any(),any(WarDto.class));
        doNothing().when(contactsService.contactsComponent.contactDetailsComponent)
          .checkWarSituation();

        contactsService.getEmpiresInGame();
        assertTrue(contactsService.warsInThisGame.isEmpty());
        contactsService.addWarInformation(warDtoList);
        assertFalse(contactsService.warsInThisGame.isEmpty());
        WAR_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".wars.*.created",meAsDef));
//        contactsService.addEnemyAfterDeclaration("empire2");
        assertTrue(contactsService.attacker("empire2"));

        sleep(100);
        contactsService.addEnemyAfterDeclaration("empire1");
        sleep(100);
        assertTrue(contactsService.defender("empire1"));

        System.out.println(contactsService.warsOnProcess.size());

        contactsService.stopWarWith("empire2");
        sleep(100);
        assertEquals(1,contactsService.warsOnProcess.size());

        contactsService.startWarWith("empire2");
        sleep(100);

    }

    @Test
    public void loadDataTest() {
        doReturn(Observable.just(empirePrivateDto)).when(empireApiService).getPrivate(any(),any());
        assertTrue(contactsService.seenEnemies.isEmpty());
        doNothing().when(contactsService).createWarListener();
        contactsService.getEmpiresInGame();
        sleep(1000);
        assertEquals(2, contactsService.seenEnemies.size());
        Contact contact = contactsService.seenEnemies.getFirst();
        assertEquals(1, contact.getDiscoveredIslands().size());
        assertTrue(contact.getDiscoveredIslands().contains("isleNr1"));
        assertFalse(contact.getDiscoveredIslands().contains("isleNr2"));

    }
    @Test
    public void loadFalseDataTest() {
        doReturn(Observable.just(falsePrivateDto)).when(empireApiService).getPrivate(any(),any());
        doNothing().when(contactsService).createWarListener();

        assertTrue(contactsService.seenEnemies.isEmpty());
        contactsService.getEmpiresInGame();

        sleep(100);
        assertEquals(0, contactsService.seenEnemies.size());
        contactsService.addEnemy("empire1","isleNr1");
        contactsService.addEnemyAfterDeclaration("empire2");

    }

    @Test
    public void saveDateTest() {
        doReturn(Observable.just(emptyPrivateDto)).when(empireApiService).getPrivate(any(),any());
        //doReturn(Observable.just(empirePrivateDto))
          //.when(empireApiService).savePrivate(any(),any(),any(EmpirePrivateDto.class));
        doNothing().when(contactsService).createWarListener();
        contactsService.getEmpiresInGame();
        contactsService.addEnemy("empire1","isleNr1");
        contactsService.addEnemyAfterDeclaration("empire2");

    }

}
