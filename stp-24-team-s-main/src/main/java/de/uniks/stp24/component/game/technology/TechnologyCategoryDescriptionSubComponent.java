package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.Effect;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "TechnologyCategoryDescription.fxml")
public class TechnologyCategoryDescriptionSubComponent extends HBox implements ReusableItemComponent<Effect> {

    @FXML
    public ImageView resourceImage;
    @FXML
    public Label descriptionLabel;

    Effect effect;

    @Inject
    TechnologyService technologyService;

    final ImageCache imageCache = new ImageCache();

    @Inject
    Subscriber subscriber;

    ResourceBundle variablesResourceBundle;

    @Inject
    public TechnologyCategoryDescriptionSubComponent(ResourceBundle variablesResourceBundle) {
        this.variablesResourceBundle = variablesResourceBundle;
    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.dispose();
        }
    }

    @Override
    public void setItem(@NotNull Effect effect) {
        descriptionLabel.setStyle("-fx-opacity: 1");
        setEffect(effect);
        setImage();
        setDescriptionLabel();
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    public void setImage() {
        String variable = effect.variable();

        /*
         * Iterate through all possible paths that technologies can affect
         */
        for (String key : Constants.resourceTranslation.keySet()) {
            if (variable.contains(key)) {
                resourceImage.setImage(imageCache.get("icons/resources/" + key + ".png"));
                break;
            } else if (variable.contains("pop")) {
                resourceImage.setImage(imageCache.get("icons/resources/population.png"));
                break;
            } else if (variable.contains("market.fee")) {
                resourceImage.setImage(imageCache.get("assets/market/market_fee.png"));
                break;
            }
        }

        if (resourceImage.getImage() == null) {
            for (String tech : Constants.technologyTranslation.values()) {
                if (variable.contains(tech)) {
                    resourceImage.setImage(imageCache.get("assets/technologies/tags/" + tech + ".png"));
                    break;
                }
            }
        }

        if (resourceImage.getImage() == null && variable.contains("systems")) {
            for (String island : Constants.islandTranslation.keySet()) {
                if (variable.contains(island)) {
                    resourceImage.setImage(imageCache.get("icons/islands/" + island + ".png"));
                    break;
                }
            }
            if (resourceImage.getImage() == null) {
                for (String upgrade : Constants.upgradeTranslation.keySet()) {
                    if (variable.contains(upgrade)) {
                        resourceImage.setImage(imageCache.get("icons/islands/regular.png"));
                        break;
                    }
                }
            }
        }

        if (resourceImage.getImage() == null && variable.contains("buildings")) {
            for (String building : Constants.buildingTranslation.keySet()) {
                if (variable.contains(building)) {
                    resourceImage.setImage(imageCache.get(Constants.buildingsIconPathsMap.get(building).replace("de/uniks/stp24/", "")));
                    break;
                }
            }
        }

        if (resourceImage.getImage() == null && variable.contains("district")) {
            for (String district : Constants.siteTranslation.keySet()) {
                if (variable.contains(district)) {
                    resourceImage.setImage(imageCache.get(Constants.sitesIconPathsMap.get(district).replace("de/uniks/stp24/", "")));
                    break;
                }
            }
        }

        if (resourceImage.getImage() == null && variable.contains("ship")) {
            resourceImage.setImage(imageCache.get("assets/technologies/tags/engineering.png"));
        }

    }

    public void setDescriptionLabel() {

        if (effect.multiplier() != 1 && effect.multiplier() != 0) {
            descriptionLabel.setText(String.format("%+d", (int) ((effect.multiplier() * 100.0) - 100)) + " % "
                    + variablesResourceBundle.getString(effect.variable()));
        }

        if (effect.base() != 0) {
            descriptionLabel.setText(String.format("%+d", (int) effect.base()) + " " + variablesResourceBundle.getString(effect.variable()));
        }
    }
}
