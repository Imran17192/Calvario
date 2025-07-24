package de.uniks.stp24.dto;

import de.uniks.stp24.model.Empire;

public record UpdateMemberDto(
        boolean ready,
        Empire empire
) { }
