package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("%licenses")
@Controller
public class LicensesController extends BasicController {

    @FXML
    Button backToLoginButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @FXML
    Pane captainContainer;

    @SubComponent
    @Inject
    BubbleComponent bubbleComponent;

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        bubbleComponent
          .setCaptainText(resources.getString("pirate.licenses"));
    }

    @Inject
    public LicensesController() {
    }

    public void backToLogin() {
        app.show("/login");
    }

    @OnDestroy
    public void destroy(){

    }
}
