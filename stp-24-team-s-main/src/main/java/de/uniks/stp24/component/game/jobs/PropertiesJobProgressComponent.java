package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.JobsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "PropertiesJobProgress.fxml")
public class PropertiesJobProgressComponent extends Pane {
    @FXML
    Text jobProgressText;
    @FXML
    ProgressBar jobProgressBar;
    @FXML
    ListView<Resource> costsListView;
    @Inject
    ImageCache imageCache;

    final Provider<ResourceComponent> negativeResourceProvider = () -> new ResourceComponent("negative", this.gameResourceBundle, this.imageCache);
    final ObservableList<Resource> resourceObservableList = FXCollections.observableArrayList();
    private double incrementAmount;
    private int progress;
    private int total;
    private boolean shouldTick = false;

    @Inject
    App app;

    @Inject
    Subscriber subscriber;
    @Inject
    public JobsService jobsService;

    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private Job job;

    @Inject
    public PropertiesJobProgressComponent() {

    }

    @OnInit
    public void init() {
        this.jobsService.onGameTicked(this::incrementProgress);
    }

    public void setJobProgress(Job job) {
        this.job = job;
        this.progress = job.progress();
        this.total = (int) job.total();
        this.jobProgressText.setText(String.format("%d/%s", this.progress, this.total));
        this.incrementAmount = (double) 1 /this.total;
        this.jobProgressBar.setProgress(this.progress*this.incrementAmount);
        this.resourceObservableList.clear();
        job.cost().forEach((name, amount) -> this.resourceObservableList.add(new Resource(name, amount, 0)));
        Platform.runLater(()-> this.costsListView.setItems(this.resourceObservableList));
        this.costsListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.negativeResourceProvider));
    }

    public void incrementProgress() {
        if (this.shouldTick) {
            this.progress++;
            this.jobProgressText.setText(String.format("%d/%s", this.progress, this.total));
            this.jobProgressBar.setProgress(this.progress * this.incrementAmount);
        }
    }

    public void cancelJob() {
        this.subscriber.subscribe(this.jobsService.stopJob(this.job), result -> {}, error -> {});
        this.getParent().setVisible(false);
        this.getParent().getParent().getParent().setVisible(false);
    }

    public void setShouldTick(boolean shouldTick) {
        this.shouldTick = shouldTick;
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
    }
}
