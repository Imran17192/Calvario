package de.uniks.stp24.ingameTests;

import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.component.game.IslandComponent;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class IslandsServiceTest extends ControllerTest {
    @Spy
    GameSystemsApiService gameSystemsApiService;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    ImageCache imageCache;
    @InjectMocks
    IslandComponent islandComponent; //= spy(IslandComponent.class);

    final SystemDto[] systems = new SystemDto[3];
    List<IslandComponent> testIsleComps;

    @Override
    public void start(Stage stage) throws Exception{
        super.start(stage);
        this.islandsService.app = this.app;
        islandsService.gameSystemsService = this.gameSystemsApiService;
        islandsService.tokenStorage = this.tokenStorage;
        islandsService.imageCache = this.imageCache;
        doReturn(null).when(this.app).show("/ingame");
        islandsService.saveEmpire("empire",new ReadEmpireDto("a","b","empire","game1","user1","name",
                "description","#FFDDEE",2,3,"home"));
        ArrayList<String> buildings = new ArrayList<>(Arrays.asList("power_plant", "mine", "farm", "research_lab", "foundry", "factory", "refinery"));
        systems[0] = new SystemDto("a","b","system1","game1","agriculture",
                "name",null,null,25,null, Upgrade.unexplored,0,
                Map.of("home",22),1.46,-20.88,null, 100);
        systems[1] = new SystemDto("a","b","system2","game1","energy",
                "name",null,
          Map.of("city",2, "industry", 3, "mining",4, "energy",5, "agriculture",6),
          26,buildings, Upgrade.unexplored,0,
                Map.of("home",18),-7.83,-11.04,"empire", 100);
        systems[2] = new SystemDto("a","b","home","game1","uninhabitable_0", "name",
                Map.of("city",2, "industry", 3, "mining",4, "energy",5, "agriculture",6),
                Map.of("city",2, "industry", 2, "mining",3, "energy",4, "agriculture",6), 22,
                buildings,Upgrade.developed,25,Map.of("system1",22,"system2",18),-5.23,4.23,"empire", 100
        );
        doReturn(Observable.just(systems)).when(gameSystemsApiService).getSystems(any());

    }

    @Test
    public void createIslandData(){
        assertEquals(0,islandsService.getListOfIslands().size());
        islandsService.retrieveIslands("game1", false);
        gameSystemsApiService.getSystems("game1");

        List<Island> testIsles = islandsService.getListOfIslands();

        sleep(100);

        testIsleComps = islandsService.createIslands(testIsles);
        Map<String,IslandComponent> testIsleMap = islandsService.getComponentMap();
        assertEquals(3,testIsles.size());
        assertEquals(3,testIsleComps.size());
        assertEquals(3,testIsleMap.size());
        List<Line> lines = islandsService.createLines(testIsleMap);
        assertEquals(2,lines.size());
        assertNotNull(islandsService.getEmpire("empire"));
        assertNotEquals(0,islandsService.getMapWidth());
        assertNotEquals(0,islandsService.getMapHeight());
        assertEquals(2,islandsService.getSiteManagerSize());
        assertEquals(37,islandsService.getAllNumberOfSites("empire"));
        assertEquals(17,islandsService.getCapacityOfOneSystem("home"));
        assertEquals(9,islandsService.getNumberOfSites("empire","energy"));

    }



}