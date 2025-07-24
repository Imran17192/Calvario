package de.uniks.stp24;

import de.uniks.stp24.service.ImageCache;
import org.junit.jupiter.api.Test;
import org.mockito.Spy;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;

public class ImageCacheTest extends ControllerTest {

    @Inject
    ImageCache imageCache;

    @Spy
    public final App app = new App();

    @Test
    public void basicTest() {
        assertNull(imageCache);
        imageCache = new ImageCache();
        imageCache.get("test/911.png");

        //fail
        imageCache.get("test/910.png");
        imageCache.get("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==");
    }

}
