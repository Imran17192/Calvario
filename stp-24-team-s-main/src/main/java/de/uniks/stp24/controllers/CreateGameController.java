package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.service.menu.CreateGameService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Controller;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.controller.Title;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("%create.game")
@Controller
public class CreateGameController extends BasicController {
    @FXML
    TextField maxMembersTextField;
    @FXML
    Text errorMessageText;
    @FXML
    HBox errorBox;
    @FXML
    Button createGameConfirmButton;
    @FXML
    Button createGameCancelButton;
    @FXML
    TextField createNameTextField;
    @FXML
    TextField createPasswordTextField;
    @FXML
    TextField editMapSizeTextfield;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @Inject
    BrowseGameController browseGameController;
    @Inject
    @SubComponent
    BubbleComponent bubbleComponent;
    @FXML
    Pane captainContainer;

    @Inject
    public CreateGameController() {

    }

    @Inject
    CreateGameService createGameService;

    @OnRender
    public void initialize() {
        createGameService = (createGameService == null) ? new CreateGameService() : createGameService;
        createGameService.setCreateGameController(this);
        initializeMaxMembersTextField();
        this.controlResponses = responseConstants.respCreateGame;
        initializeMapSizeTextField(editMapSizeTextfield);
    }

    static void initializeMapSizeTextField(TextField editMapSizeTextfield) {
        editMapSizeTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                editMapSizeTextfield.setText(newValue.replaceAll("\\D", ""));
            } else if (!newValue.isEmpty()) {
                try {
                    int number = Integer.parseInt(newValue);
                    if (number > 200 || number < 50) {
                        editMapSizeTextfield.setText(oldValue);  // Wenn die Zahl >= 200 ist, bleibt der alte Wert bestehen
                    }
                } catch (NumberFormatException e) {
                    editMapSizeTextfield.setText(oldValue);  // Falls eine ungültige Zahl eingegeben wurde, alten Wert setzen
                }
            }
        });
    }

    @OnRender
    public void setCaptain() {
        this.captainContainer.getChildren().add(this.bubbleComponent);
        this.bubbleComponent.setCaptainText(this.resources.getString("pirate.newGame"));
    }

    // class was modified! some code was refactored
    // now use subscriber
    @OnKey(code = KeyCode.ENTER)
    public void createGame() {
        int mapSize = 0;
        int maxMembers = 0;
        String gameName = this.createNameTextField.getText();
        String password = this.createPasswordTextField.getText();
        if (!maxMembersTextField.getText().isEmpty() && !editMapSizeTextfield.getText().isEmpty()) {
            maxMembers = Integer.parseInt(maxMembersTextField.getText());
            mapSize = Integer.parseInt(editMapSizeTextfield.getText());
        }
        GameSettings settings = new GameSettings(mapSize);
        if (checkIt(gameName, password) && !this.editMapSizeTextfield.getText().isEmpty() &&
                createGameService.nameIsAvailable(gameName) && maxMembers > 0) {
            subscriber.subscribe(createGameService.createGame(gameName, settings, password, maxMembers),
                    result -> {
                        browseGameController.init();
                        app.show(browseGameController);
                    },
                    error -> {
                        this.bubbleComponent.setErrorMode(true);
                        this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                    });

        } else if (!createGameService.nameIsAvailable(gameName)) {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(409));
        } else {
            this.bubbleComponent.setErrorMode(true);
        }
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void cancel() {
        app.show("/browseGames");
    }

    public void showError(Throwable error) {
        errorMessageText.setText(getErrorInfoText(error));
    }

    public void setCreateGameService(CreateGameService createGameService) {
        this.createGameService = createGameService;
    }

    public void initializeMaxMembersTextField() {
        maxMembersTextField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9]|[1-4][0-9]|50)?")) {
                return change;
            }
            return null;
        }));
    }

    public void mapSize50() {
        editMapSizeTextfield.setText("50");
    }

    public void mapSize100() {
        editMapSizeTextfield.setText("100");
    }

    public void mapSize150() {
        editMapSizeTextfield.setText("150");
    }

    public void mapSize200() {
        editMapSizeTextfield.setText("200");
    }
}

