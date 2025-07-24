package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.model.Jobs;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "IslandUpgradesJobProgress.fxml")
public class IslandUpgradesJobProgressComponent extends Pane {
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    Text jobTimerText;

    private int jobProgress = 0;
    private int totalJobProgress = 0;
    private boolean shouldTick = false;

    @Inject
    public IslandUpgradesJobProgressComponent() {}

    public void setJobProgress(Jobs.Job job) {
        this.jobProgress = job.progress();
        this.totalJobProgress = (int) job.total();
        this.jobProgressBar.setProgress((((double) 1/job.total()) * job.progress()));
        this.jobTimerText.setText(String.format("%s/%s", job.progress(), job.total()));
    }

    public void incrementJobProgress() {
        if (this.shouldTick) {
            this.jobProgress++;
            this.jobTimerText.setText(String.format("%s/%s", this.jobProgress, this.totalJobProgress));
            this.jobProgressBar.setProgress((((double) 1/this.totalJobProgress) * this.jobProgress));
        }
    }

    public void setShouldTick(boolean shouldTick) {
        this.shouldTick = shouldTick;
    }
}
