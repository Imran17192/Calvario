package de.uniks.stp24.component.game;

import de.uniks.stp24.component.game.fleetManager.FleetManagerComponent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import javax.inject.Inject;
import java.util.Objects;

/**
 * Button node for visibility toggling of components in the context menu. <br>
 * Construct a new context menu button, declaring the button id and the node that needs to be controlled.
 * Every other child node of the parent class will be set invisible.
 */
public class ContextMenuButton extends Button {
    @Inject
    public ContextMenuButton(String buttonID, Node contextNode) {
        this.setPrefSize(30, 30);
        String id = buttonID + "Button";

        this.setId(id);
        this.getStyleClass().add(id);

        if (Objects.nonNull(contextNode)) {
            StackPane parent = (StackPane) contextNode.getParent();
            this.setOnAction(act -> {
                parent.getChildren().stream()
                        .filter(node -> !node.equals(contextNode))
                        .forEach(node -> node.setVisible(false));
                contextNode.setVisible(!contextNode.isVisible());
                if(buttonID.equals("fleetManager")){
                    FleetManagerComponent fleetManagerComponent = (FleetManagerComponent) contextNode;
                    fleetManagerComponent.showFleets();
                }
            });
        }
    }
}
