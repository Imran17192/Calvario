package de.uniks.stp24.model;

import java.util.Map;

public record User(
        String name,
        String _id,
        String avatar,
        String createdAt,
        String updatedAt,
        Map<String,Integer> _public
) {
}

