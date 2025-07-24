package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "JobsOverview.fxml")
public class JobsOverviewComponent extends AnchorPane {
    @FXML
    public ToggleButton travelJobsButton;
    @FXML
    ListView<Job> jobsListView;
    @FXML
    Button closeButton;
    @Inject
    public JobsService jobsService;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    private ObservableList<Job> jobsList = FXCollections.observableArrayList();

    @Inject
    App app;

    @Inject
    public Provider<JobElementComponent> jobProvider;

    @Inject
    public JobsOverviewComponent() {
    }

    @OnRender
    public void setJobsObservableList() {
        this.jobsService.onJobsLoadingFinished(() -> {
            showIslandJobs();
            this.jobsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.jobProvider));
        });

        this.jobsService.setJobInspector("name_updates", (Jobs.Job job) -> {
            this.jobsListView.refresh();
            this.jobsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.jobProvider));
        });
    }

    public void closeOverview() {
        this.setVisible(false);
    }

    public void showIslandJobs(){
        this.jobsList = this.jobsService.getJobObservableListOfType("collection");
        this.jobsListView.setItems(this.jobsList);
    }

    public void showShipJobs(){
        this.jobsList = this.jobsService.getJobObservableListOfType("ship");
        this.jobsListView.setItems(this.jobsList);
    }

    public void showTravelJobs(){
        this.jobsList = this.jobsService.getJobObservableListOfType("travel");
        this.jobsListView.setItems(this.jobsList);
    }

    public void showTechnologyJobs(){
        this.jobsList = this.jobsService.getJobObservableListOfType("technology");
        this.jobsListView.setItems(this.jobsList);
    }
}
