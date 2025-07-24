package de.uniks.stp24.dto;

import de.uniks.stp24.model.GameSettings;

public record UpdateGameDto(
        String name,
        int maxMembers,
        boolean started,
        int speed,
        GameSettings size,
        String password
) {

}
