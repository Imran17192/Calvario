package de.uniks.stp24.model;


public record SeasonComponent(
        String transActionTypeText,
        String resourceType,
        double resourceAmount,
        double moneyAmount,
        boolean isPlaying
) {
}
