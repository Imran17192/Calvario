package de.uniks.stp24.game.islandOverview;

import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

@ExtendWith(MockitoExtension.class)
public class TestButtons extends IslandOverviewTestComponent{
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
    public void closeUpgrade() throws Exception {

        islandAttributeStorage.setIsland(testIsland);
        waitForFxEvents();
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node closeButton = lookup("#close").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void closeOverview() {
        waitForFxEvents();
        Node closeButton = lookup("#closeOverviewButton").query();
        clickOn(closeButton);
        waitForFxEvents();
        assertFalse(inGameController.overviewContainer.isVisible());
    }

    @Test
    public void goBackFromUpgrades() {
        islandAttributeStorage.setIsland(testIsland);

        waitForFxEvents();
        Node upgradeButton = lookup("#upgradeButton").query();
        clickOn(upgradeButton);
        waitForFxEvents();
        Node backButton = lookup("#backButton").query();
        clickOn(backButton);
        waitForFxEvents();
        assertTrue(inGameController.overviewContainer.isVisible());
    }
}
