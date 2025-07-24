package de.uniks.stp24.model;

public record ErrorResponse(
        int statusCode,
        String error,
        String[] message) {
}
