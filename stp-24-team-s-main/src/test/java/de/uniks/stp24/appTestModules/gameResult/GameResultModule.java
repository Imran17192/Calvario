package de.uniks.stp24.appTestModules.gameResult;

import de.uniks.stp24.appTestModules.IngameModule;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.dto.Upgrade;
import io.reactivex.rxjava3.core.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GameResultModule extends IngameModule {

    @Override
    protected void reassignData() {
        super.reassignData();

        GAME_SYSTEMS = new SystemDto[] {
                new SystemDto("0", "0", "empireIslandID", GAME_ID, "regular", "TestIslandOne",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of("energy",
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50, EMPIRE_ID, 0),
                new SystemDto("0", "0", "enemyIslandID", GAME_ID, "regular", "TestIslandTwo",
                        Map.of("energy", 13), Map.of("energy", 0), 23, new ArrayList<>(List.of("energy",
                        "shipyard", "mine", "research")), Upgrade.colonized, 13, new HashMap<>(), 50, 50, ENEMY_EMPIRE_ID, 0)
        };
    }

    @Override
    protected void loadUnloadableData() {
        super.loadUnloadableData();

        when(this.jobsApiService.getEmpireJobs(any(), any())).thenReturn(Observable.just(new ArrayList<>()));
    }
}
