package de.uniks.stp24.dto;

public record EffectDto(
        String variable,
        double base,
        double multiplier,
        double bonus
) {
}
