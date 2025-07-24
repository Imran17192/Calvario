package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.JobsApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import de.uniks.stp24.service.game.TimerService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.*;

@Component(view = "ResearchJob.fxml")
public class ResearchJobComponent extends AnchorPane {

    @FXML
    ImageView researchCostImage;
    @FXML
    ImageView technologyTagImage2;
    @FXML
    ImageView technologyTagImage1;
    @FXML
    ListView<Effect> technologyEffectsListView;
    @FXML
    Text technologyNameText;
    @FXML
    Text researchCostText;
    @FXML
    Button cancelResearchButton;
    @FXML
    Text researchProgressText;
    @FXML
    ProgressBar researchProgressBar;
    @FXML
    AnchorPane researchBackground;
    @Inject
    public JobsService jobsService;

    @Inject
    TechnologyService technologyService;

    @Inject
    JobsApiService jobsApiService;

    @Inject
    EventListener eventListener;

    @Inject
    TokenStorage tokenStorage;


    @Inject
    TimerService timerService;
    @Inject
    Subscriber subscriber;
    private Jobs.Job job;

    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    @Resource
    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    public ObservableList<Jobs.Job> jobList = FXCollections.observableArrayList();
    private TechnologyCategoryComponent technologyCategoryComponent;

    public final ObservableList<TechnologyExtended> technologies = FXCollections.observableArrayList();

    ObservableList<Effect> description = FXCollections.observableArrayList();
    Provider<TechnologyCategoryDescriptionSubComponent> provider = () -> new TechnologyCategoryDescriptionSubComponent(variablesResourceBundle);
    private boolean isTechnologiesListInitialized = false;

    @Inject
    App app;

    @Inject
    public ResearchJobComponent() {

    }

    @OnRender
    public void render() {
        cancelResearchButton.getStyleClass().clear();
        Image image = new Image("/de/uniks/stp24/assets/buttons/cancel_button.png");
        Image image1 = new Image("de/uniks/stp24/icons/resources/research.png");
        researchCostImage.setImage(image1);
        researchCostImage.setFitHeight(20);
        researchCostImage.setFitWidth(20);
        ImageView imageView = new ImageView(image);
        cancelResearchButton.setGraphic(imageView);
        setProgressBar();
    }

    @OnInit
    public void init() {

    }

    @OnInit
    public void initializeJobList() {
        this.jobsService.onJobsLoadingFinished(() ->
                this.jobList = jobsService.getJobObservableListOfType("technology"));
    }

    private void initializeTechnologiesList() {
        if (!isTechnologiesListInitialized) {
            for (Jobs.Job jobAlreadyRunning : jobList) {
                subscriber.subscribe(technologyService.getTechnology(jobAlreadyRunning.technology()), result -> {
                    if (!technologies.contains(result)) {
                        technologies.add(result);
                    }
                    technologyCategoryComponent.showWindowOnStart();
                }, error -> System.out.println("Error trying to get a Technology in ResearchComponent"));
            }
            isTechnologiesListInitialized = true;
        }
    }

    public void handleJobsAlreadyRunning() {
        initializeJobList();
        initializeTechnologiesList();
    }

    public void handleJobInformation() {
        if (Objects.nonNull(technologyCategoryComponent.getTechnology())) {
            for (Jobs.Job job1 : jobList) {
                if (job1.technology().equals(technologyCategoryComponent.getTechnology().id())) {
                    subscriber.subscribe(jobsApiService.getJobByID(tokenStorage.getGameId(), tokenStorage.getEmpireId(), job1._id()), currentJob -> {
                        jobsService.onJobCompletion(currentJob._id(), this::handleJobFinished);
                        double currentJobTotal = currentJob.total();
                        int roundedUpTotal = (int) currentJobTotal;
                        researchProgressBar.setProgress((double) currentJob.progress() / roundedUpTotal);
                        researchProgressText.setText(currentJob.progress() + " / " + roundedUpTotal);
                        this.job = currentJob;
                    }, error -> System.out.println("Error trying to get a Job in ResearchComponent"));
                }
            }
        }
    }

    public void progressHandling() {
        ObservableList<Jobs.Job> newJobList = jobsService.getJobObservableListOfType("technology");

        Set<String> existingJobTechnologies = new HashSet<>();
        List<Jobs.Job> uniqueJobList = new ArrayList<>();

        for (Jobs.Job newJob : newJobList) {
            if (existingJobTechnologies.add(newJob.technology())) {
                uniqueJobList.add(newJob);
            }
        }
        jobList.clear();
        jobList.addAll(uniqueJobList);

        this.handleJobInformation();
    }

    public void setProgressBar() {
        this.jobsService.onGameTicked(this::progressHandling);
    }

    public void setEffectListView() {
        if (technologyCategoryComponent.getTechnology() != null) {
            technologyEffectsListView.getItems().clear();
            technologyEffectsListView.setItems(description);
            description.clear();
            description.addAll(technologyCategoryComponent.getTechnology().effects());
            technologyEffectsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
        }
    }

    private void handleJobFinished() {
        if(this.isVisible()) {
            jobList.remove(job);
            technologies.removeIf(technologyExtended -> technologyExtended.id().equals(job.technology()));
            technologyCategoryComponent.handleJobCompleted(job);
            setVisible(false);
        }
    }

    @OnInit
    public void setJobFinishers() {
        this.jobsService.onJobsLoadingFinished("technology", this.technologyCategoryComponent::handleJobCompleted);
    }


    public void handleJob(TechnologyExtended technology) {
        setJobDescription(technology);
        subscriber.subscribe(jobsService.beginJob(Jobs.createTechnologyJob(technology.id())), job1 -> {
            this.job = job1;
            this.jobList.add(job1);

            this.jobsService.onJobCompletion(job1._id(), this::handleJobFinished);

            subscriber.subscribe(technologyService.getTechnology(job.technology()), result -> {
                if (!technologies.contains(result)) {
                    technologies.add(result);
                }
            }, error -> System.out.println("Error in handleJob in ResearchComponent technology: \n" +
                    error.getMessage()));
            handleJobInformation();
        }, error -> System.out.println("Error in handleJob in ResearchComponent job: \n" +
                error.getMessage()));
    }

    public void setJobDescription(TechnologyExtended technology) {
        // Check if there are at least two tags
        if (technology.tags().length > 0) {
            Image image1 = new Image(Constants.technologyIconMap.get(technology.tags()[0]));
            technologyTagImage1.setImage(image1);
        }
        if (technology.tags().length > 1) {
            Image image2 = new Image(Constants.technologyIconMap.get(technology.tags()[1]));
            technologyTagImage2.setImage(image2);
        }

        double cost = technology.cost() * 100;
        researchCostText.setText(String.valueOf(cost));
        technologyNameText.setText(technologiesResourceBundle.getString(technology.id()));
    }

    public void setTechnologyCategoryComponent(TechnologyCategoryComponent technologyCategoryComponent) {
        this.technologyCategoryComponent = technologyCategoryComponent;
    }

    public void removeJob() {
        if (this.job != null && this.job._id() != null) {
            technologies.removeIf(technology -> technology.id().equals(job.technology()));

            subscriber.subscribe(jobsService.stopJob(this.job._id()), result -> {}, error -> {});
            technologyCategoryComponent.handleJobCompleted(job);
        }
    }

    @OnDestroy
    public void destroy() {

    }

}
