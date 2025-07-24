package de.uniks.stp24.model;

import java.util.ArrayList;
import java.util.function.Consumer;

public record Announcement(
        String message,
        String forwardIcon,
        ArrayList<Consumer<Jobs.Job>> forwardMethods,
        Jobs.Job job) {
}
