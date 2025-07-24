package de.uniks.stp24.dto;

public record UpdateUserDto(
    String name,
    String avatar,
    String password) {
}
