package de.uniks.stp24.model;

import de.uniks.stp24.controllers.InGameController;

public record SiteProperties(
        InGameController inGameController,
        String siteName,
        String siteCapacity,
        Jobs.Job job
) {
}
