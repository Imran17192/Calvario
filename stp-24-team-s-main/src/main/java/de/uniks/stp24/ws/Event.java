package de.uniks.stp24.ws;

public record Event<T>(
    String event,
    T data
) {
    public String suffix() {
        return event.substring(event.lastIndexOf('.') + 1);
    }
}
