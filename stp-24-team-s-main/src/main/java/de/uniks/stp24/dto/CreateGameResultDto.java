package de.uniks.stp24.dto;

import de.uniks.stp24.model.GameSettings;

public record CreateGameResultDto(
        String createdAt,
        String updatedAt,
        String _id,
        String name,
        String owner,
        boolean started,
        int speed,
        int period,
        GameSettings settings
) {
}
