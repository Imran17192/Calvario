package de.uniks.stp24.dto;

public record AggregateItemDto(
        String variable,
        double count,
        double subtotal
) {
}
