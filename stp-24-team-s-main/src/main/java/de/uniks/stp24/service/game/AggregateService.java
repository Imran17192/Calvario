package de.uniks.stp24.service.game;

import de.uniks.stp24.rest.GameLogicApiService;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AggregateService {
    @Inject
    Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;
}
