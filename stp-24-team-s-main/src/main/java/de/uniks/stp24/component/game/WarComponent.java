package de.uniks.stp24.component.game;
import de.uniks.stp24.service.game.ContactsService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.fulib.fx.annotation.controller.Component;

import javax.inject.Inject;

@Component(view = "War.fxml")
public class WarComponent extends VBox {
    @FXML
    Button closeButton;
    @FXML
    TextFlow warText;
    @FXML
    Text warText1;
    @FXML
    Text warText2;
    @FXML
    Text warText3;

    @Inject
    ContactsService contactsService;


    private ContactDetailsComponent contactDetailsComponent;
    private StackPane parent;

    @Inject
    public WarComponent() {

    }

    public void close() {
        parent.setVisible(false);
        setVisible(false);
    }

    public void showWarMessage(String msg, String attacker) {
        warText1.setText("The ");
        warText2.setText(attacker);
        warText3.setText(" have" + (msg.equals("created") ? " started" : " stopped") + " the war against you");
        parent.setVisible(true);
        setVisible(true);
    }

    public void setParent(StackPane parent) {
        this.parent = parent;
        this.parent.setVisible(true);
        parent.getChildren().add(this);
    }

}
