package de.uniks.stp24.ingameTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.StorageOverviewComponent;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.dto.AggregateResultDto;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.ExplanationService;
import de.uniks.stp24.service.game.ResourcesService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class StorageComponentTest extends ControllerTest {
    @Spy
    EmpireApiService empireApiService;
    @Spy
    Subscriber subscriber = spy(Subscriber.class);
    @Spy
    ResourcesService resourcesService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    EmpireService empireService;
    @Spy
    ExplanationService explanationService;
    @Spy
    ImageCache imageCache;

    @InjectMocks
    StorageOverviewComponent storageOverviewComponent;

    final Subject<Event<EmpireDto>> empireDtoSubject = BehaviorSubject.create();
    final Subject<Event<Game>> gameSubject = BehaviorSubject.create();

    final Map<String, Double> resources1 = new LinkedHashMap<>() {{
        put("energy", 3.0);
        put("population", 2.0);
    }};
    final Map<String, Double> resources2 = new LinkedHashMap<>() {{
        put("energy", 4.0);
        put("population", 4.0);
    }};
    final Map<String, Double> resources3 = new LinkedHashMap<>() {{
        put("energy", 5.0);
        put("population", 4.0);
    }};


    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        explanationService.app = this.app;
        storageOverviewComponent.explanationService = this.explanationService;
        // Mock TokenStorage
        doReturn("testGameID").when(this.tokenStorage).getGameId();
        doReturn("testEmpireID").when(this.tokenStorage).getEmpireId();


        // Mock getEmpire
        doReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                "a","a",1, 2, "a", new String[]{"1"}, resources1 ,
                null))).when(this.empireService).getEmpire(any(),any());

        // Mock empire listener
        doReturn(empireDtoSubject).when(this.eventListener).listen(eq("games.testGameID.empires.testEmpireID.updated"), eq(EmpireDto.class));

        // Mock season listener
        doReturn(gameSubject).when(this.eventListener).listen(eq("games.testGameID.ticked"), eq(Game.class));

        doReturn(Observable.just(new AggregateResultDto(1,null))).when(this.empireService).getResourceAggregates(any(),any());

        this.app.show(this.storageOverviewComponent);
        storageOverviewComponent.getStylesheets().clear();
    }

    @Test
    public void updateResourcesWithEmpireUpdate(){
        waitForFxEvents();

        // resourceList: 3 energy, 2 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(2,resourcesService.getResourceCount("population"));

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources2 ,
                        null)));
        waitForFxEvents();

        // resourceList: 4 energy, 4 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(4,resourcesService.getResourceCount("population"));

        empireDtoSubject.onNext(new Event<>("games.testGameID.empires.testEmpireID.updated",
                new EmpireDto("a","b","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources3 ,
                        null)));
        waitForFxEvents();

        // resourceList: 5 energy, 4 population
        assertEquals(2, storageOverviewComponent.resourceListView.getItems().size());
        assertEquals(5,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(4,resourcesService.getResourceCount("population"));
    }


    @Test
    public void updateResourcesWithSeasonChange() {
        waitForFxEvents();
        AggregateItemDto energyAggregate = new AggregateItemDto("energy",4,1 );
        AggregateItemDto populationAggregate = new AggregateItemDto("population",4,2);
        AggregateResultDto aggregateResultDto = new AggregateResultDto(8, new AggregateItemDto[]{energyAggregate, populationAggregate});

        // Mock getEmpire (second time)
        when(this.empireService.getEmpire(any(),any()))
                .thenReturn(Observable.just(new EmpireDto("a","a","testEmpireID", "testGameID","testUserID","testEmpire",
                        "a","a",1, 2, "a", new String[]{"1"}, resources2 ,
                        null)));

        // Mock get aggregates
        when(this.empireService.getResourceAggregates(any(),any())).thenReturn(Observable.just(aggregateResultDto));

        assertEquals(3,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(0,storageOverviewComponent.resourceListView.getItems().getFirst().changePerSeason());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(0,storageOverviewComponent.resourceListView.getItems().getLast().changePerSeason());


        // Season change: energy +1, population +2
        gameSubject.onNext(new Event<>("games.testGameID.ticked",
                new Game("a","b","testGameID","testGame", "testUserID", 2,
                        0, true, 2, 1, null)));
        waitForFxEvents();

        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getFirst().count());
        assertEquals(1,storageOverviewComponent.resourceListView.getItems().getFirst().changePerSeason());
        assertEquals(4,storageOverviewComponent.resourceListView.getItems().getLast().count());
        assertEquals(2,storageOverviewComponent.resourceListView.getItems().getLast().changePerSeason());
    }

}

