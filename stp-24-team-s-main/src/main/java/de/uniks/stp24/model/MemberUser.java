package de.uniks.stp24.model;

// This model contains data for the player list in the lobby
public record MemberUser(
        User user,
        Empire empire,
        boolean ready,
        Game game,
        boolean asHost
) {
}
