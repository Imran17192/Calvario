package de.uniks.stp24.model;

import java.util.Map;

public record LoginResult(
        String _id,
        String name,
        String avatar,
        Map<String, Integer> _public,
        String accessToken,
        String refreshToken

) {
}
