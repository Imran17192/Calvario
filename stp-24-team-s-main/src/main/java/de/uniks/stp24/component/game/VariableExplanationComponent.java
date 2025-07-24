package de.uniks.stp24.component.game;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Component(view = "VariableExplanationOverview.fxml")
public class VariableExplanationComponent extends VBox {

    @FXML
    ListView<String> effectList;
    @FXML
    Text baseValue;
    @FXML
    Text totalValue;
    @FXML
    Text title;
    @FXML
    ImageView resImage;

    final ObservableList<String> explanations = FXCollections.observableArrayList();

    public VariableExplanationComponent() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("VariableExplanationOverview.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        initialize();
    }

    private void initialize() {
        effectList.setItems(explanations);
    }

    public void fillListWithEffects(List<String> newEffects) {
        explanations.setAll(newEffects);
    }

    public void setValues(String baseValue, String totalValue, String res, String imagePath) {
        this.title.setText(res);
        this.baseValue.setText(baseValue);
        this.totalValue.setText(totalValue);
        this.resImage.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(imagePath))));
    }
}
