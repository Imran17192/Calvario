package de.uniks.stp24.dto;

import de.uniks.stp24.model.Effect;

import java.util.List;
import java.util.Map;

public record UpdateEmpireDto(
        Map<String, Integer> resources,
        String[] technologies,
        Map<String, List<Effect>> effects,
        Map<String, Object> _private,
        Map<String, Object> _public

) {
}
