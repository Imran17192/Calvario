package de.uniks.stp24;

import de.uniks.stp24.service.PrefService;
import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.utils.ResponseConstants;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.Mockito.spy;

/**
 * Base class for controller tests
 */
public class ControllerTest extends ApplicationTest {

    @Spy
    public App app = spy(App.class);
    @Spy
    public PrefService prefService;
    @Spy
    public final IslandsService islandsService = Mockito.spy(IslandsService.class);
    @Spy
    public ResponseConstants responseConstants;
    @Spy
    public ResourceBundle resources = ResourceBundle.getBundle("de/uniks/stp24/lang/main", Locale.ENGLISH);
    @Spy
    public final ResourceBundle gameResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/game", Locale.ENGLISH);
    @Spy
    public final ResourceBundle variablesResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/variables", Locale.ENGLISH);
    @Spy
    public final ResourceBundle technologiesResourceBundle = ResourceBundle.getBundle("de/uniks/stp24/lang/technologies", Locale.ENGLISH);

    protected Stage stage;
    protected TestComponent testComponent;

    @Spy
    public final ContactsService contactsService = Mockito.spy(ContactsService.class);

    @Override
    public void start(Stage stage) throws Exception {

        super.start(stage);
        this.stage = stage;

        if (prefService != null) {
            stage.setX(0);
            stage.setY(0);
            prefService.setLocale(Locale.ENGLISH);
            this.prefService.removeRefreshToken();
        }

        testComponent = (TestComponent) DaggerTestComponent.builder().mainApp(app).build();
        testComponent.prefService().setLocale(Locale.ENGLISH);
        app.setComponent(testComponent);

        app.start(stage);
        stage.requestFocus();
        stage.getScene().getStylesheets().clear();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        app.cleanUp();
        app = null;
        stage = null;
        testComponent = null;
        prefService = null;
        responseConstants = null;
        resources = null;
    }

    @AfterAll
    public static void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    @AfterAll
    public static void tearDown2() {
        System.gc();
    }
}
