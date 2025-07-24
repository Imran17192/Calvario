package de.uniks.stp24.controllers;

import de.uniks.stp24.component.menu.BubbleComponent;
import de.uniks.stp24.model.GameSettings;
import de.uniks.stp24.service.menu.BrowseGameService;
import de.uniks.stp24.service.menu.EditGameService;
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
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;

@Title("%edit.game")
@Controller
public class EditGameController extends BasicController {
    @FXML
    TextField maxMembersTextField;
    @FXML
    HBox errorBoxEdit;
    @FXML
    Text errorMessageTextEdit;
    @FXML
    Button editGameConfirmButton;
    @FXML
    Button editGameCancelButton;
    @FXML
    TextField editNameTextField;
    @FXML
    TextField editPasswordTextField;
    @FXML
    TextField editRepeatPasswordTextField;
    @FXML
    TextField editMapSizeTextfield;
    @FXML
    AnchorPane backgroundAnchorPane;
    @FXML
    VBox cardBackgroundVBox;
    @Inject
    BrowseGameService browseGameService;
    @Inject
    BrowseGameController browseGameController;
    @Inject
    @SubComponent
    BubbleComponent bubbleComponent;
    @FXML
    Pane captainContainer;

    @Inject
    EditGameService editGameService;

    @Inject
    public EditGameController() {
    }

    @OnRender
    public void initialize() {
        editGameService.setEditGameController(this);
        errorMessageTextEdit.setText("");
        errorBoxEdit.setVisible(true);
        initializeMaxMembersTextField();
        this.controlResponses = responseConstants.respEditGame;

        CreateGameController.initializeMapSizeTextField(editMapSizeTextfield);
    }

    @OnRender
    public void setCaptain() {
        this.captainContainer.getChildren().add(this.bubbleComponent);
        this.bubbleComponent.setCaptainText("");
    }

    // class was modified! some code was refactored
// now use subscriber
    @OnKey(code = KeyCode.ENTER)
    public void editGame() {
        this.bubbleComponent.setErrorMode(false);
        int maxMembers = 0;
        int mapSize = 0;
        if (!maxMembersTextField.getText().isEmpty() && !editMapSizeTextfield.getText().isEmpty()) {
            maxMembers = Integer.parseInt(maxMembersTextField.getText());
            mapSize = Math.min(Integer.parseInt(editMapSizeTextfield.getText()), 200);
            mapSize = Math.max(mapSize, 50);
        }
        GameSettings settings = new GameSettings(mapSize);
        String gameName = this.editNameTextField.getText();
        String password = this.editPasswordTextField.getText();
        if (checkIt(gameName, password) && editGameService.nameIsAvailable(gameName) && maxMembers > 0 && !editMapSizeTextfield.getText().isEmpty())  {
            subscriber.subscribe(editGameService.editGame(gameName, settings, password, maxMembers),
                    result -> {
                        browseGameController.init();
                        app.show(browseGameController);
                    },
                    error -> {
                        this.bubbleComponent.setErrorMode(true);
                        this.bubbleComponent.setCaptainText(getErrorInfoText(error));
                    });
        } else if (!editGameService.nameIsAvailable(gameName)) {
            this.bubbleComponent.setErrorMode(true);
            this.bubbleComponent.setCaptainText(getErrorInfoText(409));
        } else {
            this.bubbleComponent.setErrorMode(true);
        }
    }

    @OnKey(code = KeyCode.ESCAPE)
    public void cancel() {
        browseGameService.resetSelectedGame();
        app.show("/browseGames");
    }

    public void showError(int code) {
        errorMessageTextEdit.setText(getErrorInfoText(code));
    }

    public void setEditGameService(EditGameService newEditGameService) {
        editGameService = newEditGameService;
    }

    @OnDestroy
    public void destroy() {

    }

    /*
    This method makes sure that on int values are accepted in text field
     */
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
