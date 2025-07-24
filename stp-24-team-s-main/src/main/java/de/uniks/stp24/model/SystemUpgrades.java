package de.uniks.stp24.model;

public record SystemUpgrades(
        UpgradeStatus unexplored,
        UpgradeStatus explored,
        UpgradeStatus colonized,
        UpgradeStatus upgraded,
        UpgradeStatus developed
) {
}
