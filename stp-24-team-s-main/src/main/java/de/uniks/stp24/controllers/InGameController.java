package de.uniks.stp24.controllers;

import de.uniks.stp24.component.game.*;
import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import de.uniks.stp24.component.game.jobs.JobsOverviewComponent;
import de.uniks.stp24.component.game.technology.TechnologyOverviewComponent;
import de.uniks.stp24.component.menu.PauseMenuComponent;
import de.uniks.stp24.controllers.helper.Draggable;
import de.uniks.stp24.dto.EmpireDto;
import de.uniks.stp24.dto.SystemDto;
import de.uniks.stp24.model.GameStatus;
import de.uniks.stp24.model.Island;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.records.GameListenerTriple;
import de.uniks.stp24.rest.GameSystemsApiService;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.Constants.BUILT_STATUS;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.PopupBuilder;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.service.menu.LobbyService;
import de.uniks.stp24.ws.EventListener;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import static de.uniks.stp24.service.Constants.ISLAND_COLLISION_RADIUS;
import static de.uniks.stp24.service.Constants.ISLAND_SCALE;

@Title("CALVARIO")
@Controller
public class InGameController extends BasicController {
    @FXML
    public AnchorPane rootPane;
    @FXML
    public StackPane technologiesContainer;
    @FXML
    Pane gameBackground;
    @FXML
    public Pane fogPane;
    @FXML
    StackPane helpWindowContainer;
    @FXML
    StackPane hintCaptainContainer;
    @FXML
    StackPane warContainer;
    @FXML
    StackPane gameResultContainer;
    @FXML
    public Pane shadow;
    @FXML
    StackPane eventContainer;
    @FXML
    public HBox contextMenuButtons;

    public Pane debugGrid;

    @FXML
    public StackPane contextMenuContainer;
    @FXML
    public StackPane contactDetailsContainer;
    @FXML
    public Group group;
    @FXML
    public StackPane overviewContainer;
    @FXML
    public ScrollPane mapScrollPane;
    @FXML
    public Pane mapGrid;
    @FXML
    public StackPane zoomPane;
    @FXML
    public StackPane deleteStructureWarningContainer;
    @FXML
    public StackPane siteProperties;
    @FXML
    public StackPane buildingProperties;
    @FXML
    public StackPane buildingsWindow;
    @FXML
    StackPane pauseMenuContainer;
    @FXML
    StackPane islandClaimingContainer;
    @FXML
    StackPane battleResultContainer;

    @FXML
    public StackPane clockComponentContainer;

    @Inject
    public EventService eventService;
    @Inject
    public InGameService inGameService;
    @Inject
    EmpireService empireService;
    @Inject
    public IslandsService islandsService;
    @Inject
    public ExplanationService explanationService;
    @Inject
    ShipService shipService;

    @Inject
    public JobsService jobsService;
    @Inject
    public FleetService fleetService;
    @Inject
    public FleetCoordinationService fleetCoordinationService;
    @Inject
    public TechnologyService technologyService;
    @Inject
    GameStatService gameStatService;

    @Inject
    @SubComponent
    public BattleResultComponent battleResultComponent;

    @SubComponent
    @Inject
    public JobsOverviewComponent jobsOverviewComponent;
    @SubComponent
    @Inject
    public PauseMenuComponent pauseMenuComponent;
    @SubComponent
    @Inject
    public IslandClaimingComponent islandClaimingComponent;
    @SubComponent
    @Inject
    public OverviewSitesComponent overviewSitesComponent;
    @SubComponent
    @Inject
    public OverviewUpgradeComponent overviewUpgradeComponent;
    @SubComponent
    @Inject
    public StorageOverviewComponent storageOverviewComponent;
    @SubComponent
    @Inject
    public IslandTravelComponent islandTravelComponent;

    @SubComponent
    @Inject
    public EmpireOverviewComponent empireOverviewComponent;
    @SubComponent
    @Inject
    public HelpComponent helpComponent;
    @SubComponent
    @Inject
    public CoolerBubbleComponent coolerBubbleComponent;
    @SubComponent
    @Inject
    public ClockComponent clockComponent;
    @SubComponent
    @Inject
    public TechnologyOverviewComponent technologiesComponent;
    @SubComponent
    @Inject
    public MarketComponent marketOverviewComponent;
    @SubComponent
    @Inject
    public ContactsComponent contactsOverviewComponent;


    @SubComponent
    @Inject
    public DeleteStructureComponent deleteStructureComponent;
    @SubComponent
    @Inject
    public EventComponent eventComponent;
    @SubComponent
    @Inject
    public BuildingPropertiesComponent buildingPropertiesComponent;
    @SubComponent
    @Inject
    public BuildingsWindowComponent buildingsWindowComponent;
    @SubComponent
    @Inject
    public SitePropertiesComponent sitePropertiesComponent;
    @SubComponent
    @Inject
    public WarComponent warComponent;
    @SubComponent
    @Inject
    public GameResultPopup gameResultPopup;
    @SubComponent
    @Inject
    public FleetManagerComponent fleetManagerComponent;
    @Inject
    public FogOfWar fogOfWar;
    @Inject
    public BattleService battleService;

    public List<IslandComponent> islandComponentList;
    Map<String, IslandComponent> islandComponentMap;

    @Inject
    public Subscriber subscriber;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    EventListener eventListener;

    @Inject
    public InGameController() {
        lastUpdate = "";
    }


    @Inject
    public GameSystemsApiService gameSystemsApiService;
    @Inject
    public VariableService variableService;
    @Inject
    public LobbyService lobbyService;
    @Inject
    public ContactsService contactService;

    public IslandComponent selectedIsland;

    boolean pause = false;

    String gameID;
    String empireID;
    String lastUpdate;
    double scale = 1.0;
    private final List<GameListenerTriple> gameListenerTriple = new ArrayList<>();
    public final ArrayList<String> flagsPath = new ArrayList<>();
    final String resourcesPaths = "/de/uniks/stp24/assets/";
    final String flagsFolderPath = "flags/flag_";
    final PopupBuilder popupBuildingProperties = new PopupBuilder();
    final PopupBuilder popupBuildingWindow = new PopupBuilder();
    final PopupBuilder popupSiteProperties = new PopupBuilder();
    final PopupBuilder popupDeleteStructure = new PopupBuilder();
    final PopupBuilder popupHelpWindow = new PopupBuilder();

    int islandCollisionRadius = ISLAND_COLLISION_RADIUS;

    FadeTransition fadeTransition;
    Shape fog;
    Shape islandFog;
    ColorAdjust solarColorAdjust;
    ColorAdjust dayColorAdjust;
    Timeline solarTimeLine;
    Timeline dayTimeLine;

    final ArrayList<Node> draggables = new ArrayList<>();
    final ArrayList<GameFleetController> fleetsOnMap = new ArrayList<>();


    @OnRender
    public void addSpeechBubble() {
        hintCaptainContainer.getChildren().add(coolerBubbleComponent);
        coolerBubbleComponent.silence();
    }


    @OnInit
    public void init() {
        overviewSitesComponent.setIngameController(this);
        overviewUpgradeComponent.setIngameController(this);
        buildingsWindowComponent.setInGameController(this);
        buildingPropertiesComponent.setInGameController(this);
        sitePropertiesComponent.setInGameController(this);
        deleteStructureComponent.setInGameController(this);
        variableService.setIngameController(this);
        pauseMenuComponent.setInGameController(this);
        helpComponent.setInGameController(this);
        clockComponent.setInGameController(this);

        gameID = tokenStorage.getGameId();
        empireID = tokenStorage.getEmpireId();

        GameStatus gameStatus = inGameService.getGameStatus();
        PropertyChangeListener callHandlePauseChanged = this::handlePauseChanged;
        gameStatus.listeners().addPropertyChangeListener(GameStatus.PROPERTY_PAUSED, callHandlePauseChanged);
        this.gameListenerTriple.add(new GameListenerTriple(gameStatus, callHandlePauseChanged, "PROPERTY_PAUSED"));

        variableService.addRunnable(this::loadGameAttributes);
        variableService.initVariables();
        battleService.setBattleConditionUpdates();

        if (!tokenStorage.isSpectator()) {
            this.subscriber.subscribe(empireService.getEmpire(gameID, empireID),
                    result -> {
                        islandAttributes.setEmpireDto(result);
                        islandAttributes.setIsland(islandsService.getIsland(result.homeSystem()));
                    },
                    error -> System.out.println("error in getEmpire in inGame"));
            createEmpireListener();
        }

        for (int i = 0; i <= 16; i++) this.flagsPath.add(resourcesPaths + flagsFolderPath + i + ".png");
    }

    /*
      This method should be called every time after a job is done.
    */
    public void updateVariableDependencies() {
        variableService.loadVariablesDataStructure();
    }

    public void loadGameAttributes() {
        shipService.initShipTypes();
        islandAttributes.setSystemUpgradeAttributes();
        islandAttributes.setBuildingAttributes();
        islandAttributes.setDistrictAttributes();
        shipService.initShipTypes();
    }

    private void handlePauseChanged(@NotNull PropertyChangeEvent propertyChangeEvent) {
        if (Objects.nonNull(propertyChangeEvent.getNewValue())) {
            pause = (Boolean) propertyChangeEvent.getNewValue();
            if (pause) {
                shadow.setVisible(true);
                shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
                pauseGame();
            } else {
                if (!eventContainer.isVisible()) shadow.setVisible(false);
                resumeGame();
            }
        }
    }

    @OnRender
    public void render() {
        fogPane.setPickOnBounds(false);
        gameResultPopup.setInGameController(this);


        this.fleetCoordinationService.setInGameController(this);
        this.fleetCoordinationService.setJobFinishers();

        this.jobsService.loadEmpireJobs();
        this.jobsService.initializeJobsListeners();

        explanationService.setInGameController(this);

        buildingProperties.setMouseTransparent(true);
        buildingProperties.setPickOnBounds(false);
        buildingsWindow.setMouseTransparent(true);
        siteProperties.setMouseTransparent(true);
        siteProperties.setPickOnBounds(false);
        deleteStructureWarningContainer.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);
        helpComponent.setVisible(false);
        helpComponent.setMouseTransparent(true);

        warContainer.setVisible(false);
        warContainer.setPickOnBounds(false);
        warComponent.setVisible(false);
        warComponent.setMouseTransparent(false);

        gameResultContainer.setPickOnBounds(false);
        gameResultPopup.setVisible(false);
        gameResultPopup.setMouseTransparent(false);

        pauseMenuContainer.setMouseTransparent(true);
        pauseMenuContainer.setVisible(false);
        eventComponent.setParent(shadow, eventContainer);
        clockComponent.setToggle(true);
        clockComponentContainer.getChildren().add(clockComponent);
        eventContainer.getChildren().add(eventComponent);
        eventContainer.setVisible(false);
        shadow.setVisible(false);
        eventComponent.setClockComponent(clockComponent);
        eventComponent.setBackground(gameBackground);
        eventComponent.setInGameController(this);

        pauseMenuContainer.getChildren().add(pauseMenuComponent);

        overviewContainer.setVisible(false);
        overviewSitesComponent.setContainer();
        overviewContainer.getChildren().add(overviewSitesComponent);
        overviewContainer.getChildren().add(overviewUpgradeComponent);
        islandClaimingContainer.getChildren().add(this.islandClaimingComponent);
        islandClaimingContainer.setVisible(false);
        contextMenuContainer.setPickOnBounds(false);
        contextMenuContainer.getChildren().addAll(
                storageOverviewComponent,
                jobsOverviewComponent,
                empireOverviewComponent,
                marketOverviewComponent,
                fleetManagerComponent,
                contactsOverviewComponent
        );
        contextMenuContainer.getChildren().forEach(child -> {
            child.setVisible(false);
            // make every node in contextMenuContainer draggable
            draggables.add(child);
        });
        this.createContextMenuButtons();

        this.battleResultComponent.setVisible(false);
        this.battleResultContainer.setPickOnBounds(false);

        // make pop ups draggable
        draggables.add(eventContainer);
        draggables.add(deleteStructureWarningContainer);

        draggables.forEach(Draggable.DraggableNode::new);

        // make island overview draggable (the nodes attached to the overview will follow it)
        draggables.addAll(Arrays.asList(overviewContainer, buildingsWindow, buildingProperties, siteProperties));
        new Draggable.DraggableNode(overviewContainer, buildingsWindow, buildingProperties, siteProperties);

        this.group.getChildren().add(this.islandTravelComponent);
        this.islandTravelComponent.setVisible(false);

        contactsOverviewComponent.setParents(contextMenuContainer, contactDetailsContainer);
        contactsOverviewComponent.contactDetailsComponent.setWarComponent(warComponent);
        warComponent.setParent(warContainer);
        this.battleResultContainer.getChildren().add(battleResultComponent);
        battleService.setBattleResultComponent(battleResultComponent);
        contactService.setContactOverview(contactsOverviewComponent);

        this.gameResultContainer.getChildren().add(gameResultPopup);
        gameStatService.setGameResultPopup(gameResultPopup);

        this.fleetService.loadGameFleets();
        this.fleetService.initializeShipListener();
        this.fleetService.initializeFleetListeners();

//        this.mapGrid.setOnMouseClicked(this.fleetCoordinationService::travelToMousePosition);
        explanationService.setInGameController(this);

        technologiesComponent.setContainer(technologiesContainer);
        technologiesContainer.setVisible(false);
        technologiesContainer.getChildren().add(technologiesComponent);

        subscriber.subscribe(gameSystemsApiService.getSystems(tokenStorage.getGameId()),
                islands -> {
                    SystemDto system = islands[0];
                    Island island = islandsService.getIsland(system._id());
                    tokenStorage.setIsland(island);
                }, error -> System.out.println("Error try to get Systems because: " + error.getMessage()));

        contactDetailsContainer.setPickOnBounds(true);
        contactDetailsContainer.setVisible(false);

        /*
         * to avoid that the app.refresh() crashes the map
         * remove after app is entirely developed
         */
        rootPane.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.F5) {
                refreshMap();
                event.consume();
                System.out.println("in InGameController cannot be refreshed");
            }
        });
        contactService.getEmpiresInGame();

    }

    /*
     * to avoid that the app.refresh() crashes the map
     * remove after app is entirely developed
     */

    private void refreshMap() {
        mapGrid.getChildren().clear();
        createMap();
        mapGrid.getChildren().addAll(fleetsOnMap);
    }

    @OnKey(code = KeyCode.R, alt = true)
    public void resetDraggables() {
        for (Node draggable : draggables) {
            draggable.setTranslateX(0);
            draggable.setTranslateY(0);
        }
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void keyPressed() {
        helpComponent.setVisible(false);
        helpWindowContainer.setVisible(false);
        helpComponent.setMouseTransparent(true);
        helpWindowContainer.setMouseTransparent(true);

        pause = !pause;
        this.inGameService.gameStatus.firePropertyChange(GameStatus.PROPERTY_PAUSED, null, null);
        inGameService.setShowSettings(false);
        inGameService.setPaused(pause);
        if (pause) {
            shadow.setVisible(true);
            shadow.setStyle("-fx-opacity: 0.5; -fx-background-color: black");
            pauseMenuContainer.setMouseTransparent(false);
            pauseGame();
        } else {
            pauseMenuContainer.setMouseTransparent(true);
            resumeGame();
        }
    }

    @OnKey(code = KeyCode.J, alt = true)
    public void showJobsOverview() {
        this.toggleContextMenuVisibility(this.jobsOverviewComponent);
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void showStorageOverview() {
        this.toggleContextMenuVisibility(this.storageOverviewComponent);
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void showEmpireOverview() {
        this.toggleContextMenuVisibility(this.empireOverviewComponent);
    }

    @OnKey(code = KeyCode.M, alt = true)
    public void showMarket() {
        this.toggleContextMenuVisibility(this.marketOverviewComponent);
    }

    @OnKey(code = KeyCode.C, alt = true)
    public void showContacts() {this.toggleContextMenuVisibility(this.contactsOverviewComponent);}

    @OnKey(code = KeyCode.T, alt = true)
    public void showTechnologies() {
        technologiesContainer.setVisible(!technologiesContainer.isVisible());
        technologiesContainer.getChildren().getFirst().setVisible(false);
        technologiesContainer.getChildren().getLast().setVisible(true);
    }

    @OnKey(code = KeyCode.F, alt = true)
    public void showFleetManager() {
        this.toggleContextMenuVisibility(this.fleetManagerComponent);
        this.fleetManagerComponent.showFleets();
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void showHelpOverview() {
        if (this.helpComponent.isVisible()) {
            this.shadow.setVisible(false);
            this.helpComponent.close();
            shadow.setVisible(false);
            this.removePause();
        } else {
            this.shadow.setVisible(true);
            showHelp();
        }
    }

    private void toggleContextMenuVisibility(Node node) {
        if (!tokenStorage.isSpectator()) {
            this.contextMenuContainer.getChildren().stream()
                    .filter(child -> !child.equals(node))
                    .forEach(child -> child.setVisible(false));
            node.setVisible(!node.isVisible());
        }
    }

    public void pauseGame() {
        closeComponents();
        pauseMenuComponent.setVisible(true);
        pauseMenuContainer.setVisible(pause);
        pauseMenuContainer.setMouseTransparent(false);
    }

    public void pauseGameFromHelp() {
        pause = true;
        inGameService.setPaused(true);
        if (pause) {
            pauseMenuContainer.setMouseTransparent(false);
            shadow.setVisible(true);
            pauseGame();
        } else {
            pauseMenuContainer.setMouseTransparent(true);
            resumeGame();
        }
    }

    public void closeComponents() {
        this.contextMenuContainer.getChildren().forEach(child -> child.setVisible(false));
        this.contactsOverviewComponent.closeContactsComponent();
    }

    public void resumeGame() {
        pauseMenuContainer.setVisible(pause);
    }

    public void removePause() {
        pause = false;
        pauseMenuContainer.setVisible(false);
        shadow.setVisible(false);
    }

    /**
     * Please read the {@link ContextMenuButton ContextMenuButton} documentation to add additional context menu nodes.
     */
    private void createContextMenuButtons() {
        if (!tokenStorage.isSpectator())
            this.contextMenuButtons.getChildren().addAll(
                    new ContextMenuButton("storageOverview", this.storageOverviewComponent),
                    new ContextMenuButton("empireOverview", this.empireOverviewComponent),
                    new ContextMenuButton("jobsOverview", this.jobsOverviewComponent));
    }

    @OnRender
    public void createMap() {
        this.islandComponentList = islandsService.createIslands(islandsService.getListOfIslands());
        this.islandComponentMap = islandsService.getComponentMap();
        double x = islandsService.getMapWidth();
        double y = islandsService.getMapHeight();
        mapGrid.setMinSize(x, y);
        fogOfWar.setMapSize(x, y);
        fogOfWar.init(this);
        islandsService.createLines(this.islandComponentMap).forEach(line -> this.mapGrid.getChildren().add(line));
        islandsService.generateDistancePoints();
        islandsService.getDistancePoints().forEach((ids, value) -> {
            var point = value.get(value.size() / 2);
            Label distLabel = new Label(" "+ islandsService.getDistance(ids[0], ids[1]) + " ");
            distLabel.setStyle("-fx-font-size: 28; -fx-font-weight: bold; -fx-text-fill: #e43900;" +
                               "-fx-background-color: white; -fx-background-radius: 10");
            distLabel.setVisible(false);
            this.mapGrid.getChildren().add(distLabel);
            distLabel.setLayoutX(point.getX() - 16);
            distLabel.setLayoutY(point.getY() - 16);
        });

        group.setScaleX(0.65);
        group.setScaleY(0.65);

        this.islandComponentList.forEach(isle -> {
            isle.setInGameController(this);
            isle.addEventHandler(MouseEvent.MOUSE_CLICKED, this::showInfo);
            isle.setScaleX(ISLAND_SCALE);
            isle.setScaleY(ISLAND_SCALE);
            isle.collisionCircle.setRadius(islandCollisionRadius);
            this.fogPane.getChildren().add(isle);

            if (Objects.nonNull(isle.island.owner()) && isle.island.owner().equals(tokenStorage.getEmpireId()))
                removeFogFromIsland(false, isle);
        });

        mapScrollPane.viewportBoundsProperty().addListener((observable, oldValue, newValue) -> zoomPane.setPrefSize(newValue.getWidth(), newValue.getHeight()));
        mapScrollPane.setVvalue(0.5);
        mapScrollPane.setHvalue(0.5);

        /*
         * zoom function working but not perfect!
         * it's necessary to check deltaX and deltaY because 'shiftdown' switches deltas in event
         */
        zoomPane.setOnScroll(event -> {
            if (event.isShiftDown() && (event.getDeltaY() > 0 || event.getDeltaX() > 0)) {
                scale += 0.1;
                scale = Math.min(scale, 3);
                event.consume();
            } else if (event.isShiftDown() && (event.getDeltaY() < 0 || event.getDeltaX() < 0)) {
                scale -= 0.1;
                scale = Math.max(scale, 0.35);
                event.consume();
            }
            group.setScaleX(scale);
            group.setScaleY(scale);
        });

        // Event Listener for Island changes
        this.subscriber.subscribe(this.eventListener.listen(String.format("games.%s.systems.%s.updated",
                        tokenStorage.getGameId(), "*"), SystemDto.class),
                event -> {

                    IslandComponent isle = islandsService.getIslandComponent(event.data()._id());
                    Island oldIsland = isle.getIsland();
                    System.out.println("gameResult first id and second id 1: ");
                    if(Objects.nonNull(event.data().owner()) && !event.data().owner().equals(isle.getIsland().owner())){
                        System.out.print("gameResult: 2 ");
                        System.out.println("empireIslands a"+ this.islandsService.empireIslands.get(event.data().owner()));
                        System.out.println("size "+ this.islandsService.empireIslands.get(event.data().owner()).size());
                        islandsService.empireIslands.get(event.data().owner()).add(isle.getIsland());
                        System.out.println("empireIslands b"+ this.islandsService.empireIslands.get(event.data().owner()));
                        System.out.println("size "+ this.islandsService.empireIslands.get(event.data().owner()).size());
                        if(Objects.nonNull(isle.getIsland().owner())){
                            System.out.println("gameResult: 3");
                            islandsService.empireIslands.get(isle.getIsland().owner()).remove(isle.island);
                            System.out.println("empireIslands c"+ this.islandsService.empireIslands.get(isle.getIsland().owner()));
                            System.out.println("size "+ this.islandsService.empireIslands.get(isle.getIsland().owner()));
                            gameStatService.checkState();
                        }
                    }

                    Island updatedIsland = islandsService.convertToIsland(event.data());
                    isle.applyInfo(updatedIsland);

                    if (Objects.nonNull(updatedIsland.owner())) {

                        if (Objects.nonNull(oldIsland.owner()) && !oldIsland.owner().equals(event.data().owner()))
                            this.battleService.checkBattleConditionOnIslandClaimed(isle.island);

                        // apply drop shadow and flag
                        isle.applyEmpireInfo();
                        this.islandsService.updateIsles(updatedIsland);
                        this.empireOverviewComponent.updateIslandList(updatedIsland);
                        // island is already claimed
                        this.islandClaimingContainer.setVisible(false);
                        // remove Fog from Island if the island is owned by the player
                        if (updatedIsland.owner().equals(tokenStorage.getEmpireId())) {
                            removeFogFromIsland(true, isle);
                            // island is already claimed
                            this.islandClaimingContainer.setVisible(false);
                        }
                    }
                    // check if the island/upgrade overview is visible for the updated island
                    if (Objects.nonNull(selectedIsland) &&
                            updatedIsland.id().equals(selectedIsland.island.id()) &&
                            (overviewSitesComponent.isVisible() || overviewUpgradeComponent.isVisible())) {
                        islandAttributes.setIsland(updatedIsland);
                        String shownPage = overviewSitesComponent.getShownPage();
                        // open the island overview again with updated information
                        showOverview();
                        switch (shownPage) {
                            case "upgrade" -> overviewSitesComponent.showUpgrades();
                            case "details" -> overviewSitesComponent.showDetails();
                            case "buildings" -> overviewSitesComponent.showBuildings();
                            case "sites" -> overviewSitesComponent.showSites();
                            case "jobs" -> overviewSitesComponent.showJobs();
                        }
                    }
                },Throwable::printStackTrace
        );

        if (!tokenStorage.isSpectator())
            Platform.runLater(() -> {
            Button showTechnologiesButton = new Button();
            showTechnologiesButton.setId("showTechnologiesButton");
            showTechnologiesButton.setOnAction(event -> showTechnologies());
            showTechnologiesButton.setId("technologiesButton");
            showTechnologiesButton.getStyleClass().add("technologiesButton");
            contextMenuButtons.getChildren().addAll(showTechnologiesButton,
              new ContextMenuButton("marketOverview", this.marketOverviewComponent),
              new ContextMenuButton("fleetManager", fleetManagerComponent),
              new ContextMenuButton("contactsOverview", this.contactsOverviewComponent));
        });
    }

    public void showInfo(MouseEvent event) {
        if (event.getSource() instanceof IslandComponent selected) {
            tokenStorage.setIsland(selected.getIsland());
            selectedIsland = selected;
            tokenStorage.setIsland(selectedIsland.getIsland());
            islandAttributes.setIsland(selectedIsland.getIsland());
            if (Objects.nonNull(selected.getIsland().owner()) && !selected.foggy) {
                this.islandClaimingContainer.setVisible(false);
                this.sitePropertiesComponent.setVisible(false);
                this.buildingPropertiesComponent.setVisible(false);
                // show only for your own islands
                if (selected.island.owner().equals(this.tokenStorage.getEmpireId())) {
                    this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                      this.jobsService.getObservableListForSystem(this.tokenStorage.getIsland().id()));
                    showOverview();
                    selected.showUnshowRudder();
                    selected.getDefenceAndHealth(this.empireID);
                }
                selected.showDefenseAndHealth();

                this.islandTravelComponent.setVisible(true);
                this.islandTravelComponent.setIslandInformation(selected.island);

                this.islandTravelComponent.setLayoutX(selected.getLayoutX()-100);
                this.islandTravelComponent.setLayoutY(selected.getLayoutY()+80);

                // Show island claiming scroll
            } else if (!this.tokenStorage.isSpectator()) {
                this.islandTravelComponent.setVisible(false);
                if (Objects.nonNull(selectedIsland)) selectedIsland.showUnshowRudder();
                this.overviewSitesComponent.closeOverview();
                if (this.islandClaimingContainer.getLayoutX()+80 == selected.getLayoutX() &&
                        this.islandClaimingContainer.getLayoutY()+220 == selected.getLayoutY() &&
                        this.islandClaimingContainer.isVisible()) {
                    this.islandClaimingContainer.setVisible(false);
                } else {
                    this.islandClaimingContainer.setVisible(true);
                    this.islandClaimingContainer.setLayoutX(selected.getLayoutX()-80);
                    this.islandClaimingContainer.setLayoutY(selected.getLayoutY()-220);
                    this.islandClaimingComponent.setIslandInformation(selected.island);
                }
            }
        }
    }


    @OnRender
    public void setJobInspectors() {
        this.jobsService.setJobInspector("island_jobs_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            if (selected.upgrade().equals("unexplored") || selected.upgrade().equals("explored")) return;

            this.tokenStorage.setIsland(selected);
            this.overviewSitesComponent.jobsComponent.setJobsObservableList(
                    this.jobsService.getObservableListForSystem(job.system()));

            this.islandAttributes.setIsland(selected);
            selectedIsland = this.islandsService.getIslandComponent(job.system());
            if (Objects.nonNull(selected.owner())) {
                showOverview();
                this.overviewSitesComponent.showJobs();
            }
        });

        this.jobsService.setJobInspector("island_upgrade", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.overviewSitesComponent.showUpgrades();
        });

        this.jobsService.setJobInspector("site_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.setSiteType(job.district());
            this.showSiteOverview();
        });

        this.jobsService.setJobInspector("building_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            this.showBuildingInformation(job.building(), job._id(), BUILT_STATUS.QUEUED);
        });

        this.jobsService.setJobInspector("building_done_overview", (Jobs.Job job) -> {
            Island selected = this.islandsService.getIsland(job.system());
            this.islandAttributes.setIsland(selected);
            this.tokenStorage.setIsland(selected);
            // after the job is done, the isBuilt should be BUILT cause the building is built!
            this.showBuildingInformation(job.building(), "", BUILT_STATUS.BUILT);
        });

        this.jobsService.setJobInspector("storage_overview", (Jobs.Job job) -> showStorageOverview());

        this.jobsService.setJobInspector("technology_overview", (Jobs.Job job) ->
                showTechnologies()
        );

        this.jobsService.setJobInspector("show_fleet_manager", (Jobs.Job job) ->
                showFleetManager()
        );
    }

    public void showOverview() {
            overviewSitesComponent.buildingsComponent.resetPage();
            overviewSitesComponent.buildingsComponent.setGridPane();
            overviewContainer.setVisible(true);
            overviewSitesComponent.sitesContainer.setVisible(true);
            overviewSitesComponent.buildingsButton.setDisable(true);
            inGameService.showOnly(overviewContainer, overviewSitesComponent);
            inGameService.showOnly(overviewSitesComponent.sitesContainer, overviewSitesComponent.buildingsComponent);
            overviewSitesComponent.setOverviewSites();
            // update island name
            if (Objects.nonNull(this.islandAttributes.getIsland()) && !this.islandAttributes.getIsland().name().isEmpty())
                overviewSitesComponent.inputIslandName.setText(this.islandAttributes.getIsland().name());
    }

    @OnKey(code = KeyCode.SPACE)
    public void resetZoom() {
        scale = 0.65;
        group.setScaleX(scale);
        group.setScaleY(scale);
    }

    @OnKey(code = KeyCode.R, control = true)
    public void setCollisionVisibility() {
        this.mapGrid.getChildren().forEach(child -> {
            if (child instanceof IslandComponent island) island.collisionCircle.setVisible(!island.collisionCircle.isVisible());
            if (child instanceof GameFleetController fleet) fleet.collisionCircle.setVisible(!fleet.collisionCircle.isVisible());
        });
    }

    @OnKey(code = KeyCode.D, control = true)
    public void setIDsVisibility() {
        this.mapGrid.getChildren().stream().filter(child -> child instanceof Label)
                .forEach(label -> label.setVisible(!label.isVisible()));
    }

    @OnKey(code = KeyCode.D, shift = true)
    public void setDebugVisibility() {
        this.debugGrid.setVisible(!this.debugGrid.isVisible());
    }

    public void resetZoomMouse(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.MIDDLE) {
            resetZoom();
        }
    }

    public void showBuildingInformation(String buildingToAdd, String jobID, BUILT_STATUS isBuilt) {
        siteProperties.setVisible(false);
        siteProperties.setMouseTransparent(true);
        buildingPropertiesComponent.setBuildingType(buildingToAdd, jobID, isBuilt);
        popupBuildingProperties.showPopup(buildingProperties, buildingPropertiesComponent);
    }

    public void handleDeleteStructure(String buildingType) {
        deleteStructureWarningContainer.setMouseTransparent(false);
        popupDeleteStructure.showPopup(deleteStructureWarningContainer, deleteStructureComponent);
        popupDeleteStructure.setBlur(buildingProperties, buildingsWindow);
        popupBuildingProperties.setBlur(mapScrollPane, siteProperties);
        deleteStructureComponent.handleDeleteStructure(buildingType);
    }

    public void updateAmountSitesGrid() {
        sitePropertiesComponent.displayAmountOfSite();
    }

    public void handleAfterStructureDelete() {
        deleteStructureWarningContainer.setMouseTransparent(true);

        buildingsWindow.setMouseTransparent(false);
        popupDeleteStructure.removeBlur();
        popupBuildingProperties.removeBlur();
        if (!siteProperties.isVisible()) {
            siteProperties.setMouseTransparent(true);
        }
    }

    public void createEmpireListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".empires." + tokenStorage.getEmpireId() + ".updated", EmpireDto.class),
                event -> {
                    if (!lastUpdate.equals(event.data().updatedAt())) {
                        islandAttributes.setEmpireDto(event.data());
//                        overviewUpgradeComponent.setUpgradeButton();
                        this.lastUpdate = event.data().updatedAt();
                    }
                },
                error -> System.out.println("errorListener: " + error)
        );
    }

    public void showBuildingWindow() {
        siteProperties.setVisible(false);
        buildingsWindow.setMouseTransparent(false);
        popupBuildingWindow.showPopup(buildingsWindow, buildingsWindowComponent);
        buildingProperties.setMouseTransparent(false);
    }

    public void showSiteOverview() {
        siteProperties.setMouseTransparent(false);
        buildingsWindow.setVisible(false);
        buildingProperties.setVisible(false);
        popupSiteProperties.showPopup(siteProperties, sitePropertiesComponent);
    }

    public void setSiteType(String siteType) {
        sitePropertiesComponent.setSiteType(siteType);
    }

    public void setSitePropertiesInvisible() {
        sitePropertiesComponent.setVisible(false);
        buildingProperties.setMouseTransparent(false);
        overviewSitesComponent.buildingsComponent.setGridPane();
    }

    public void     showHelp() {
        this.shadow.setVisible(true);
        popupHelpWindow.showPopup(helpWindowContainer,helpComponent);
        helpComponent.setVisible(true);
        shadow.setVisible(true);
        helpComponent.setMouseTransparent(false);
        helpWindowContainer.setMouseTransparent(false);
        pauseMenuContainer.setVisible(false);
        pauseMenuContainer.setMouseTransparent(true);
        helpComponent.displayTechnologies();
    }

    private void removeIslands() {
        // removes islands from map
        islandComponentList.forEach(IslandComponent::destroy);
        islandComponentList = null;
        islandComponentMap = null;
        islandsService.removeDataForMap();
    }


    @OnDestroy
    public void destroy() {
        removeIslands();
        this.gameListenerTriple.forEach(triple -> triple.game().listeners()
                .removePropertyChangeListener(triple.propertyName(), triple.listener()));
        this.subscriber.dispose();
        this.jobsService.dispose();
        this.fleetService.dispose();
        this.fleetCoordinationService.dispose();
        this.variableService.dispose();
        this.contactService.dispose();
        this.islandsService.destroy();
        this.battleService.dispose();
    }


    // FOG OF WAR //
    @OnInit
    public void initFog() {
        fadeTransition = new FadeTransition(Duration.millis(2000));
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.setCycleCount(0);
        fadeTransition.setAutoReverse(false);

        this.solarColorAdjust = new ColorAdjust();
        this.solarColorAdjust.setBrightness(-0.75);
        this.solarColorAdjust.setContrast(-0.75);
        this.solarColorAdjust.setSaturation(-1);

        this.dayColorAdjust = new ColorAdjust();

        solarTimeLine = new Timeline(new KeyFrame(
                Duration.seconds(7),
                new KeyValue(this.dayColorAdjust.contrastProperty(), this.solarColorAdjust.getContrast(), Interpolator.EASE_BOTH),
                new KeyValue(this.dayColorAdjust.brightnessProperty(), this.solarColorAdjust.getBrightness(), Interpolator.EASE_BOTH),
                new KeyValue(this.dayColorAdjust.saturationProperty(), this.solarColorAdjust.getSaturation(), Interpolator.EASE_BOTH)
        ));
        solarTimeLine.setCycleCount(1);
        solarTimeLine.setAutoReverse(false);

        dayTimeLine = new Timeline(new KeyFrame(
                Duration.seconds(3),
                new KeyValue(this.solarColorAdjust.contrastProperty(), this.dayColorAdjust.getContrast(), Interpolator.EASE_BOTH),
                new KeyValue(this.solarColorAdjust.brightnessProperty(), this.dayColorAdjust.getBrightness(), Interpolator.EASE_BOTH),
                new KeyValue(this.solarColorAdjust.saturationProperty(), this.dayColorAdjust.getSaturation(), Interpolator.EASE_BOTH)
        ));
        dayTimeLine.setCycleCount(1);
        dayTimeLine.setAutoReverse(false);
    }

    public void removeFogFromIsland(boolean animate, IslandComponent island) {
        if (island.foggy) {
            this.fogOfWar.removeFogFromIsland(island);
            if (animate) this.updateFogAnimated();
            else this.updateFog();
        }
    }

    public void removeFogFromShape(Shape shape) {
        this.fogOfWar.removeShapesFromFog(shape);
        this.updateFog();
    }

    public void updateFogAnimated() {
        this.islandFog = this.fogOfWar.getIslandFog();
        fadeTransition.setNode(this.islandFog);
        zoomPane.getChildren().remove(this.islandFog);
        zoomPane.getChildren().add(2, this.islandFog);
        fadeTransition.setOnFinished(event -> {
            zoomPane.getChildren().remove(this.islandFog);
            this.updateFog();
        });
        fadeTransition.play();
    }

    public void updateFog() {
        zoomPane.getChildren().remove(this.fog);
        this.fog = fogOfWar.getCurrentFog();
        zoomPane.getChildren().add(2, this.fog);
    }

    public void darkenFog() {
        solarTimeLine.stop();
        this.zoomPane.getChildren().get(2).setEffect(dayColorAdjust);
        solarTimeLine.setOnFinished(event -> darkenFogNotAnimated());
        solarTimeLine.play();
    }

    public void darkenFogNotAnimated() {
        this.fogOfWar.setIsNight(true);
        if (!tokenStorage.isSpectator())
            this.islandComponentList.forEach(isle -> isle.changeBlendMode(BlendMode.MULTIPLY));
        this.dayColorAdjust = new ColorAdjust();
        this.zoomPane.getChildren().get(2).setEffect(solarColorAdjust);
    }

    public void brightenFog() {
        dayTimeLine.stop();
        this.zoomPane.getChildren().get(2).setEffect(solarColorAdjust);
        dayTimeLine.setOnFinished(event -> brightenFogNotAnimated());
        dayTimeLine.play();
    }

    public void brightenFogNotAnimated() {
        this.fogOfWar.setIsNight(false);
        if (!tokenStorage.isSpectator())
            this.islandComponentList.forEach(isle -> isle.changeBlendMode(BlendMode.LIGHTEN));
        this.solarColorAdjust.setBrightness(-0.75);
        this.solarColorAdjust.setContrast(-0.75);
        this.solarColorAdjust.setSaturation(-1);
        this.zoomPane.getChildren().get(2).setEffect(dayColorAdjust);
    }

    @OnDestroy
    public void saveFog() {
        fogOfWar.saveFog();
    }
}
