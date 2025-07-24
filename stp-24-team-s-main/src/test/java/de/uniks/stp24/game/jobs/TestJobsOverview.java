package de.uniks.stp24.game.jobs;

import de.uniks.stp24.component.game.jobs.JobElementComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.model.Jobs.Job;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import javax.inject.Provider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestJobsOverview extends JobsTestComponent {
    @InjectMocks
    JobsOverviewComponent jobsOverviewComponent;

    final Provider<JobElementComponent> jobElementComponentProvider = () -> {
        JobElementComponent comp = new JobElementComponent();
        comp.islandsService = islandsService;
        comp.imageCache = imageCache;
        comp.jobsService = jobsService;
        comp.subscriber = subscriber;
        comp.gameResourceBundle = gameResourceBundle;
        return comp;
    };

    ListView<Job> jobListView;
    ObservableList<Job> jobs;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        doReturn(null).when(this.imageCache).get(any());
        doReturn(ISLAND_1).when(this.islandsService).getIsland(any());

        this.jobsOverviewComponent.jobProvider = this.jobElementComponentProvider;
        this.jobsOverviewComponent.jobsService = this.jobsService;

        this.app.show(this.jobsOverviewComponent);
        this.jobsOverviewComponent.getStylesheets().clear();
    }

    @BeforeEach
    public void getListView() {
        WaitForAsyncUtils.waitForFxEvents();
        this.jobListView = lookup("#jobsListView").queryListView();
        this.jobs = jobListView.getItems();
    }

    @Test
    public void testInitializingOverview() {
        // Assert that only the running island jobs are shown
        assertEquals(7, this.jobs.size());
    }

    @Test
    public void testDeletingJobs() {
        when(this.jobsApiService.deleteJob(eq(this.GAME_ID), eq(this.EMPIRE_ID), any()))
                .thenReturn(Observable.just(this.jobsList.get(0)));

        // Test deleting a job from an island that has more jobs in the queue
        String jobID = this.jobs.get(0)._id();
        assertEquals(7, this.jobs.size());
        clickOn("#jobElementDeleteButton_" + jobID);
        this.deleteInternally(jobID);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(6, this.jobs.size());

        // Test deleting a job from an island with a single job
        jobID = this.jobs.get(2)._id();

        clickOn("#jobElementDeleteButton_" + jobID);
        this.deleteInternally(jobID);
        WaitForAsyncUtils.waitForFxEvents();

        assertEquals(5, this.jobs.size());
    }

    @Test
    public void testUpdatingJobs() {
        // Simulate game tick
        this.jobs.forEach(job -> this.updateInternally(job._id()));
        WaitForAsyncUtils.waitForFxEvents();

        this.jobs.forEach(job -> assertEquals(1, job.progress()));
    }

    @Test
    public void testClosingOverview() {
        clickOn("#closeButton");
        assertFalse(this.jobsOverviewComponent.isVisible());
    }

    @Test
    public void testOpeningInspectionWindow() {
        String jobID = this.jobs.get(0)._id();
        clickOn("#jobElementInspectionButton_" + jobID);

        assertEquals(1, this.inspectorCalls.get("overview"));
    }
}
