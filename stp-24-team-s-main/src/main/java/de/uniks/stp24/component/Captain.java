package de.uniks.stp24.component;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;

import static java.lang.Math.ceil;

public class Captain extends Pane {
    @FXML
    public TextArea captainText;
    @FXML
    ImageView captainLogo;
    @FXML
    VBox backgroundBubble;

    @Inject
    public Subscriber subscriber;

    double bubbleWidth;
    double textWidth;
    double textHeight;

    ScaleTransition scaleTransition;


    @OnRender
    public void initScaleTransition() {
        scaleTransition = new ScaleTransition(Duration.seconds(0.3), captainLogo);
        bubbleWidth = backgroundBubble.getPrefWidth();
        textWidth = captainText.getPrefWidth();
        textHeight = captainText.getPrefHeight();
    }

    public void rotateCaptain() {
        captainLogo.setRotate(-30);
    }

    public void setCaptainText(String text) {
        captainText.setText(text);
        double calculated = ceil((double) text.length() /22)*37;
        double newTextWidth = textWidth, newTextHeight = textHeight, newBubbleWidth = bubbleWidth;
        double bubbleOffset, textOffset;

        switch ((int) calculated/37) {
            case 0 -> {
                bubbleOffset = 100;
                textOffset = 0;
            }
            case 1 -> {
                bubbleOffset = 70;
                textOffset = 0;
            }
            case 2 -> {
                bubbleOffset = 40;
                textOffset = 12;
            }
            case 3 -> {
                bubbleOffset = 10;
                textOffset = 20;
            }
            case 5 -> {
                calculated = 3*37+7;
                bubbleOffset = 0;
                textOffset = 20;
                newTextWidth *= 1.3;
                newTextHeight *= 1.3;
                newBubbleWidth *= 1.3;
            }
            default -> {
                calculated = 3*37+7;
                bubbleOffset = 0;
                textOffset = 20;
                newTextWidth *= 1.5;
                newTextHeight *= 1.5;
                newBubbleWidth *= 1.3;
            }
        }

        this.captainText.setPrefHeight(newTextHeight);
        this.captainText.setPrefWidth(newTextWidth);
        this.backgroundBubble.setPrefWidth(newBubbleWidth);
        this.backgroundBubble.setPrefHeight(calculated);
        this.backgroundBubble.setLayoutY(bubbleOffset);
        this.captainText.setLayoutY(bubbleOffset+textOffset);
    }

    public void setErrorMode(boolean isError) {
        if (isError) {
            captainText.setStyle("-fx-text-fill: RED");
        }
        else {
            captainText.setStyle("-fx-text-fill: BLACK");
        }
    }

    public void scaleUp() {
        scaleTransition.stop();
        scaleTransition.setToX(1.3);
        scaleTransition.setToY(1.3);
        scaleTransition.play();
    }

    public void scaleDown() {
        scaleTransition.stop();
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }

    @OnDestroy
    public void destroy() {
        subscriber.dispose();
        captainLogo.setImage(null);
    }
}
