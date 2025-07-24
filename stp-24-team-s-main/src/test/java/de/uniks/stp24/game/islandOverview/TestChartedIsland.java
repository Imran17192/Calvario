package de.uniks.stp24.game.islandOverview;

import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestChartedIsland extends IslandOverviewTestComponent{
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.getScene().getStylesheets().clear();
        initComponents();
        inGameController.rootPane.getStylesheets().clear();
        inGameController.overviewSitesComponent.imagePane.getStylesheets().clear();
        inGameController.overviewSitesComponent.islandFlag.getStylesheets().clear();
        inGameController.showOverview();
    }


    @Test
    public void testOwnedIsland() {
        doReturn(readEmpireDto).when(islandsService).getEmpire(any());
        islandAttributeStorage.setIsland(testIsland);

        //Open island overview of owned Island
        waitForFxEvents();
        assertTrue(this.inGameController.overviewContainer.isVisible());
        waitForFxEvents();
        assertEquals(this.inGameController.overviewSitesComponent.crewCapacity.getText(), String.valueOf(20));
        waitForFxEvents();
        int usedSlots = sitesComponent.getTotalSiteSlots(islandAttributeStorage.getIsland()) +
                islandAttributeStorage.getIsland().buildings().size();
        assertEquals(this.inGameController.overviewSitesComponent.resCapacity.getText(), usedSlots + "/" + islandAttributeStorage.getIsland().resourceCapacity());
        waitForFxEvents();
        assertEquals(this.inGameController.overviewSitesComponent.island_name.getText(), "Plundered Island(Colony)");

        //Test function of buttons
        //"Buildings" selected
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        Node prev = lookup("#prev").query();
        Node next = lookup("#next").query();

        //-> Check if building nodes are visible
        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));
        for (int i = 0; i < buildingNodes.size() - 2; i++) {
            clickOn(buildingNodes.get(i));
            assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), buildingNodes.size());
            assertTrue(!prev.isVisible() && !next.isVisible());
        }

        //"Details" selected
        clickOn(this.inGameController.overviewSitesComponent.detailsButton);
        assertTrue(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        Text amount1 = lookup("#amount1").query();
        Text amount2 = lookup("#amount2").query();
        Node showCons = lookup("#showConsumption").query();

        assertEquals("+195.0", amount1.getText());
        assertEquals("+199.0", amount2.getText());
        clickOn(showCons);
        waitForFxEvents();
        assertEquals("-265.0", amount1.getText());
        assertEquals("-275.0", amount2.getText());

        //"Sites" selected
        clickOn(this.inGameController.overviewSitesComponent.sitesButton);
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.jobsButton.isDisable());
        waitForFxEvents();

        //"Jobs" selected. Enough Resources
        clickOn(this.inGameController.overviewSitesComponent.jobsButton);
        assertFalse(this.inGameController.overviewSitesComponent.detailsButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.sitesButton.isDisable());
        assertFalse(this.inGameController.overviewSitesComponent.buildingsButton.isDisable());
        assertTrue(this.inGameController.overviewSitesComponent.jobsButton.isDisable());

        //"Upgrades" selected. Enough Resources
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node checkExplored = lookup("#checkExplored").query();
        Node checkColonized = lookup("#checkColonized").query();
        Node checkUpgraded = lookup("#checkUpgraded").query();
        Node checkDeveloped = lookup("#checkDeveloped").query();

        assertTrue(checkExplored.isVisible());
        assertTrue(checkColonized.isVisible());
        assertFalse(checkUpgraded.isVisible());
        assertFalse(checkDeveloped.isVisible());
        waitForFxEvents();
    }

    @Test
    public void testEnemiesIsland() {
        assertTrue(this.inGameController.overviewContainer.isVisible());
        Node upgradeButton = lookup("#upgradeButton").query();

        assertTrue(upgradeButton.isVisible());

        Node prev = lookup("#prev").query();
        Node next = lookup("#next").query();

        ArrayList<Node> buildingNodes = new ArrayList<>(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building"));

        int oldValue = this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size();
        clickOn(buildingNodes.getLast());

        assertEquals(this.inGameController.overviewSitesComponent.buildingsComponent.buildings.lookupAll("#building").size(), oldValue);
        assertTrue(!prev.isVisible() && !next.isVisible());
    }
}
