package de.uniks.stp24.component.menu;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.ErrorService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.EditAccService;
import de.uniks.stp24.utils.ResponseConstants;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "WarningScreen.fxml")
public class WarningScreenComponent extends VBox {
    @FXML
    public Button deleteAccButton;
    @FXML
    public Button cancelDeleteButton;
    @FXML
    public Text warningText;
    @FXML
    public Label userName;
    @FXML
    VBox warningContainer;
    @Inject
    App app;
    @Inject
    EditAccService editAccService;
    @Inject
    ObjectMapper objectMapper;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    ErrorService errorService;
    @Inject
    ResponseConstants responseConstants;
    @Inject
    @Resource
    ResourceBundle resources;

    @Inject
    public WarningScreenComponent() {
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void cancelDelete() {
        if (getParent() != null)
            getParent().setVisible(false);
    }

    public void deleteAcc() {
        // Delete user and switch back to the login screen
        this.subscriber.subscribe(editAccService.deleteUser(),
            result -> app.show("/login",
                        Map.of("info","deleted")),
            error -> {
                int code = errorService.getStatus(error);
                warningText.setText(responseConstants.resStdText.getOrDefault(code,"no.entry"));
        });
    }

    @OnDestroy
    public void destroy() {
        if(subscriber != null) {
            this.subscriber.dispose();
        }
    }

    public void setWarning(String text) {
        this.warningText.setText(text);
    }

    public void setUserName(String name) {
        this.userName.setText(name);
    }
}
