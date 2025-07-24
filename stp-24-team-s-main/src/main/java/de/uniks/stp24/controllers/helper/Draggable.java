package de.uniks.stp24.controllers.helper;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Draggable {
    public static final class DraggableNode implements EventHandler<MouseEvent> {
        private double lastMouseX = 0, lastMouseY = 0;

        private boolean dragging = false;

        private final Node eventNode;
        private final List<Node> dragNodes = new ArrayList<>();

        public DraggableNode(final Node node) {
            this(node, node);
        }

        public DraggableNode(final Node eventNode, final Node... dragNodes) {
            this.eventNode = eventNode;
            this.dragNodes.addAll(Arrays.asList(dragNodes));
            if (!this.dragNodes.contains(eventNode)) this.dragNodes.add(eventNode);
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        @Override
        public void handle(final MouseEvent event) {
            if (MouseEvent.MOUSE_PRESSED == event.getEventType()) {
                if (this.eventNode.contains(event.getX(), event.getY())) {
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();
                    event.consume();
                }
            } else if (MouseEvent.MOUSE_DRAGGED == event.getEventType()) {
                if (!this.dragging) {
                    this.dragging = true;
                }
                final double deltaX = event.getSceneX() - this.lastMouseX;
                final double deltaY = event.getSceneY() - this.lastMouseY;

                for (final Node dragNode : this.dragNodes) {
                    final double initialTranslateX = dragNode.getTranslateX();
                    final double initialTranslateY = dragNode.getTranslateY();
                    dragNode.setTranslateX(initialTranslateX + deltaX);
                    dragNode.setTranslateY(initialTranslateY + deltaY);
                }

                this.lastMouseX = event.getSceneX();
                this.lastMouseY = event.getSceneY();

                event.consume();
            } else if (MouseEvent.MOUSE_RELEASED == event.getEventType()) {
                if (this.dragging) {
                    event.consume();
                    this.dragging = false;
                }
            }
        }
    }
}