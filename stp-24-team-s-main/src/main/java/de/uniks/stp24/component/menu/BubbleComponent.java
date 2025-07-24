package de.uniks.stp24.component.menu;

import de.uniks.stp24.component.Captain;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "CaptainBubble.fxml")
public class BubbleComponent extends Captain {
    @Inject
    public BubbleComponent() {

    }
}