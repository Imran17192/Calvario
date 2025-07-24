package de.uniks.stp24.component.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.JobsService;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "Buildings.fxml")
public class BuildingsComponent extends AnchorPane {

    @FXML
    public Button prev;
    @FXML
    public Button next;
    @FXML
    public GridPane buildings;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    public TokenStorage tokenStorage;

    @Inject
    public JobsService jobsService;
    @Inject
    public ImageCache imageCache;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private int currentPage = 0;
    private InGameController inGameController;
    final List<String[]> buildingList = new ArrayList<>();

    @Inject
    public BuildingsComponent() {
    }

    /*

    This method creates and updates the pages for showing all buildings of an island.
    If you click on next or prev the page that should appear will be updated with
    the buildings on next or prev page.

     */
    public void setGridPane() {
        buildings.getChildren().clear();
        this.buildingList.clear();

        Island island = this.islandAttributes.getIsland();

        if (Objects.nonNull(island)) {
            island.buildings().forEach(building -> this.buildingList.add(new String[]{building, null, ""}));

            ObservableList<Jobs.Job> islandJobs = this.jobsService.getObservableListForSystem(island.id());
            FilteredList<Jobs.Job> buildingJobs = islandJobs.filtered(job -> job.type().equals("building"));
            buildingJobs.forEach(job -> {
                if (islandJobs.stream().filter(islandJob -> !islandJob.type().equals("ship")).toList().indexOf(job) > 0)
                    this.buildingList.add(new String[]{job.building(), "on_pause", job._id()});
                else this.buildingList.add(new String[]{job.building(), "on_progress", job._id()});
            });

            prev.setVisible(currentPage > 0);

            int pageCapacity = 8;
            next.setVisible(this.buildingList.size() >= (currentPage + 1) * pageCapacity);

            int row = 0;
            int col = 0;

            for (int i = currentPage * pageCapacity; i < this.buildingList.size(); i++) {
                String[] buildingType = this.buildingList.get(i);
                Building building = new Building(this, buildingType[0], islandAttributes,
                        inGameController, buildingType[1], buildingType[2]);
                buildings.add(building, col, row);

                if ((i + 1) % 8 == 0) break;

                col++;
                if (col >= 4) {
                    col = 0;
                    row++;
                    if (row >= 2) break;
                }
            }

            if (!isGridPaneFull(currentPage)) {
                buildings.add(new Building(this, "buildNewBuilding", islandAttributes, inGameController, null, ""), col, row);
            } else {
                next.setMouseTransparent(false);
                next.setVisible(true);
            }
        }
    }

    /*

    Checks if page on current page is full.

     */
    public boolean isGridPaneFull(int pageToCheck) {
        if (!this.buildingList.isEmpty()) {
            int size = this.buildingList.size();
            int indexToCheck = (pageToCheck + 1) * 8 - 1;
            return indexToCheck < size;
        }
        return false;
    }

    public void resetPage() {
        prev.setVisible(false);
        next.setVisible(false);
        this.currentPage = 0;
    }

    public void goPrevSite() {
        currentPage = currentPage - 1;
        setGridPane();
    }

    public void goNextSite() {
            int size = this.buildingList.size();
            if (isGridPaneFull(currentPage + 1) || (!isGridPaneFull(currentPage + 1) && size % 8 != 0)) {
                currentPage = currentPage + 1;
                setGridPane();
            } else if (!isGridPaneFull(currentPage + 1) && size % 8 == 0) {
                currentPage = currentPage + 1;
                buildings.getChildren().clear();
                buildings.add(new Building(this, "buildNewBuilding", islandAttributes, inGameController,
                        null, ""), 0, 0);
                prev.setVisible(true);
                next.setVisible(false);
            }
    }

    public void setInGameController(InGameController inGameController){
        this.inGameController = inGameController;
    }

}
