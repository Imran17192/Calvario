package de.uniks.stp24;


import de.uniks.stp24.appTestModules.InGameTestComponent;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Resource;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static de.uniks.stp24.service.Constants.buildingTranslation;
import static de.uniks.stp24.service.Constants.siteTranslation;
import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class AppTest2 extends InGameTestComponent {

    Button homeIsland;
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.getScene().getStylesheets().clear();
        initComponents();
        inGameController.islandClaimingComponent.setVisible(false);
        inGameController.rootPane.getStylesheets().clear();
        inGameController.overviewSitesComponent.imagePane.getStylesheets().clear();
        inGameController.overviewSitesComponent.islandFlag.getStylesheets().clear();
        inGameController.empireOverviewComponent.getStylesheets().clear();

    }

    @Test
    public void v2() {
        createMap();

        // Island Overview
        openIslandOverview();
        goToBuildings();
        buildBuilding();
        destroyBuilding();
        goToSites();
        destroySiteCell();
        buildSiteCell();
        goToUpgrade();
        upgradeIsland();
        closeIslandOverview();

        // Storage Overview
        checkStorageOverview();

        // Empire Overview
        checkEmpireOverview();
    }

    public void createMap() {
        homeIsland = new Button();
        homeIsland.setLayoutX(500);
        homeIsland.setLayoutY(500);
        homeIsland.setPrefWidth(50);
        homeIsland.setPrefHeight(50);
        homeIsland.setId("homeIsland");
        homeIsland.setOnAction(this::openIslandOverview);
        Platform.runLater(() -> {
            inGameController.mapGrid.getChildren().add(homeIsland);
            waitForFxEvents();
        });
        waitForFxEvents();
    }

    public void openIslandOverview(ActionEvent actionEvent) {
        this.inGameController.showOverview();
    }

    private void openIslandOverview() {

        this.islandAttributeStorage.setIsland(testIsland);

        clickOn("#homeIsland");
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());

        // check Info
        assertEquals(this.inGameController.overviewSitesComponent.crewCapacity.getText(), String.valueOf(20));
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributeStorage.getIsland()) + islandAttributeStorage.getIsland().buildings().size();
        assertEquals(this.inGameController.overviewSitesComponent.resCapacity.getText(), usedSlots + "/" + islandAttributeStorage.getIsland().resourceCapacity());

        assertNotEquals("Geplünderte Insel(Kolonie)", this.inGameController.overviewSitesComponent.island_name.getText());
    }

    private void goToBuildings() {
        clickOn(inGameController.overviewSitesComponent.buildingsButton);
        waitForFxEvents();

        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
    }

    private void buildBuilding() {
        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.get(3));
        waitForFxEvents();

        assertTrue(this.inGameController.buildingsWindowComponent.isVisible());
        clickOn(this.inGameController.buildingsWindowComponent.buildingRefinery);
        waitForFxEvents();
        assertTrue(inGameController.buildingPropertiesComponent.isVisible());

        clickOn("#buyButton");
        waitForFxEvents();
        this.inGameController.buildingPropertiesComponent.setVisible(false);
    }

    private void destroyBuilding() {
        assertTrue(inGameController.buildingsWindowComponent.isVisible());
        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        clickOn(buildingNodes.getFirst());
        waitForFxEvents();
        assertTrue(inGameController.buildingPropertiesComponent.isVisible());
        clickOn("#destroyButton");
        waitForFxEvents();
        assertTrue(inGameController.deleteStructureComponent.isVisible());
        assertEquals(gameResourceBundle.getString("sure.you.want.delete") + " ",
                inGameController.deleteStructureComponent.deleteText.getText());
        assertEquals(gameResourceBundle.getString(buildingTranslation.get("refinery")),
                inGameController.deleteStructureComponent.warningText.getText());


        clickOn("#confirmButton");
        waitForFxEvents();
        inGameController.deleteStructureComponent.setVisible(false);
        inGameController.buildingPropertiesComponent.setVisible(false);
    }

    private void goToSites() {
        clickOn(this.inGameController.overviewSitesComponent.sitesButton);
        waitForFxEvents();

        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
    }

    private void destroySiteCell() {
        assertFalse(this.inGameController.sitePropertiesComponent.isVisible());
        clickOn("#energy");
        waitForFxEvents();

        // DO NOT DELETE THIS! SOMETIMES YO HAVE TO CLICK 2 TIMES ON DISTRICT TO OPEN THE WINDOW
        clickOn("#energy");
        waitForFxEvents();
        assertTrue(this.inGameController.sitePropertiesComponent.isVisible());

        clickOn("#buildSiteButton");
        waitForFxEvents();

        inGameController.buildingPropertiesComponent.setVisible(false);
    }

    private void buildSiteCell() {
        clickOn("#energy");
        waitForFxEvents();

        // DO NOT DELETE THIS! SOMETIMES YO HAVE TO CLICK 2 TIMES ON DISTRICT TO OPEN THE WINDOW
        clickOn("#energy");
        waitForFxEvents();
        assertTrue(this.inGameController.sitePropertiesComponent.isVisible());

        clickOn("#destroySiteButton");
        waitForFxEvents();
        assertTrue(this.inGameController.deleteStructureComponent.isVisible());
        assertEquals(gameResourceBundle.getString("sure.you.want.delete") + " ",
                inGameController.deleteStructureComponent.deleteText.getText());
        assertEquals(gameResourceBundle.getString(siteTranslation.get("energy")),
                inGameController.deleteStructureComponent.warningText.getText());

        clickOn("#confirmButton");
        waitForFxEvents();
        inGameController.deleteStructureComponent.setVisible(false);
        inGameController.buildingPropertiesComponent.setVisible(false);
    }

    private void goToUpgrade() {
        assertFalse(inGameController.overviewUpgradeComponent.isVisible());

        clickOn("#upgradeButton");
        waitForFxEvents();

        assertTrue(inGameController.overviewUpgradeComponent.isVisible());

        Node checkExplored = lookup("#checkExplored").query();
        Node checkColonized = lookup("#checkColonized").query();
        Node checkUpgraded = lookup("#checkUpgraded").query();
        Node checkDeveloped = lookup("#checkDeveloped").query();

        assertTrue(checkExplored.isVisible());
        assertTrue(checkColonized.isVisible());
        assertFalse(checkUpgraded.isVisible());
        assertFalse(checkDeveloped.isVisible());
    }

    private void upgradeIsland() {
        clickOn("#confirmUpgrade");
        waitForFxEvents();
    }

    private void closeIslandOverview() {
        // close Island Overview
        Node closeButton = lookup("#close").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewSitesComponent.isVisible());
    }

    private void checkStorageOverview() {
        // Storage is closed
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());

        // Open storage
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewComponent.isVisible());

        // Close storage with Button in Ingame
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());

        // Open again
        clickOn("#storageOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.storageOverviewComponent.isVisible());

        // Check Information
        for (Resource resource : this.inGameController.storageOverviewComponent.resourceListView.getItems()) {
            for (AggregateItemDto item : empireResources) {
                if (item.variable().equals(resource.resourceID())) {
                    assertEquals(item.count(), resource.count());
                    assertEquals(item.subtotal(), resource.changePerSeason());
                }
            }
        }

        // Close storage with button in StorageOverviewComponent
        clickOn("#closeStorageOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.storageOverviewComponent.isVisible());
    }

    private void checkEmpireOverview() {
        // Empire Overview is closed
        assertFalse(this.inGameController.empireOverviewComponent.isVisible());

        // Open Empire Overview
        clickOn("#empireOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.empireOverviewComponent.isVisible());

        // Close Empire Overview with Button in Ingame
        clickOn("#empireOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.empireOverviewComponent.isVisible());

        // Open again
        clickOn("#empireOverviewButton");
        waitForFxEvents();
        assertTrue(this.inGameController.empireOverviewComponent.isVisible());

        // Check Information
        assertEquals(this.inGameController.empireOverviewComponent.empireName, empireDto.name());
        assertEquals(this.inGameController.empireOverviewComponent.empireDescription, empireDto.description());
        assertEquals(this.inGameController.empireOverviewComponent.colour, empireDto.color());
        assertEquals(this.inGameController.empireOverviewComponent.flag, empireDto.flag());
        assertEquals(this.inGameController.empireOverviewComponent.portrait, empireDto.portrait());

        // Close Empire Overview with close button in component
        clickOn("#closeEmpireOverviewButton");
        waitForFxEvents();
        assertFalse(this.inGameController.empireOverviewComponent.isVisible());
    }
}
