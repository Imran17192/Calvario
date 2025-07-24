package de.uniks.stp24.service;

import de.uniks.stp24.Main;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
import java.util.prefs.Preferences;

@Singleton
public class PrefService {

    private final Preferences prefs = Preferences.userNodeForPackage(Main.class);
    @Inject
    public PrefService() {

    }

    public void removeRefreshToken() {
        prefs.remove("refreshToken");
    }
    public String getRefreshToken() {
        return prefs.get("refreshToken",null);
    }

    public void setRefreshToken(String refreshToken) {
        prefs.put("refreshToken",refreshToken);

    }

    public Locale getLocale() {
        Locale.setDefault(Locale.ENGLISH);
        return Locale.forLanguageTag(prefs.get("language", Locale.getDefault().toLanguageTag()));
    }

    public void setLocale(Locale locale) {
        prefs.put("language",locale.getLanguage());
    }
}
