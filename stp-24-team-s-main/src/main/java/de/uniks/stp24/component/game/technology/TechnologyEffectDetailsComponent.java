package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.TechnologyExtended;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "TechnologyEffectDetails.fxml")
public class TechnologyEffectDetailsComponent extends VBox {

    @FXML
    public VBox effectTitleVBox;

    @FXML
    public VBox effectVBox;

    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    @Inject
    App app;

    Provider<TechnologyCategoryDescriptionSubComponent> provider = () -> new TechnologyCategoryDescriptionSubComponent(variablesResourceBundle);

    @Inject
    public TechnologyEffectDetailsComponent() {

    }

    @OnRender
    public void render() {

    }

    @OnInit
    public void init() {

    }

    @OnDestroy
    public void destroy() {

    }

    public void clear() {
        effectVBox.getChildren().clear();
    }

    public void setTechnologyInfos(TechnologyExtended technology) {
        effectVBox.getChildren().clear();
        effectTitleVBox.setStyle("-fx-font-size: 20px");
        effectVBox.getChildren().add(effectTitleVBox);
        effectVBox.setPrefWidth(350);
        for (Effect effect : technology.effects()) {
            TechnologyCategoryDescriptionSubComponent component = provider.get();
            app.initAndRender(component);
            component.setStyle("-fx-font-size: 14px");
            component.setPrefHeight(USE_COMPUTED_SIZE);
            component.setMinWidth(350);
            component.setEffect(effect);
            component.setImage();
            component.setDescriptionLabel();
            effectVBox.getChildren().add(component);
        }
    }
}