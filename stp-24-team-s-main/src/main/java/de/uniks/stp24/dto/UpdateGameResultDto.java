package de.uniks.stp24.dto;

import de.uniks.stp24.model.GameSettings;

public record UpdateGameResultDto(
    String createdAt,
    String updatedAt,
    String _id,
    String name,
    String owner,
    boolean started,
    int speed,
    int period,
    GameSettings size
) {
}
