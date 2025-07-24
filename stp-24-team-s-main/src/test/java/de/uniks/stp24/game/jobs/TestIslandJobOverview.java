package de.uniks.stp24.game.jobs;

import de.uniks.stp24.component.game.jobs.IslandOverviewJobProgressComponent;
import de.uniks.stp24.component.game.jobs.IslandOverviewJobsComponent;
import de.uniks.stp24.model.Jobs;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TestIslandJobOverview extends JobsTestComponent {
    @InjectMocks
    IslandOverviewJobsComponent islandOverviewJobsComponent;
    final Provider<IslandOverviewJobProgressComponent> islandOverviewJobProgressComponentProvider = () -> {
        IslandOverviewJobProgressComponent comp = new IslandOverviewJobProgressComponent();
        comp.gameResourceBundle = this.gameResourceBundle;
        comp.islandAttributes = this.islandAttributeStorage;
        comp.subscriber = this.subscriber;
        comp.jobsService = this.jobsService;
        comp.imageCache = this.imageCache;
        comp.app = this.app;
        return comp;
    };

    ListView<Jobs.Job> jobListView;
    ObservableList<Jobs.Job> jobs;

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);

        doReturn(null).when(this.imageCache).get(any());
        doReturn(ISLAND_1).when(this.islandAttributeStorage).getIsland();

        this.islandOverviewJobsComponent.setPrefHeight(600);

        this.islandOverviewJobsComponent.jobsService = this.jobsService;
        this.islandOverviewJobsComponent.islandAttributes = this.islandAttributeStorage;
        this.islandOverviewJobsComponent.progressPaneProvider = this.islandOverviewJobProgressComponentProvider;

        this.app.show(this.islandOverviewJobsComponent);
        this.islandOverviewJobsComponent.getStylesheets().clear();
    }

    @BeforeEach
    public void loadIslandJobs() {
        this.islandOverviewJobsComponent.setJobsObservableList(this.jobsService.getObservableListForSystem(this.SYSTEM_ID_1));
        this.jobListView = lookup("#jobProgressListView").queryListView();
        this.jobListView.setPrefHeight(500);
        this.jobs = jobListView.getItems();
    }

    @Test
    public void testLoadingIslandJobs() {
        WaitForAsyncUtils.waitForFxEvents();

        // Assert that only the jobs of this system were loaded
        assertEquals(4, this.jobs.size());
    }

    @Test
    public void testIslandNameInsertion() {
        WaitForAsyncUtils.waitForFxEvents();
        doReturn(this.ISLAND_1).when(this.islandAttributeStorage).getIsland();

        this.islandOverviewJobsComponent.insertIslandName();
        assertTrue(lookup("#noJobText").queryText().getText().contains(this.SYSTEM_NAME_1));
    }

    @Test
    public void testDeletingJob() {
        WaitForAsyncUtils.waitForFxEvents();
        doReturn(Observable.empty()).when(this.jobsApiService).deleteJob(eq(this.GAME_ID), eq(this.EMPIRE_ID), anyString());

        String jobID = this.jobs.getFirst()._id();

        this.callSubjectEvent(EVENT.DELETED, jobID);
        clickOn("#jobProgressDeleteButton_" + jobID);
    }

    @Test
    public void testOpeningInspectionWindow() {
        WaitForAsyncUtils.waitForFxEvents();

        String jobID = this.jobs.getFirst()._id();
        clickOn("#jobProgressInspectionButton_" + jobID);
        assertEquals(1, this.inspectorCalls.get("building"));

        jobID = this.jobs.get(2)._id();
        clickOn("#jobProgressInspectionButton_" + jobID);
        assertEquals(1, this.inspectorCalls.get("site"));
    }

    @Test
    public void testNoJobTestAppearance() {
        WaitForAsyncUtils.waitForFxEvents();
        doReturn(this.ISLAND_1).when(this.islandAttributeStorage).getIsland();

        for (Jobs.Job job : this.jobs) {
            this.deleteInternally(job._id());
        }
        WaitForAsyncUtils.waitForFxEvents();

//        assertTrue(lookup("#noJobText").queryText().isVisible());

        this.createInternally();
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(lookup("#noJobText").queryText().isVisible());
    }
}
