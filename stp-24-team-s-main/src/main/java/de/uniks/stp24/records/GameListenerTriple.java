package de.uniks.stp24.records;

import java.beans.PropertyChangeListener;

public record GameListenerTriple(de.uniks.stp24.model.GameStatus game,
                                 PropertyChangeListener listener,
                                 String propertyName) {
}
