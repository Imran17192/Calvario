package de.uniks.stp24.dto;

import de.uniks.stp24.model.Effect;

import java.util.List;
import java.util.Map;

public record UpdateEmpireContactDto(
        Map<String, Integer> resources,
        Map<String, List<Effect>> effects,
        Map<String, Object> _private,
        Map<String, Object> _public
) {

}
