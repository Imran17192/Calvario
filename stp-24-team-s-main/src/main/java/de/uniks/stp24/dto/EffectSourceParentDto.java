package de.uniks.stp24.dto;

public record EffectSourceParentDto(EffectSourceDto[] effects) {
    public EffectSourceParentDto {
        if (effects == null) {
            effects = new EffectSourceDto[0];
        }
    }
}
