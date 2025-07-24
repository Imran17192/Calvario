package de.uniks.stp24.dto;

public record EffectSourceDto(
        String id,
        String eventType,
        int duration,
        EffectDto[] effects
) {
}
