package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

import static de.uniks.stp24.model.Jobs.Job;

@Component(view = "IslandCell.fxml")
public class IslandCellComponent extends VBox {
    @FXML
    public ImageView islandImage;
    @FXML
    public Label islandNameLabel;

    private final ImageCache imageCache;
    private final TokenStorage tokenStorage;
    private final JobsService jobsService;
    private Island island;


    @Inject
    public IslandCellComponent(ImageCache imageCache, JobsService jobsService, TokenStorage tokenStorage) {
        this.jobsService = jobsService;
        this.tokenStorage = tokenStorage;
        this.imageCache = imageCache;
    }

    public void setItem(Island island){
        this.island = island;
        this.islandImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/buttons/IslandButton/button_" + island.type().name().toLowerCase() + ".png"));
        this.islandNameLabel.setText(island.name());
    }

    public void showIslandOverview(){
        Job fakeJob = new Job("", "", "", 0, 0, this.tokenStorage.getGameId(), this.tokenStorage.getEmpireId(), island.id(), 0,
                "", "", "", "", "", "", null, null, null);
        this.jobsService.getJobInspector("island_jobs_overview").accept(fakeJob);
    }

}
