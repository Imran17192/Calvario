package de.uniks.stp24.service;

import de.uniks.stp24.App;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class BasicService {
    @Inject
    public App app;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    public ErrorService errorService;
}
