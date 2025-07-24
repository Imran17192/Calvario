package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.IslandsService;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import java.util.Random;

@Title("CALVARIO")
@Controller
public class LoadingScreenController extends BasicController {

    @FXML
    AnchorPane backGround;
    @FXML
    Pane captainContainer;

    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    @Inject
    public EmpireService empireService;

    @Inject
    public IslandsService islandsService;

    @Inject
    TokenStorage tokenStorage;

    Random random = new Random();

    @Inject
    public LoadingScreenController() {

    }

    @OnRender
    public void render() {
        captainContainer.getChildren().add(bubbleComponent);
        bubbleComponent.setCaptainText("LOADING ...");
        int rand = random.nextInt(1, 8);
        String backgroundImage = "-fx-background-image: url('[PATH]');"
                .replace("[PATH]", "/de/uniks/stp24/assets/backgrounds/loading/l" + rand + ".jpg");
        backGround.setStyle(backgroundImage + "-fx-background-size: cover;");
    }
}
