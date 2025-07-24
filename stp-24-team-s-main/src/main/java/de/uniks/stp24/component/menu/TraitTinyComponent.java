package de.uniks.stp24.component.menu;

import de.uniks.stp24.controllers.GangCreationController;
import org.fulib.fx.annotation.controller.Component;

import java.util.ResourceBundle;

@Component(view = "TraitTiny.fxml")
public class TraitTinyComponent extends TraitComponent {
    public TraitTinyComponent(GangCreationController gangCreationController, ResourceBundle variablesResourceBundle, Boolean showChoose, Boolean showRemove) {
        super(gangCreationController, variablesResourceBundle, showChoose, showRemove);
    }
}
