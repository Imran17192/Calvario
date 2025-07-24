package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.service.menu.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.annotation.param.Param;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Title("%login")
@Controller
public class LoginController extends BasicController {
    @FXML
    Pane captainContainer;
    @FXML
    Button licensesButton;
    @FXML
    ToggleButton showPasswordToggleButton;
    @FXML
    Text errorLabel;
    @FXML
    CheckBox rememberMeBox;
    @FXML
    Button loginButton;
    @FXML
    Button signupButton;
    @FXML
    PasswordField passwordInput;
    @FXML
    TextField usernameInput;
    @FXML
    TextField showPasswordText;
    @FXML
    ToggleButton enToggleButton;
    @FXML
    ToggleButton deToggleButton;

    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;

    @Inject
    Subscriber subscriber;
    @Inject
    public LoginService loginService;
    @SubComponent
    @Inject
    BubbleComponent bubbleComponent;

    @Param("info")
    public String info;
    @Param("username")
    public String username;
    @Param("password")
    public String password;

    @Inject
    public LoginController() {
    }

    @OnInit
    public void init() {
        this.controlResponses = responseConstants.respLogin;
    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
        bubbleComponent.setCaptainText(resources.getString("pirate.login.welcome"));
    }

    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username)) {
            this.usernameInput.setText(this.username);
        }
        if (Objects.nonNull(this.password)) {
            this.passwordInput.setText(this.password);
        }
        if (Objects.nonNull(this.info)) {
            switch(this.info) {
                case "normalLogin" -> this.bubbleComponent.setCaptainText(resources.getString("pirate.login.welcome"));
                case "autologinFailed", "deleted" -> this.bubbleComponent.setCaptainText(resources.getString("pirate.account.deleted"));
                case "logout" -> this.bubbleComponent.setCaptainText(resources.getString("logout.successful.on.this.device"));
                case "registered" -> this.bubbleComponent.setCaptainText(resources.getString("account.registered"));
                case "error" -> this.bubbleComponent.setCaptainText("ONERROR");

                default -> this.bubbleComponent.setCaptainText("");
            }
        }
        if(prefService.getLocale() == Locale.ENGLISH) {
            enToggleButton.setSelected(true);
        } else {
            deToggleButton.setSelected(true);
        }
    }

    public void login() {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        if (checkIt(username,password)) {
            boolean rememberMe = this.rememberMeBox.isSelected();

            loginButton.setDisable(true);
            signupButton.setDisable(true);
            bubbleComponent.setErrorMode(false);
            this.bubbleComponent.setCaptainText(getErrorInfoText(201));

            subscriber.subscribe(loginService.login(username, password, rememberMe),
                    result -> app.show("/browseGames")
                    // in case of server's response => error
                    // handle with error response
                    , error -> {
                        bubbleComponent.setErrorMode(true);
                        this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                        enableButtons();
                    });
        } else {
            // 1 is used for default in switch
            bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(-1));
            enableButtons();
        }
    }

    public void signup() {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        app.show("/signup", Map.of("username", username, "password", password));
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
        enToggleButton.setSelected(true);
        deToggleButton.setSelected(false);
    }

    @FXML
    public void setDe() {
        setLanguage(Locale.GERMAN);
        enToggleButton.setSelected(false);
        deToggleButton.setSelected(true);
    }

    public void setLanguage(Locale locale) {
        String username = this.usernameInput.getText();
        String password = this.passwordInput.getText();
        resources = languageService.setLocale(locale);
        app.refresh();
        this.usernameInput.setText(username);
        this.passwordInput.setText(password);
    }

    @OnRender(1)
    public void setupShowPassword() {
        // TextField showPasswordText is per default not managed

        // setting properties managed and visible to change depending on
        // showPasswordToggleButton state
        showPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        passwordInput.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        passwordInput.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showPasswordText.textProperty().bindBidirectional(passwordInput.textProperty());
    }

    public void showLicenses() {
        app.show("/licenses");
    }

    public void enableButtons(){
        loginButton.setDisable(false);
        signupButton.setDisable(false);
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }
}
