package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Site;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import static de.uniks.stp24.service.Constants.sitesIconPathsMap;

@Component(view = "ClaimingSite.fxml")
public class ClaimingSiteComponent extends Pane implements ReusableItemComponent<Site> {
    @FXML
    ImageView siteImage;
    @FXML
    Text siteCapacityText;

    @Inject
    public ImageCache imageCache;

    @Inject
    public ClaimingSiteComponent() {}

    @Override
    public void setItem(@NotNull Site item) {
        this.siteImage.setImage(this.imageCache.get("/" + sitesIconPathsMap.get(item.siteID())));
        this.siteCapacityText.setText(String.format("%d", item.maxCells()));
    }
}
