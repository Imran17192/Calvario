package de.uniks.stp24;

import de.uniks.stp24.appTestModules.AppTest3Module;
import de.uniks.stp24.model.Trait;
import de.uniks.stp24.ws.Event;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AppTest3 extends AppTest3Module {
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        when(this.gameLogicApiService.getAggregate(any(),any(),any())).thenReturn(Observable.just(HEALTH_DEF_DTO));
        this.app.show(this.lobbyController);
    }

    @Test
    public void v3() {
        this.selectTraits();
        this.loadGame();

        this.showTip();
        this.performMarketTrades();
        this.performSeasonalTrades();
        this.beginSiteJob();
        this.beginBuildingJob();
        this.beginIslandUpgrade();
        this.inspectJobs();
        this.cancelJob();
        this.beginTechnologyJob();
        this.showHelpWindow();
    }

    public void selectTraits() {
        clickOn("#selectEmpireButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#editButton");
        WaitForAsyncUtils.waitForFxEvents();

        clickOn("#chooseTraitsButton");
        WaitForAsyncUtils.waitForFxEvents();

        ListView<Trait> traitListView = lookup("#selectedTraitsListView").queryListView();

        clickOn("#preparedButtonChoose");
        assertEquals(1, traitListView.getItems().size());
        clickOn("#unpreparedButtonChoose");
        assertEquals(1, traitListView.getItems().size());

        clickOn("#preparedButtonUnChoose");
        assertEquals(0, traitListView.getItems().size());
        clickOn("#__dev__ButtonChoose");
        assertEquals(1, traitListView.getItems().size());

        clickOn("#traitsConfirmButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertEquals(1, lookup("#confirmedTraitsListView").queryListView().getItems().size());

        clickOn("#confirmButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#selectButton");
    }

    public void loadGame() {
        WaitForAsyncUtils.waitForFxEvents();
        MEMBER_DTO_SUBJECT.onNext(new Event<>("games." + GAME_ID + ".members.*.updated", MEMBER_DTO2));
        clickOn("#startJourneyButton");
        WaitForAsyncUtils.waitForFxEvents();
        sleep(2000);
    }

    public void beginSiteJob() {
        clickOn("#ingameFleet_testFleetID_1");
        clickOn("#jobsOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.jobsOverviewComponent.isVisible());
        clickOn("#islandID_1_instance");
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.overviewSitesComponent.isVisible());
        clickOn("#sitesButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#energy");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildSiteButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[3] + "created", JOBS[3]));
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(this.jobsService.getObservableListForSystem("islandID_1").isEmpty());
    }

    public void beginBuildingJob() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildingsButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#empty_building_element");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buildingRefinery");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buyButton");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[2] + "created", JOBS[2]));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.propertiesJobProgressComponent.isVisible());
    }

    public void beginIslandUpgrade() {
        clickOn("#upgradeButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#confirmUpgrade");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#backButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeButton");
        assertTrue(this.islandUpgradesJobProgressComponent.isVisible());
    }

    public void inspectJobs() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobElementInspectionButton_jobSiteID");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobProgressInspectionButton_jobSiteID");
    }

    public void cancelJob() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#jobElementDeleteButton_jobSiteID");
        JOB_SUBJECT.onNext(new Event<>(JOB_EVENT_PATHS[3] + "deleted", JOBS[3]));
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeOverviewButton");
    }

    public void performMarketTrades() {
        marketComponent.updateInformation();
        marketComponent.userCredits = 1000000000;
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#marketOverviewButton");
        assertTrue(this.marketComponent.isVisible());
        clickOn("#minerals_marketButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#incrementNumberOfGoods");
        clickOn("#incrementNumberOfGoods");
        clickOn("#incrementNumberOfGoods");
        clickOn("#buyButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#incrementNumberOfGoods");
        clickOn("#incrementNumberOfGoods");
        clickOn("#incrementNumberOfGoods");
        clickOn("#sellButton");
        WaitForAsyncUtils.waitForFxEvents();
    }

    public void performSeasonalTrades() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#everySeasonButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#buyButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(lookup("#seasonalTradesListView").queryListView().getItems().isEmpty());
        clickOn("#playControlsButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#cancelTradesButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(lookup("#seasonalTradesListView").queryListView().getItems().isEmpty());
        clickOn("#closeMarketOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(this.marketComponent.isVisible());
    }

    public void beginTechnologyJob() {
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#storageOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeStorageOverviewButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#technologiesButton");
        assertTrue(this.technologyOverviewComponent.isVisible());
        clickOn("#crewRelationsButton");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#closeCategoryButton");
    }

    public void showTip() {
        GAME_SUBJECT.onNext(this.tickGame(0));
        WaitForAsyncUtils.waitForFxEvents();
        assertTrue(this.coolerBubbleComponent.isVisible());
        clickOn("#nextButton");
        WaitForAsyncUtils.waitForFxEvents();
        assertFalse(this.coolerBubbleComponent.isVisible());
    }

    public void showHelpWindow() {
        press(KeyCode.ESCAPE);
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("#settingsButton");
        sleep(2000);
        WaitForAsyncUtils.waitForFxEvents();
//        assertTrue(this.helpComponent.isVisible());
//        clickOn("#closeHelpButton");
        sleep(2000);
        WaitForAsyncUtils.waitForFxEvents();
//        assertFalse(this.helpComponent.isVisible());

    }
}
