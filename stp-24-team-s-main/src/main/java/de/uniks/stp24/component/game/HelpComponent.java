package de.uniks.stp24.component.game;
import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.model.TechHelp;
import de.uniks.stp24.service.Constants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Map;
import java.util.ResourceBundle;

@Component(view = "help.fxml")
public class HelpComponent extends AnchorPane {

    @FXML
    Button closeHelpButton;
    @FXML
    Button backButton;
    @FXML
    public ListView<TechHelp> technologyTagsListView;
    private InGameController inGameController;

    public ObservableList<TechHelp> technologies = FXCollections.observableArrayList();

    @Resource
    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;


    @Inject
    public HelpComponent(){

    }

    public void back(){
        setVisible(false);
        inGameController.pauseGameFromHelp();
    }

    public void close(){
        setVisible(false);
        this.inGameController.shadow.setVisible(false);
    }

    public void setInGameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public void displayTechnologies() {
        for (Map.Entry<String, String> icon : Constants.technologyIconMap.entrySet()) {
            TechHelp techHelp = new TechHelp(icon.getValue(), icon.getKey());
            technologies.add(techHelp);
        }

        technologyTagsListView.setItems(technologies);
        technologyTagsListView.setCellFactory(listView -> new TechnologyListCell(technologiesResourceBundle));
    }
}

class TechnologyListCell extends ListCell<TechHelp> {

    private final HBox content;
    private final ImageView imageView;
    private final Text text;

    private final ResourceBundle technologiesResourceBundle;

    public TechnologyListCell(ResourceBundle technologiesResourceBundle) {
        super();
        imageView = new ImageView();
        text = new Text();
        text.getStyleClass().add("technologyText");
        this.technologiesResourceBundle = technologiesResourceBundle;

        content = new HBox(imageView, text);
        content.setSpacing(50);
    }

    @Override
    protected void updateItem(TechHelp techHelp, boolean empty) {
        super.updateItem(techHelp, empty);
        if (techHelp != null && !empty) {
            imageView.setImage(new Image(techHelp.imageID()));
            text.setText(technologiesResourceBundle.getString("technologies." + techHelp.description()));
            setGraphic(content);
        } else {
            setGraphic(null);
        }
    }


}
