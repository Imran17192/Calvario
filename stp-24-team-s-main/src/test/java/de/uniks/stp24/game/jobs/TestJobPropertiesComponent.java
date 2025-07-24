package de.uniks.stp24.game.jobs;

import de.uniks.stp24.component.game.jobs.PropertiesJobProgressComponent;
import javafx.application.Platform;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TestJobPropertiesComponent extends JobsTestComponent {
    @InjectMocks
    PropertiesJobProgressComponent jobProgressComponent;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        this.jobProgressComponent.jobsService = this.jobsService;
        doReturn(null).when(this.imageCache).get(any());

        this.app.show(this.jobProgressComponent);
    }

    @BeforeEach
    public void setJob() {
        this.jobProgressComponent.setJobProgress(this.jobsList.get(0));
    }

    @Test
    public void testSetJobProgress() {
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("0/10", lookup("#jobProgressText").queryText().getText());

        Platform.runLater(() -> this.jobProgressComponent.setJobProgress(this.jobsList.get(4)));
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals("0/6", lookup("#jobProgressText").queryText().getText());

    }

    @Test
    public void testIncrementProgress() {
        WaitForAsyncUtils.waitForFxEvents();
        this.jobProgressComponent.setShouldTick(true);
        this.jobProgressComponent.incrementProgress();
        assertEquals("1/10", lookup("#jobProgressText").queryText().getText());
        assertEquals(0.1, ((ProgressBar) lookup("#jobProgressBar").query()).getProgress());
    }
}
