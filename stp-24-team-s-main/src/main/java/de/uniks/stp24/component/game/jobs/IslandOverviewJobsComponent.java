package de.uniks.stp24.component.game.jobs;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Jobs.Job;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "IslandOverviewJobs.fxml")
public class IslandOverviewJobsComponent extends AnchorPane {
    @FXML
    public Text noJobText;
    @FXML
    ListView<Job> jobProgressListView;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    IslandsService islandsService;
    @Inject
    App app;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    @Inject
    public Provider<IslandOverviewJobProgressComponent> progressPaneProvider;

    @Inject
    public JobsService jobsService;

    @Inject
    IslandOverviewJobsComponent() {
    }

    @OnRender
    public void setJobProgressPane() {
        this.jobProgressListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.progressPaneProvider));
    }

    @OnInit
    public void setTextVisibility() {
        this.jobsService.onJobCommonStart(() -> this.noJobText.setVisible(false));
        this.jobsService.onJobCommonFinish(() -> {
            if (Objects.nonNull(this.islandAttributes.getIsland())) {
                if (this.jobsService.getObservableListForSystem(this.islandAttributes.getIsland().id()).isEmpty())
                    this.noJobText.setVisible(true);
            }
        });
    }


    public void insertIslandName() {
        this.noJobText.setText(this.noJobText.getText()
                .replace("{ISLAND_NAME}", this.islandsService.getIslandName(this.islandAttributes.getIsland().id())));
    }

    public void setJobsObservableList(ObservableList<Job> observer) {
        if (Objects.nonNull(this.islandAttributes.getIsland()) && Objects.nonNull(this.islandAttributes.getIsland().owner()) && !islandAttributes.getIsland().owner().equals(this.tokenStorage.getEmpireId()))
            this.noJobText.setText(this.gameResourceBundle.getString("overview.jobs.enemyIsland"));
        else this.noJobText.setText(this.gameResourceBundle.getString("jobs.island.noJob"));
        this.noJobText.setVisible(observer.isEmpty());
        this.jobProgressListView.setItems(observer);
    }
}
