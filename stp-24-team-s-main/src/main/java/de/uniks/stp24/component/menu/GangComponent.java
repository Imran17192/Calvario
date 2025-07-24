package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.Gang;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "Gang.fxml")
public class GangComponent extends Pane implements ReusableItemComponent<Gang> {

    @FXML
    Pane pane;
    @FXML
    ImageView portraitImage;
    @FXML
    Label gangNameTextComponent;

    final ImageCache imageCache = new ImageCache();

    @Inject
    public GangComponent() {

    }

    @Override
    public void setItem(@NotNull Gang gang) {
        gangNameTextComponent.setStyle("-fx-text-fill: black;");
        gangNameTextComponent.setText(gang.name());
        pane.setStyle("-fx-background-color: " + gang.color());
        portraitImage.setImage(imageCache.get(gang.portraitsPath()));
    }
}