package de.uniks.stp24.service.menu;

import de.uniks.stp24.service.PrefService;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Locale;
import java.util.ResourceBundle;


public class LanguageService {
    @Inject
    public PrefService prefService;
    @Inject
    public Provider<ResourceBundle> newResources;

    @Inject
    public LanguageService() {
    }

    public ResourceBundle setLocale(Locale locale) {
        prefService.setLocale(locale);
        return newResources.get();
    }

}
