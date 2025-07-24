package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Jobs.JobDTO;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Singleton
public class JobsService {
    @Inject
    public JobsApiService jobsApiService;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public Subscriber subscriber;
    @Inject
    public EventListener eventListener;

    public final Map<String, ObservableList<Job>> jobCollections = new HashMap<>();
    final Map<String, ArrayList<Runnable>> jobCompletionFunctions = new HashMap<>();
    final Map<String, ArrayList<Runnable>> jobDeletionFunctions = new HashMap<>();
    final Map<String, Consumer<Job>> jobInspectionFunctions = new HashMap<>();
    final Map<String, ArrayList<Consumer<Job>>> loadTypeFunctions = new HashMap<>();
    final ArrayList<Runnable> loadCommonFunctions = new ArrayList<>();
    final ArrayList<Runnable> finishCommonFunctions = new ArrayList<>();
    final ArrayList<Runnable> startCommonFunctions = new ArrayList<>();
    final ArrayList<Runnable> jobCommonUpdates = new ArrayList<>();
    final ArrayList<Consumer<Job>> startCommonConsumers = new ArrayList<>();
    final Map<String, ArrayList<Consumer<Job>>> jobCompletionConsumers = new HashMap<>();
    final ArrayList<Runnable> tickedCommonFunctions = new ArrayList<>();

    private int period = -1;

    @Inject
    public JobsService() {}

    /**
     * Loads jobCollections started by the player's empire upon entering the game. <p>
     * Call this method inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * within the {@link de.uniks.stp24.service.InGameService InGameService} controller before the
     * {@link #initializeJobsListeners() initializeJobsListener} method.
     */
    public void loadEmpireJobs() {
        this.jobCollections.put("building", FXCollections.observableArrayList());
        this.jobCollections.put("district", FXCollections.observableArrayList());
        this.jobCollections.put("upgrade", FXCollections.observableArrayList());
        this.jobCollections.put("technology", FXCollections.observableArrayList());
        this.jobCollections.put("collection", FXCollections.observableArrayList());
        this.jobCollections.put("ship", FXCollections.observableArrayList());
        this.jobCollections.put("travel", FXCollections.observableArrayList());
        if(this.tokenStorage.isSpectator()) return;
        this.subscriber.subscribe(this.jobsApiService.getEmpireJobs(
                        this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), jobList -> {
                    jobList.forEach(this::addJobToGroups);

                    this.loadCommonFunctions.forEach(Runnable::run);
                    this.jobCollections.get("collection").forEach(job -> {
                        if (this.loadTypeFunctions.containsKey(job.type()))
                            this.loadTypeFunctions.get(job.type()).forEach(func -> func.accept(job));
                    });
                }, error -> System.out.println("JobsService: Failed to load job collections \n" + error.getMessage())
        );
    }

    /**
     * Creates a listener on job updates. <p>
     * Call this method inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * within the {@link de.uniks.stp24.service.InGameService InGameService} controller after the
     * {@link #loadEmpireJobs() loadEmpireJobs} method.
     */
    public void initializeJobsListeners() {
        if(this.tokenStorage.isSpectator()) return;
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.empires.%s.jobs.*.*",
                this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId()), Job.class), result -> {
            Job job = result.data();

            switch (result.suffix()) {
                case "created" -> this.addJobToGroups(job);
                case "updated" -> this.updateJobInGroups(job);
            }
            this.jobCommonUpdates.forEach(Runnable::run);

            }, Throwable::printStackTrace);

        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.ticked",
                this.tokenStorage.getGameId()), Game.class), game -> {
            if (game.data().period() != this.period) {
                this.tickedCommonFunctions.forEach(Runnable::run);
                this.period = game.data().period();
            }

        }, error -> System.out.println("Error listening to game ticks in the JobsService\n" + error.getMessage()));
    }

    public void addJobToGroups(@NotNull Job job) {
        if (job.progress() == job.total()) return;

        if (!job.type().equals("technology")) {
            this.jobCollections.get(job.type()).add(job);
        }

        if (!this.jobCollections.containsKey(job.system()))
            this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
        else this.jobCollections.get(job.system()).add(job);

        if (!(job.type().equals("travel") || job.type().equals("ship") || job.type().equals("technology")))
            this.jobCollections.get("collection").add(job);

        this.startCommonFunctions.forEach(Runnable::run);
        this.startCommonConsumers.forEach(func -> func.accept(job));
    }

    public void updateJobInGroups(@NotNull Job job) {
        this.jobCollections.get(job.type()).replaceAll(other -> other.equals(job) ? job : other);
        this.jobCollections.get("collection").replaceAll(other -> other.equals(job) ? job : other);

        if (!job.type().equals("technology")) {
            if (Objects.nonNull(job.system())) {
                if (!this.jobCollections.containsKey(job.system()))
                    this.jobCollections.put(job.system(), FXCollections.observableArrayList(job));
                else this.jobCollections.get(job.system()).replaceAll(other -> other.equals(job) ? job : other);
            }
        }

        if (job.progress() == job.total()) this.deleteJobFromGroups(job);
    }

    public void deleteJobFromGroups(@NotNull Job job) {
        this.jobCollections.get(job.type()).removeIf(other -> other._id().equals(job._id()));
        this.jobCollections.get("collection").removeIf(other -> other._id().equals(job._id()));

        if (!job.type().equals("technology")) {
            if (Objects.nonNull(job.system())) {
                this.jobCollections.get(job.system()).removeIf(other -> other._id().equals(job._id()));

                ObservableList<Job> systemJobs = this.jobCollections.get(job.system());
                if (!systemJobs.isEmpty() && !this.jobCollections.get("collection").contains(systemJobs.getFirst()))
                    this.jobCollections.get("collection").add(systemJobs.getFirst());
            }
        }

        if (this.jobCompletionFunctions.containsKey(job._id()))
            this.jobCompletionFunctions.get(job._id()).forEach(Runnable::run);

        this.finishCommonFunctions.forEach(Runnable::run);

        if (this.jobCompletionConsumers.containsKey(job._id()))
            this.jobCompletionConsumers.get(job._id()).forEach(func -> func.accept(job));
    }

    private void deleteJobFromGroups(String jobID) {
        this.jobCollections.forEach((key, list) -> list.removeIf(job -> job._id().equals(jobID)));
        this.jobCompletionFunctions.remove(jobID);
        this.jobCompletionConsumers.remove(jobID);
    }

    /**
     * A method to define further common functions that should be executed when a job of any type
     * is started. It is possible to add more than one function on the job start.
     * @param func {@link Runnable runnable } lambda function that will be executed after any job starts
     */
    public void onJobCommonStart(Runnable func) {
        this.startCommonFunctions.add(func);
    }

    public void onJobCommonStart(Consumer<Job> func) {
        this.startCommonConsumers.add(func);
    }

    public void onJobCommonUpdates(Runnable func) {
        this.jobCommonUpdates.add(func);
    }

    public void onGameTicked(Runnable func) {
        this.tickedCommonFunctions.add(func);
    }

    /**
     * A method used to define the further execution result of a job. It's useful if you need to execute
     * some methods that lay within other classes.
     * It is possible to add more than one function on the job completion.
     * @param jobID ID of the job after completion of which the function will be executed
     * @param func the execution function
     */
    public void onJobDeletion(String jobID, Runnable func) {
        if (!this.jobDeletionFunctions.containsKey(jobID))
            this.jobDeletionFunctions.put(jobID, new ArrayList<>());
        this.jobDeletionFunctions.get(jobID).add(func);
    }

    /**
     * A method used to define the {@link Runnable Runnable} lambda functions that should be called after the
     * job is completed. It's useful if you need to execute some methods that lay within other classes.
     * The execution function will be deleted after the job is completed.
     * It is possible to add more than one function on the job completion.
     * @param jobID ID of the job after completion of which the function will be executed
     * @param func the execution function
     */
    public void onJobCompletion(String jobID, Runnable func) {
        if (!this.jobCompletionFunctions.containsKey(jobID))
            this.jobCompletionFunctions.put(jobID, new ArrayList<>());
        this.jobCompletionFunctions.get(jobID).add(func);
    }

    public void onJobCompletion(String jobID, Consumer<Job> func) {
        if (!this.jobCompletionConsumers.containsKey(jobID))
            this.jobCompletionConsumers.put(jobID, new ArrayList<>());
        this.jobCompletionConsumers.get(jobID).add(func);
    }


    /**
     * A method that is used to define functions that should be executed when any on
     */
    public void onJobCommonFinish(Runnable func) {
        this.finishCommonFunctions.add(func);
    }

    /**
     * Provides a {@link Consumer<String> Consumer}<{@link String}> lambda functions that should only be executed after
     * the initial job loading is completed. <p>
     * Provide this method with {@link #getJobObservableListOfType(String) getJobObservableListOfType},
     * {@link #getObservableListForSystem(String) getObservableListForSystem} or  inside a method annotated with {@link org.fulib.fx.annotation.event.OnInit @OnInit}
     * inside your {@link org.fulib.fx.annotation.controller.Controller Controller} to receive an {@link ObservableList}
     *  with loaded jobs. <p>
     *  Name the parameter inside the consumer function as a <i>jobID</i>: {@code (jobID) -> yourFunction(jobID)}.
     * @param func the function that has to be executed after the job loading process is finished
     */
    public void onJobsLoadingFinished(String jobType, Consumer<Job> func) {
        if (!this.loadTypeFunctions.containsKey(jobType))
            this.loadTypeFunctions.put(jobType, new ArrayList<>());
        this.loadTypeFunctions.get(jobType).add(func);
    }

    /**
     * Provides {@link Runnable Runnable} lambda functions that should only be executed after
     * the initial job loading is completed.
     * @param func the function that has to be executed after the job loading process is finished
     */
    public void onJobsLoadingFinished(Runnable func) {
        this.loadCommonFunctions.add(func);
    }

    /**
     * Begins a new job from given {@link JobDTO JobDTO}. Use the static methods of the
     * {@link de.uniks.stp24.model.Jobs Jobs} class to create a new JobDTO of a specific type.
     * @param jobDTO DTO containing a request type
     * @return {@link Job Job} class containing the {@link de.uniks.stp24.model.Jobs JobResult} of starting the job
     */
    public Observable<Job> beginJob(JobDTO jobDTO) {
        return this.jobsApiService.createNewJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobDTO);
    }

    /**
     * Stops the job of the given id. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param jobID ID of the job that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(String jobID) {
        this.deleteJobFromGroups(jobID);
        if (this.jobDeletionFunctions.containsKey(jobID))
            this.jobDeletionFunctions.get(jobID).forEach(Runnable::run);

        return this.jobsApiService.deleteJob(this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), jobID);
    }

    /**
     * Stops the given job. If the job is not completed, the resources for its initializing will be returned
     * to the empire.
     * @param job Job as a class that needs to be stopped
     * @return {@link Job Job} class containing the result of stopping the job.
     */
    public Observable<Job> stopJob(Job job) {
        return this.stopJob(job._id());
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> of a specific job type
     * that will be dynamically updated upon starting, editing or deleting jobCollections. <p>
     * To receive a list prefilled with jobs right after the loading of the
     * {@link org.fulib.fx.annotation.controller.Controller Controller} is done, provide this function within the
     * {@link #onJobsLoadingFinished(Runnable) onJobsLoadingFinished}.
     * @param jobType type of the jobs
     * @return {@link ObservableList ObservableList}<{@link Job Job}> filtered for a specific job type
     */
    public ObservableList<Job> getJobObservableListOfType(String jobType) {
        return this.jobCollections.get(jobType);
    }

    /**
     * Returns an {@link ObservableList ObservableList}<{@link Job Job}> for a specific island
     * that will be dynamically updated upon starting, editing or deleting jobCollections. <p>
     * To receive a list prefilled with jobs right after the loading of the
     * {@link org.fulib.fx.annotation.controller.Controller Controller} is done, provide this function within the
     * {@link #onJobsLoadingFinished(Runnable) onJobsLoadingFinished}.
     * @param systemID ID of the island
     * @return {@link ObservableList ObservableList}<{@link Job Job}> with jobs of type <i>building</i>,
     * <i>district</i> and <i>upgrade</i> filtered for a specific island
     */
    public ObservableList<Job> getObservableListForSystem(String systemID) {
        if (!this.jobCollections.containsKey(systemID))
            this.jobCollections.put(systemID, FXCollections.observableArrayList());
        return this.jobCollections.get(systemID);
    }


    public void setJobInspector(String inspectorID, Consumer<Job> func) {
        this.jobInspectionFunctions.put(inspectorID, func);
    }

    public Consumer<Job> getJobInspector(String inspectorID) {
        if (this.jobInspectionFunctions.containsKey(inspectorID))
            return this.jobInspectionFunctions.get(inspectorID);
        else System.out.printf("Job Service: the inspection function is not found for a given inspector ID: %s!\n", inspectorID);
        return null;
    }

    public boolean isCurrentIslandJob(Job job) {
        if (this.getObservableListForSystem(job.system()).isEmpty()) return true;
        return job.equals(this.getObservableListForSystem(job.system()).getFirst());
    }

    public int getStructureJobsInQueueCount(String systemID) {
        if (!this.jobCollections.containsKey(systemID))
            return 0;

        int count = 0;
        for (Job job : jobCollections.get(systemID)) {
            if (job.type().equals("building") || job.type().equals("district")) {
                count++;
            }
        }
        return count;
    }

    /**
     * A clearing method that should be called inside the {@link InGameController#destroy() InGameController.destroy()}
     * to properly dispose of the jobs and functions that were loaded in the service during the game.
     */
    public void dispose() {
        this.jobCollections.clear();
        this.jobCompletionFunctions.clear();
        this.jobDeletionFunctions.clear();
        this.loadTypeFunctions.clear();
        this.loadCommonFunctions.clear();
        this.finishCommonFunctions.clear();
        this.tickedCommonFunctions.clear();
        this.jobCompletionConsumers.clear();
        this.subscriber.dispose();
    }
}
