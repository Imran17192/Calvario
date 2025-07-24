package de.uniks.stp24.service;

import de.uniks.stp24.Main;
import javafx.scene.image.Image;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class ImageCache {
    private final Map<String, Image> images = new HashMap<>();

    @Inject
    public ImageCache() {
    }

    public Image get(String path) {
        return get(path,true);
    }
    public Image get(String path, boolean background) {
        return images.computeIfAbsent(path, p -> load(p, background));
    }

    private Image load(String path, boolean background) {
        if (!path.startsWith("http://") &&
                !path.startsWith("https://") &&
                !path.startsWith("file://") &&
                !path.startsWith("data:")) {
            // maybe must be modified to path + directory
            final URL url = Main.class.getResource(path);
            if (url != null) {
                path = url.toExternalForm();
            } else {
                System.err.println("Failed to load image: " + path);
                return new Image("https://fakeimg.pl/50x50?text=Image+not+found",true);
            }
        }
        final String finalPath = path;
        final Image image = new Image(finalPath, background);
        image.errorProperty().addListener((observable, oldValue, newValue ) -> {
                if (newValue) {
                    System.err.println("Failed to load image: " + finalPath);
                    images.remove(finalPath);
                }
        });
        return image;
    }
}


