package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobProgress.fxml")
public class IslandOverviewJobProgressComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Text jobTimeRemaining;
    @FXML
    Button stopJobButton;
    @FXML
    Button infoJobButton;
    @FXML
    Text jobDescriptionText;
    @FXML
    Text jobPositionText;
    @FXML
    ImageView jobImage;

    @FXML
    ListView<de.uniks.stp24.model.Resource> costsListView;
    public final Provider<ResourceComponent> negativeResourceProvider = () ->
            new ResourceComponent("negative", this.gameResourceBundle, this.imageCache);
    private final ObservableList<de.uniks.stp24.model.Resource> resourceObservableList = FXCollections.observableArrayList();

    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public JobsService jobsService;
    @Inject
    public ImageCache imageCache;
    @Inject
    public App app;

    @Inject
    public Subscriber subscriber;

    private Job job;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    public IslandOverviewJobProgressComponent() {
        this.setPickOnBounds(false);
    }

    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;
        this.jobProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
        this.stopJobButton.setId("jobProgressDeleteButton_" + job._id());
        this.infoJobButton.setId("jobProgressInspectionButton_" + job._id());

        ObservableList<Job> systemJobs = this.jobsService.getObservableListForSystem(job.system());
        FilteredList<Job> filteredJobs = systemJobs.filtered(systemJob -> !systemJob.type().equals("ship"));
        this.jobProgressBar.setVisible(true);
        this.jobTimeRemaining.setVisible(true);
        if (filteredJobs.indexOf(job) != 0 && !job.type().equals("ship")) {
            this.jobProgressBar.setVisible(false);
            this.jobTimeRemaining.setVisible(false);
        }

        this.jobPositionText.setText(systemJobs.indexOf(job)+1 + ".");
        this.jobTimeRemaining.setText(String.format("%s/%s", job.progress(), (int) job.total()));

        this.resourceObservableList.clear();
        job.cost().forEach((name, count) -> this.resourceObservableList
                .add(new de.uniks.stp24.model.Resource(name, count, 0)));
        this.costsListView.setItems(this.resourceObservableList);
        this.costsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));
        this.costsListView.setMouseTransparent(true);
        this.infoJobButton.setVisible(true);

        switch (job.type()) {
            case "building" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.buildingsIconPathsMap.get(job.building())));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString(
                        Constants.buildingTranslation.get(job.building())));
            }
            case "district" -> {
                this.jobImage.setImage(this.imageCache.get("/"+ Constants.sitesIconPathsMap.get(job.district())));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString(
                        Constants.siteTranslation.get(job.district())) + " Site");
            }
            case "upgrade" -> {
                this.jobImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/other/upgrade_job.png"));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString("jobs."+
                        this.islandAttributes.getIsland().upgrade()));
            }
            case "ship" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.shipIconMap.get(job.ship())));
                this.jobDescriptionText.setText(this.gameResourceBundle.getString("ship." + job.ship()));
                this.infoJobButton.setVisible(false);
            }
        }
    }

    public void showJobDetails() {
        switch (this.job.type()) {
            case "district" -> this.jobsService.getJobInspector("site_overview")
                    .accept(job);
            case "building" -> this.jobsService.getJobInspector("building_overview")
                    .accept(job);
            case "upgrade" -> this.jobsService.getJobInspector("island_upgrade")
                    .accept(job);
        }
    }

    public void stopJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job), result -> {}, error -> {});
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
