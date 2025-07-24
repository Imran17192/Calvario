package de.uniks.stp24.dto;

import java.util.Map;

public record CreateUserDto(
        String name,
        String avatar,
        String password,
        Map<String,Integer> _public
) {
}
