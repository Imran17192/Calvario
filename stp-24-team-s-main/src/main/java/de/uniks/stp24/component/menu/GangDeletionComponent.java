package de.uniks.stp24.component.menu;

import de.uniks.stp24.controllers.GangCreationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "WarningGangDeletion.fxml")
public class GangDeletionComponent extends VBox {

    @FXML
    Label gameName;
    @FXML
    Button deleteGangButton;
    @FXML
    Button cancelDeleteButton;
    @FXML
    Text warningText;
    @FXML
    VBox warningContainer;

    @Inject
    @Resource
    ResourceBundle resources;


    GangCreationController gangCreationController;


    @Inject
    public GangDeletionComponent() {

    }

    public void setGangCreationController(GangCreationController gangCreationController){
        this.gangCreationController = gangCreationController;
    }

    public void setWarningText(String deletedGang){
        this.gameName.setText(deletedGang);
    }

    public void cancelDelete() {
        getParent().setVisible(false);
    }

    public void deleteGang(){
            gangCreationController.delete();
            getParent().setVisible(false);
    }
}
