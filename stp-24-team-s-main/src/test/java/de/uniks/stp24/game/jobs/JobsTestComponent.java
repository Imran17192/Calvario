package de.uniks.stp24.game.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.ControllerTest;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.IslandType;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.FleetCoordinationService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.ws.Event;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.Subject;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.fulib.fx.controller.Subscriber;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class JobsTestComponent extends ControllerTest {
    @Spy
    IslandAttributeStorage islandAttributeStorage;
    @Spy
    ImageCache imageCache;
    @Spy
    ObjectMapper objectMapper;
    @Spy
    TokenStorage tokenStorage;
    @Spy
    final
    EventListener eventListener = new EventListener(tokenStorage, objectMapper);
    @Spy
    Subscriber subscriber;
    @Spy
    JobsApiService jobsApiService;
    @Spy
    FleetCoordinationService fleetCoordinationService;

    @InjectMocks
    JobsService jobsService;

    protected enum EVENT {CREATED, UPDATED, DELETED}

    protected final Subject<Event<Job>> JOB_SUBJECT = BehaviorSubject.create();
    protected final Subject<Event<Game>> GAME_SUBJECT = BehaviorSubject.create();
    protected final String GAME_ID = "jobsGameID";
    protected final String EMPIRE_ID = "jobsEmpireID";
    protected final String SYSTEM_ID_1 = "jobsSystemID_1";
    protected final String SYSTEM_ID_2 = "jobsSystemID_2";
    protected final String SYSTEM_ID_3 = "jobsSystemID_3";
    protected final String SYSTEM_ID_4 = "jobsSystemID_4";
    protected final String SYSTEM_NAME_1 = "TestIslandUno";
    protected final String SYSTEM_NAME_2 = "TestIslandDos";
    protected final String SYSTEM_NAME_3 = "TestIslandTres";
    protected final String SYSTEM_NAME_4 = "TestIslandCuatro";
    protected final List<Job> jobsList = new ArrayList<>();

    protected final Island ISLAND_1 = new Island(this.EMPIRE_ID, 0, 0, 0,
            IslandType.agriculture, 3, 12, 0,
            Map.of("energy", 3), null,
            null, this.SYSTEM_ID_1, "unexplored", this.SYSTEM_NAME_1, 100);
    protected final Island ISLAND_UNEXP = new Island(this.EMPIRE_ID, 0, 0, 0,
            IslandType.agriculture, 3, 12, 0,
            Map.of("energy", 3), null,
            null, "ISLAND_UNEXP", "unexplored", this.SYSTEM_NAME_1, 100);
    protected final Island ISLAND_EXP = new Island(this.EMPIRE_ID, 0, 0, 0,
            IslandType.agriculture, 3, 24, 0,
            Map.of("energy", 3), null,
            null, "ISLAND_EXP", "explored", this.SYSTEM_NAME_1, 100);

    protected final Map<String, Integer> inspectorCalls = new HashMap<>();

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        this.jobsList.addAll(List.of(
                new Job("0", "0", "jobID_1", 0, 10,
                this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_1, 0, "building", "farm",
                null, null,"","",new LinkedList<>(), Map.of("energy", 100), null),
                new Job("0", "0", "jobID_2", 0, 10,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_1, 0, "building", "refinery",
                        null, null,"","",new LinkedList<>(), Map.of("energy", 30), null),
                new Job("0", "0", "jobID_3", 0, 10,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_1, 0, "district", null,
                        "energy", null,"","",new LinkedList<>(), Map.of("energy", 30), null),
                new Job("0", "0", "jobID_4", 0, 10,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_1, 0, "upgrade", null,
                        null, null,"","",new LinkedList<>(), Map.of("energy", 30), null),
                new Job("0", "0", "jobID_5", 0, 6,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_2, 0, "district", null,
                        "energy", null,"","",new LinkedList<>(), Map.of("energy", 200), null),
                new Job("0", "0", "jobID_6", 0, 6,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_3, 0, "district", null,
                        "ancient_foundry", null,"","",new LinkedList<>(), Map.of("energy", 200), null),
                new Job("0", "0", "jobID_7", 0, 6,
                        this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_4, 0, "upgrade", null,
                        null, null,"","",new LinkedList<>(), Map.of("energy", 200), null)));

        this.initMocks();
        this.mockJobInspectors();
    }

    private void initMocks() {
        doReturn(this.GAME_ID).when(this.tokenStorage).getGameId();
        doReturn(this.EMPIRE_ID).when(this.tokenStorage).getEmpireId();
        doReturn(this.SYSTEM_NAME_1).when(this.islandsService).getIslandName(this.SYSTEM_ID_1);
        doReturn(this.SYSTEM_NAME_1).when(this.islandsService).getIslandName(this.SYSTEM_ID_1);
        doReturn(this.SYSTEM_NAME_2).when(this.islandsService).getIslandName(this.SYSTEM_ID_2);
        doReturn(this.SYSTEM_NAME_3).when(this.islandsService).getIslandName(this.SYSTEM_ID_3);
        doReturn(this.SYSTEM_NAME_4).when(this.islandsService).getIslandName(this.SYSTEM_ID_4);

        doReturn(Observable.just(this.jobsList)).when(this.jobsApiService).getEmpireJobs(this.GAME_ID, this.EMPIRE_ID);

        when(this.eventListener.listen(String.format("games.%s.empires.%s.jobs.*.*", this.GAME_ID, this.EMPIRE_ID),
                Job.class)).thenReturn(JOB_SUBJECT);
        when(this.eventListener.listen(String.format("games.%s.ticked", this.GAME_ID), Game.class))
                .thenReturn(GAME_SUBJECT);

        this.jobsService.loadEmpireJobs();
        this.jobsService.initializeJobsListeners();
    }

    protected void callSubjectEvent(EVENT type, String jobID) {
        switch (type) {
            case CREATED -> this.JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.%s.created",
                    this.GAME_ID, this.EMPIRE_ID, jobID),
                    new Job("0", "0", jobID, 0, 10,
                            this.GAME_ID, this.EMPIRE_ID, this.SYSTEM_ID_4, 0, "building", "farm",
                            null, null,"","",new LinkedList<>(), Map.of("energy", 100), null)));

            case UPDATED -> this.JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.%s.updated",
                    this.GAME_ID, this.EMPIRE_ID, jobID),
                    this.jobsList.stream().filter(job -> job._id().equals(jobID)).toList().getFirst()));

            case DELETED -> this.JOB_SUBJECT.onNext(new Event<>(String.format("games.%s.empires.%s.jobs.%s.deleted",
                        this.GAME_ID, this.EMPIRE_ID, jobID),
                        this.jobsList.stream().filter(job -> job._id().equals(jobID)).toList().getFirst()));
        }
    }

    // This method allows for testing of the job inspectors without the need of loading InGameController
    // The real behavior of job inspectors must be tested through app test 3
    private void mockJobInspectors() {
        this.inspectorCalls.putAll(Map.of("overview", 0, "building", 0, "site", 0));

        this.jobsService.setJobInspector("island_jobs_overview", (id) ->
                this.inspectorCalls.put("overview", this.inspectorCalls.get("overview")+1));
        this.jobsService.setJobInspector("building_overview", (id) ->
                this.inspectorCalls.put("building", this.inspectorCalls.get("building")+1));
        this.jobsService.setJobInspector("site_overview", (id) ->
                this.inspectorCalls.put("site", this.inspectorCalls.get("site")+1));
    }

    // EventListener does not respond to Subject calls. Find the issue later
    // These methods mock the behavior of the event listener
    public void createInternally() {
        Platform.runLater(() -> this.jobsService.addJobToGroups(this.jobsList.getFirst()));
    }

    public void createInternally(Job job) {
        Platform.runLater(() -> this.jobsService.addJobToGroups(job));
        this.jobsList.add(job);
    }

    public void updateInternally(String jobID) {
        Job job = this.jobsList.stream().filter(job2 -> job2._id().equals(jobID)).toList().getFirst();
        Job updatedJob = new Job(job.createdAt(), "1", job._id(), job.progress()+1, job.total(), job.game(),
                job.empire(), job.system(), job.priority(), job.type(), job.building(), job.district(), job.technology(),
                job.fleet(), job.ship(), job.path(), job.cost(), job.result());
        Platform.runLater(() -> this.jobsService.updateJobInGroups(updatedJob));
    }

    public void deleteInternally(String jobID) {
        Job job = this.jobsList.stream().filter(job2 -> job2._id().equals(jobID)).toList().getFirst();
        Platform.runLater(() -> this.jobsService.deleteJobFromGroups(job));
    }
}
