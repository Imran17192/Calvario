package de.uniks.stp24.component.menu;


import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.stp24.App;
import de.uniks.stp24.service.menu.BrowseGameService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.ResourceBundle;


@Component(view = "Logout.fxml")
public class LogoutComponent extends VBox {
    @FXML
    Text warningText;
    @FXML
    Button cancelButton;
    @FXML
    Button logoutButton;
    @FXML
    VBox logoutWindow;

    @Inject
    App app;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    ObjectMapper objectMapper;

    @Inject
    Subscriber subscriber;
    @Inject
    @Resource
    ResourceBundle resources;
    private Pane parent;

    @Inject
    public LogoutComponent(){

    }

    @OnRender
    public void render() {
        logoutWindow.setStyle("-fx-background-color: transparent;");
        warningText.setText(resources.getString("you.will.be.logged.out"));
    }

    @OnKey(code = KeyCode.ENTER)
    public void logout() {
        if (this.getParent() != null && this.getParent().isVisible()) {
            this.subscriber.subscribe(browseGameService.logout(""));
            this.getParent().setVisible(false);
            app.show("/login");
        }
    }

    public void setParent(Pane parent) {
        this.parent = parent;
    }

    public void cancel() {
        this.getParent().setVisible(false);
        this.parent.setVisible(false);
    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            this.subscriber.dispose();
        }
    }
}
