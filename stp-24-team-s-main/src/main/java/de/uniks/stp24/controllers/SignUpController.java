package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.rest.UserApiService;
import de.uniks.stp24.service.menu.SignUpService;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
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

import javax.inject.Inject;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Title("%register")
@Controller
public class SignUpController extends BasicController {

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;
    @FXML
    Text errorTextField;
    @FXML
    Pane captainContainer;
    @FXML
    public Button registerButton;
    @FXML
    TextField showPasswordText;
    @FXML
    TextField showRepeatPasswordText;
    @FXML
    ToggleButton showPasswordToggleButton;
    @FXML
    ToggleButton enToggleButton;
    @FXML
    ToggleButton deToggleButton;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;


    @Param("username")
    public String username;
    @Param("password")
    public String password;
    private Text mirrorText;
    private String lastMsg;

    @Inject
    SignUpService signUpService;
    @Inject
    UserApiService userApiService;
    @SubComponent
    @Inject
    public BubbleComponent bubbleComponent;

    private BooleanBinding isLoginFieldEmpty;
    private BooleanBinding isPasswordFieldEmpty;
    private BooleanBinding isRepeatPasswordEmpty;
    private BooleanBinding passwordInputsMatch;
    private BooleanBinding isPasswordTooShort;
    private BooleanBinding mirrorChangeText;

    @Inject
    public SignUpController() {}

    @OnInit
    public void init(){
        this.controlResponses = responseConstants.respSignup;
        lastMsg = "";
        this.mirrorText = new Text(lastMsg);
        mirrorText.setVisible(false);
    }

    @OnRender
    public void addSpeechBubble() {
        captainContainer.getChildren().add(bubbleComponent);
    }

    // Sets boolean bindings for text manipulations

    @OnRender
    public void createBindings() {
       this.isLoginFieldEmpty =  this.usernameField.textProperty().isEmpty();
       this.isPasswordFieldEmpty = this.passwordField.textProperty().isEmpty();
       this.isRepeatPasswordEmpty = this.repeatPasswordField.textProperty().isEmpty();
       this.isPasswordTooShort = this.passwordField.lengthProperty().lessThan(8);
       this.passwordInputsMatch = Bindings.equal(this.passwordField.textProperty(),
               this.repeatPasswordField.textProperty());
       this.mirrorChangeText = Bindings.equal(lastMsg, mirrorText.textProperty());
    }

    // Gets inputs from login screen to the signup screen fields
    @OnRender
    public void applyInputs() {
        if (Objects.nonNull(this.username))
            this.usernameField.setText(this.username);
        if (Objects.nonNull(this.password))
            this.passwordField.setText(this.password);
        if(prefService.getLocale() == Locale.ENGLISH) {
            enToggleButton.setSelected(true);
        } else {
            deToggleButton.setSelected(true);
        }
    }

    // Shows an error message when input fields are empty or password inputs do not match
    @OnRender
    public void showErrorMessage() {
        this.errorTextField.textProperty().bind(Bindings.createStringBinding(() -> {
            this.bubbleComponent.captainText.setText("");
            this.bubbleComponent.setErrorMode(true);
            if (this.isLoginFieldEmpty.get())
                this.bubbleComponent.setCaptainText(resources.getString("pirate.register.tell.name"));
            else if (this.isPasswordFieldEmpty.get())
                this.bubbleComponent.setCaptainText(resources.getString("pirate.general.enter.password"));
            else if (this.isPasswordTooShort.get() && !this.isPasswordFieldEmpty.get())
                this.bubbleComponent.setCaptainText(resources.getString("pirate.register.8characters"));
            else if (this.isRepeatPasswordEmpty.get() && !this.isPasswordFieldEmpty.get())
                this.bubbleComponent.setCaptainText(resources.getString("pirate.general.repeat.password"));
            else if (!this.passwordInputsMatch.get() && !this.isPasswordFieldEmpty.get() && !this.isPasswordFieldEmpty.get())
                this.bubbleComponent.setCaptainText(resources.getString("pirate.register.passwords.dont.match"));
            else {
                this.bubbleComponent.setErrorMode(false);
                this.bubbleComponent.setCaptainText(resources.getString("pirate.register.possible"));
            }

            return "";
          }, this.isLoginFieldEmpty, this.isPasswordFieldEmpty,
                this.isRepeatPasswordEmpty, this.passwordInputsMatch,
                this.isPasswordTooShort, this.mirrorChangeText));
    }

    // Disables register button when input fields are empty or password inputs do not match
    // modified passwords do not need to match
    @OnRender
    public void disableRegisterButton() {
        this.registerButton.disableProperty()
                .bind(this.isLoginFieldEmpty
                        .or(this.isPasswordFieldEmpty)
                        .or(this.isRepeatPasswordEmpty)
                        .or(this.isPasswordTooShort)
                );
    }

    public void register() {
        if (checkIt(this.usernameField.getText(), this.passwordField.getText(),
                this.repeatPasswordField.getText()) && this.passwordInputsMatch.getValue()) {

            bubbleComponent.setErrorMode(false);
            this.bubbleComponent.setCaptainText(getErrorInfoText(201));

            this.subscriber.subscribe(this.signUpService.register(this.getUsername(), this.getPassword()),
                    result -> this.app.show("/login",
                            Map.of(
                                    "username", this.getUsername(),
                                    "password", this.getPassword(),
                                    "info", "registered"
                            )),
                    error -> {
                        bubbleComponent.setErrorMode(true);
                        this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                    });
            } else {
            bubbleComponent.setErrorMode(true);
            this.bubbleComponent
              .setCaptainText(getErrorInfoText(this.passwordInputsMatch.not().getValue() ? -2 : -1));
        }
    }

    // Returns user to the login screen
    public void goBack() {
        app.show("/login",
                Map.of("username", this.getUsername(),
                        "password", this.getPassword()
        ));
    }

    @OnRender(1)
    public void setupShowPassword() {
        // TextField showPasswordText is per default not managed
        // setting properties managed and visible to change depending on
        // showPasswordToggleButton state

        //passwordField
        showPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        passwordField.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        passwordField.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showPasswordText.textProperty().bindBidirectional(passwordField.textProperty());

        //repeatPasswordField
        showRepeatPasswordText.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        showRepeatPasswordText.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty());
        repeatPasswordField.managedProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());
        repeatPasswordField.visibleProperty()
                .bind(showPasswordToggleButton.selectedProperty().not());

        // binding textValue from both fields
        showRepeatPasswordText.textProperty().bindBidirectional(repeatPasswordField.textProperty());

    }

    private String getUsername() {
        return this.usernameField.getText();
    }

    private String getPassword() {
        return this.passwordField.getText();
    }

    @OnDestroy
    public void destroy() {
        this.subscriber.dispose();
        backgroundAnchorPane.setStyle("-fx-background-image: null");
        cardBackgroundVBox.setStyle("-fx-background-image: null");
    }

    @FXML
    public void setEn() {
        setLanguage(Locale.ENGLISH);
        enToggleButton.setSelected(true);
        deToggleButton.setSelected(false);
    }

    public void setDe() {
        setLanguage(Locale.GERMAN);
        enToggleButton.setSelected(false);
        deToggleButton.setSelected(true);
    }

    public void setLanguage(Locale locale) {
        String password = this.passwordField.getText();
        String repeatPassword = this.repeatPasswordField.getText();
        resources = languageService.setLocale(locale);
        app.refresh();
        this.repeatPasswordField.setText(repeatPassword);
        this.passwordField.setText(password);
    }
}
