package de.uniks.stp24.battles;

import de.uniks.stp24.appTestModules.IngameModule;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Fleets;
import io.reactivex.rxjava3.core.Observable;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BattlesModule extends IngameModule {

    @Override
    protected void reassignData() {
        super.reassignData();
        GAME_SYSTEMS = new SystemDto[] {
                new SystemDto("0", "0", "empireIslandID", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50,
                        EMPIRE_ID, 0),
                new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "EnemyIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 70, 50,
                        ENEMY_EMPIRE_ID, 0),
                new SystemDto("0", "0", "wildIslandID", GAME_ID, "regular", "WildIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.unexplored, 13, new HashMap<>(), 30, 50,
                        null, 0),
                new SystemDto("0", "0", "battleIslandID", GAME_ID, "regular", "BattleIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.unexplored, 13, new HashMap<>(), 30, 70,
                        null, 0),
                new SystemDto("0", "0", "empireIslandID2", GAME_ID, "regular", "EmpireIsland",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of(
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 100, 100,
                        ENEMY_EMPIRE_ID, 0)
        };
        FLEET_DTOS = new ArrayList<>(List.of(
                new Fleets.ReadFleetDTO("a", "a",
                        "empireFleetToWild", GAME_ID, EMPIRE_ID, "EmpireFleetUno", GAME_SYSTEMS[2]._id(),
                        4, new HashMap<>(), new HashMap<>()),
                new Fleets.ReadFleetDTO("a", "a",
                        "empireFleetToEnemy", GAME_ID, EMPIRE_ID, "EmpireFleetDos", GAME_SYSTEMS[3]._id(),
                        4, new HashMap<>(), new HashMap<>()),
                new Fleets.ReadFleetDTO("a", "a",
                        "empireFleetToIsland", GAME_ID, EMPIRE_ID, "EmpireFleetTres", GAME_SYSTEMS[1]._id(),
                        4, new HashMap<>(), new HashMap<>()),
                new Fleets.ReadFleetDTO("a", "a",
                        "wildFleet", GAME_ID, null, "WildFleet", GAME_SYSTEMS[2]._id(),
                        4, new HashMap<>(), new HashMap<>()),
                new Fleets.ReadFleetDTO("a", "a",
                        "enemyFleetBattle", GAME_ID, ENEMY_EMPIRE_ID, "EnemyFleetUno", GAME_SYSTEMS[3]._id(),
                        4, new HashMap<>(), new HashMap<>()),
                new Fleets.ReadFleetDTO("a", "a",
                        "enemyFleetEmpire", GAME_ID, ENEMY_EMPIRE_ID, "EnemyFleetDos", GAME_SYSTEMS[0]._id(),
                        4, new HashMap<>(), new HashMap<>())
        ));
    }

    @Override
    protected void loadUnloadableData() {
        super.loadUnloadableData();
        this.contactsService.addWarInformation(List.of(new WarDto(
                "0", "0", "warID", GAME_ID, EMPIRE_ID, ENEMY_EMPIRE_ID, "War")));
    }

    @Override
    protected void initializeApiMocks() {
        super.initializeApiMocks();

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));

    }
}
