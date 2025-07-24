package de.uniks.stp24.service.game;

import de.uniks.stp24.App;
import de.uniks.stp24.component.game.CustomComponentListCell;
import de.uniks.stp24.component.game.ResourceComponent;
import de.uniks.stp24.component.game.VariableExplanationComponent;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.model.Sources;
import de.uniks.stp24.service.IslandAttributeStorage;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static de.uniks.stp24.service.Constants.*;

@Singleton
public class ExplanationService {
    @Inject
    public App app;
    @Inject
    public VariableService variableService;
    @Inject
    IslandAttributeStorage islandAttributes;
    @Inject
    EventService eventService;
    @Inject
    @org.fulib.fx.annotation.controller.Resource
    ResourceBundle langBundle;
    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;
    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    private InGameController inGameController;
    Map<String, Double> activeEffects = new HashMap<>();

    @Inject
    public ExplanationService() {

    }

    /*
    Methods below is made for explanation of resources.
     */
    public CustomComponentListCell<Resource, ResourceComponent> addMouseHoverListener(CustomComponentListCell<Resource, ResourceComponent> cell, String listTyp, String indicator, String resourceCategory) {
        VariableExplanationComponent variableExplanationComponent = new VariableExplanationComponent();

        Tooltip tooltip = new Tooltip();
        Tooltip.install(cell, tooltip);
        tooltip.setGraphic(variableExplanationComponent);
        tooltip.setStyle(
                "-fx-background-color: transparent; " +
                        "-fx-background-insets: 0; " +
                        "-fx-background-radius: 0; " +
                        "-fx-border-color: transparent; " +
                        "-fx-padding: 0;"
        );
        AtomicBoolean entered = new AtomicBoolean(false);

        app.stage().getScene().addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            if (cell.isEmpty() || cell.getItem() == null) {
                tooltip.hide();
                return;
            }

            double mouseX = event.getScreenX();
            double mouseY = event.getScreenY();

            Point2D cellStart = cell.localToScreen(0, 0);
            Point2D cellEnd = cell.localToScreen(cell.getWidth() * 2 / 3, cell.getHeight());

            boolean isMouseInsideCell = false;
            if (cellEnd != null) {
                isMouseInsideCell = mouseX < cellEnd.getX() && mouseX > cellStart.getX() && mouseY > cellStart.getY() + cell.getHeight() * 2 / 5 && mouseY < cellEnd.getY() - cell.getHeight() * 2 / 5;
            }

            if(inGameController.overviewUpgradeComponent.isVisible() || inGameController.buildingPropertiesComponent.isVisible() || inGameController.sitePropertiesComponent.isVisible()){
                if (isMouseInsideCell && !entered.get()) {
                    initializeResExplanation(listTyp, indicator, resourceCategory, cell.getItem().resourceID(), variableExplanationComponent);
                    tooltip.show(app.stage(), mouseX, mouseY);
                    entered.set(true);

                } else if (!isMouseInsideCell) {
                    tooltip.hide();
                    entered.set(false);
                }
            }
        });
        return cell;
    }

    private void initializeResExplanation(String listType, String indicator, String resCategory, String id, VariableExplanationComponent variableExplanationComponent) {
        String variable = listType + "." + indicator + "." + resCategory + "." + id;
        ExplainedVariableDTO explanation = setVariableExplanationComponent(variable, resCategory, id, variableExplanationComponent);
        this.activeEffects.clear();

        /*
        Iterate over all active effect of current variable and show the effects visually.
         */
        for (Sources source : explanation.sources()) {
            for (Effect effect : source.effects())
                if (effect.variable().equals(variable)) activeEffects.put(source.id(), (effect.multiplier() - 1) * 100);
        }

        List<String> effects = new ArrayList<>();

        for(Map.Entry<String, Double> entry : activeEffects.entrySet()){
            double mult = entry.getValue();
            BigDecimal roundedMult = new BigDecimal(mult).setScale(2, RoundingMode.HALF_UP);
            String effect = entry.getKey();
            String effectText;
            if (eventService.eventNames.contains(effect)) {
                effectText = gameResourceBundle.getString("event." + effect + ".name");
            } else {
                effectText = variablesResourceBundle.getString(effect);
            }
            effects.add(roundedMult + "% " + effectText);
        }

        variableExplanationComponent.fillListWithEffects(effects);
    }

    private ExplainedVariableDTO setVariableExplanationComponent(String variable, String resCategory, String id, VariableExplanationComponent variableExplanationComponent){
        ExplainedVariableDTO explanation = variableService.data.get(variable);
        String title = gameResourceBundle.getString(resourceTranslation.get(id)) + " " + gameResourceBundle.getString(economyProcess.get(resCategory));
        String base;
        String total;

        if(resCategory.equals("production")){
            base = gameResourceBundle.getString(economyProcess.get("base")) + ": +" + explanation.initial();
            total = gameResourceBundle.getString(economyProcess.get("total")) + ": +" + Math.floor(explanation.finalValue());
        } else {
            base = gameResourceBundle.getString(economyProcess.get("base")) + ": -" + explanation.initial();
            total = gameResourceBundle.getString(economyProcess.get("total")) + ": -" + Math.floor(explanation.finalValue());
        }

        variableExplanationComponent.setValues(base, total, title, resourceImagePath.get(id));

        return explanation;
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }
}
