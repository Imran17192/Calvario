package de.uniks.stp24.model;

import javax.inject.Inject;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class GameStatus {
    protected PropertyChangeSupport listeners;
    public static final String PROPERTY_PAUSED = "paused";
    public static final String PROPERTY_SETTINGS = "settings";
    public static final String PROPERTY_LANGUAGE = "language";


    private Boolean paused = false;
    private Boolean showSettings = false;
    private int language = 0;


    @Inject
    public GameStatus() {

    }

    public Boolean getPaused() {
        return this.paused;
    }

    public GameStatus setPaused(Boolean value) {
        if (Objects.equals(value, this.paused)) {
            return this;
        }

        final Boolean oldValue = this.paused;
        this.paused = value;
        this.firePropertyChange(PROPERTY_PAUSED, oldValue, value);
        return this;
    }

    public Boolean getShowSettings() {
        return this.showSettings;
    }

    public GameStatus setShowSettings(Boolean value) {
        if (Objects.equals(value, this.showSettings)) {
            return this;
        }

        final Boolean oldValue = this.showSettings;
        this.showSettings = value;
        this.firePropertyChange(PROPERTY_SETTINGS, oldValue, value);
        return this;
    }

    public GameStatus setLanguage(int value) {
        if (Objects.equals(value, this.language)) {
            return this;
        }

        final int oldValue = this.language;
        this.language = value;
        this.firePropertyChange(PROPERTY_LANGUAGE, oldValue, value);
        return this;
    }

    public int getLanguage() {
        return this.language;
    }

    public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (this.listeners != null) {
            this.listeners.firePropertyChange(propertyName, oldValue, newValue);
            return true;
        }
        return false;
    }

    public PropertyChangeSupport listeners() {
        if (this.listeners == null) {
            this.listeners = new PropertyChangeSupport(this);
        }
        return this.listeners;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder();
        result.append(' ').append(this.getPaused());
        return result.substring(1);
    }

    public void removeYou() {
        this.setPaused(null);
    }
}