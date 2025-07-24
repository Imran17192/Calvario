package de.uniks.stp24.model;

public record Effect(
        String variable,
        double base,
        double multiplier,
        double bonus
) {
}
