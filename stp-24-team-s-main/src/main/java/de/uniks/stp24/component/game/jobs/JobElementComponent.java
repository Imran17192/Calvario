package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.FleetService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "JobElement.fxml")
public class JobElementComponent extends Pane implements ReusableItemComponent<Job> {
    @FXML
    public ImageView jobImage;
    @FXML
    public Text jobNameText;
    @FXML
    public Text jobTypeText;
    @FXML
    public Text timerText;
    @FXML
    Button jobCancelButton;
    @FXML
    Button inspectionButton;

    @Inject
    public ImageCache imageCache;

    @Inject
    public IslandsService islandsService;
    @Inject
    public JobsService jobsService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public FleetService fleetService;
    @Inject
    public TechnologyService technologyService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    private Job job;

    @Inject
    public JobElementComponent() {

    }

    @Override
    public void setItem(@NotNull Job job) {
        this.job = job;
        this.jobCancelButton.setId("jobElementDeleteButton_" + job._id());
        this.inspectionButton.setId("jobElementInspectionButton_" + job._id());

        this.inspectionButton.setVisible(true);
        this.jobCancelButton.setVisible(true);
        Island island = this.islandsService.getIsland(job.system());
        this.timerText.setText(String.format("%s/%s", job.progress(), (int) job.total()));
        if(island != null) {
            this.jobNameText.setText(island.name());
        }
        switch (job.type()) {
            case "building" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.buildingsIconPathsMap.get(job.building())));
                this.jobTypeText.setText(this.gameResourceBundle.getString(
                        Constants.buildingTranslation.get(job.building())));
            }
            case "district" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.sitesIconPathsMap.get(job.district())));
                this.jobTypeText.setText(this.gameResourceBundle.getString(
                        Constants.siteTranslation.get(job.district())) + " Site");
            }
            case "upgrade" -> {
                assert island != null;
                if (island.upgrade().equals("unexplored") || island.upgrade().equals("explored")) {
                    this.inspectionButton.setVisible(false);
                }
                this.jobImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/other/upgrade_job.png"));
                this.jobTypeText.setText(this.gameResourceBundle.getString("jobs." + island.upgrade()));
            }
            case "ship" -> {
                this.jobImage.setImage(this.imageCache.get("/" + Constants.shipIconMap.get(job.ship())));
                this.jobTypeText.setText(this.gameResourceBundle.getString("ship." + job.ship()) + " - "
                        + this.fleetService.getFleet(job.fleet()).name());
            }
            case "technology" -> {
                this.jobCancelButton.setVisible(false);
                this.jobNameText.setText(this.gameResourceBundle.getString("technologies." + this.technologyService.getTechnologyCategory(job.technology())));
                this.jobImage.setImage(this.imageCache.get("assets/technologies/tags/" + this.technologyService.getTechnologyCategory(job.technology()) + ".png"));
                this.jobTypeText.setText(this.technologiesResourceBundle.getString(job.technology()));
            }
            case "travel" -> {
                this.inspectionButton.setVisible(false);
                if (Objects.nonNull(this.fleetService.getFleet(job.fleet()))) {
                    this.jobNameText.setText(this.fleetService.getFleet(job.fleet()).name());
                    this.jobImage.setImage(this.imageCache.get("icons/ships/ship_Image_With_Frame1.png"));
                    String islandName = (this.islandsService.getIsland(job.path().getLast()).name() != null ?
                            this.islandsService.getIsland(job.path().getLast()).name() : "");
                    this.jobTypeText.setText(this.gameResourceBundle.getString("travelling.to") + " " + islandName);
                }
            }
        }
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job), result -> {}, error -> {});
    }

    public void showJobOverview() {
        if(job.type().equals("technology")) {
            this.jobsService.getJobInspector("technology_overview").accept(job);
        } else {
            this.jobsService.getJobInspector("island_jobs_overview").accept(job);
        }
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
