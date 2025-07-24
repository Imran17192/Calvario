package de.uniks.stp24.component.game;

import de.uniks.stp24.App;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import de.uniks.stp24.service.game.JobsService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.controlsfx.control.GridCell;
import org.controlsfx.control.GridView;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ResourceBundle;

@Component(view = "EmpireOverview.fxml")
public class EmpireOverviewComponent extends StackPane {
    @FXML
    Button closeEmpireOverviewButton;
    @FXML
    ImageView portraitContainer;
    @FXML
    ImageView flagContainer;
    @FXML
    Text empireNameContainer;
    @FXML
    Text empireDescriptionContainer;
    @FXML
    AnchorPane colourContainer;
    @FXML
    GridView<Island> islandGridView;

    @Inject
    App app;
    @Inject
    Subscriber subscriber;
    @Inject
    EventListener eventListener;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    EmpireService empireService;
    @Inject
    IslandsService islandsService;
    @Inject
    ImageCache imageCache;
    @Inject
    JobsService jobsService;

    @Inject
    @org.fulib.fx.annotation.controller.Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    private String empireID;
    public int portrait, flag;
    public String colour, empireName, empireDescription;

    public ObservableList<Island> islands = FXCollections.observableArrayList();
    FilteredList<Island> ownIslands;

    final String resourcesPaths = "/de/uniks/stp24/assets/";
    final String flagsFolderPath = "flags/flag_";
    final String portraitsFolderPath = "portraits/captain_";

    @Inject
    public EmpireOverviewComponent() {
    }


    /**
     * Initializes the list of empires and subscribes to updates from the EmpireService.
     */
    @OnInit
    public void initEmpireList() {
        if (!this.tokenStorage.isSpectator()) {
            String gameID = tokenStorage.getGameId();
            empireID = tokenStorage.getEmpireId();
            this.ownIslands = this.islands.filtered(empireIsland -> empireIsland.owner() != null && empireIsland.owner().equals(empireID));
            this.subscriber.subscribe(this.empireService.getEmpire(gameID, empireID),
                    this::empireTraits,
                    error -> System.out.println("Error in EmpireOverviewComponent on initEmpireList:\n" + error.getMessage()));
        }
    }

    /**
     * Closes the empire overview by hiding the parent component.
     */
    public void closeEmpireOverview() {
        this.setVisible(false);
    }

    /**
     * Updates the traits of the empire based on the provided EmpireDto.
     */
    private void empireTraits(EmpireDto empireDto) {
        this.portrait = empireDto.portrait();
        this.flag = empireDto.flag();
        this.colour = empireDto.color();
        this.empireName = empireDto.name();
        this.empireDescription = empireDto.description();
        addTraits();
        fillGrid();
    }

    /**
     * Adds the traits of the empire to the UI components.
     */
    private void addTraits() {
        flagContainer.setImage(imageCache.get(resourcesPaths + flagsFolderPath + this.flag + ".png"));

        empireNameContainer.setText(this.empireName);
        empireNameContainer.getStyleClass().add("empireOverviewTextWhite");

        empireDescriptionContainer.setText(this.empireDescription);
        empireDescriptionContainer.getStyleClass().add("empireOverviewTextBlack");

        portraitContainer.setImage(imageCache.get(resourcesPaths + portraitsFolderPath + this.portrait + ".png"));
        colourContainer.setStyle("-fx-background-color: " + this.colour);
    }


    /**
     * Filters the islands owned by the empire and fills the grid with island components.
     */
    public void fillGrid() {
        islands.addAll(islandsService.getListOfIslands());
        islandGridView.setItems(ownIslands);
        islandGridView.setCellFactory(grid -> new IslandCell(this.imageCache, this.jobsService, this.tokenStorage));
    }

    public void updateIslandList(Island island){
        this.islands.replaceAll(old -> old.equals(island) ? island : old);
    }


    /**
     * The IslandCell class represents a custom cell in the GridView for displaying islands.
     */
    public class IslandCell extends GridCell<Island> {
        private final ImageCache imageCache;
        private final JobsService jobsService;
        private final TokenStorage tokenStorage;

        public IslandCell(ImageCache imageCache, JobsService jobsService, TokenStorage tokenStorage) {
            this.jobsService = jobsService;
            this.tokenStorage = tokenStorage;
            this.imageCache = imageCache;

        }

        @Override
        protected void updateItem(Island item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                IslandCellComponent islandCellComponent = app.initAndRender(new IslandCellComponent(this.imageCache,  this.jobsService, this.tokenStorage));
                islandCellComponent.setItem(item);
                setGraphic(islandCellComponent);
            }
        }
    }


    /**
     * Cleans up resources when the component is destroyed.
     */
    @OnDestroy
    void destroy() {
        this.subscriber.dispose();
    }
}