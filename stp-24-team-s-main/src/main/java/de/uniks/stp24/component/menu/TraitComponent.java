package de.uniks.stp24.component.menu;

import de.uniks.stp24.controllers.GangCreationController;
import de.uniks.stp24.model.Trait;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.ResourceBundle;

@Component(view = "Trait.fxml")
public class TraitComponent extends HBox implements ReusableItemComponent<Trait> {
    @FXML
    Button chooseTraitButton;
    @FXML
    Button unChooseTraitButton;
    @FXML
    Text traitName;
    @FXML
    Label costText;

    final ResourceBundle variablesResourceBundle;

    Trait trait;

    final GangCreationController gangCreationController;

    final Boolean showChoose;
    final Boolean showRemove;

    @Inject
    public TraitComponent(GangCreationController gangCreationController, ResourceBundle variablesResourceBundle, Boolean showChoose, Boolean showRemove) {
        this.gangCreationController = gangCreationController;
        this.showChoose = showChoose;
        this.showRemove = showRemove;
        this.variablesResourceBundle = variablesResourceBundle;
    }

    @Override
    public void setItem(@NotNull Trait trait) {
        this.chooseTraitButton.setId(trait.id() + "ButtonChoose");
        this.unChooseTraitButton.setId(trait.id() + "ButtonUnChoose");
        this.trait = trait;
        this.setId(trait.id());
        traitName.setText(variablesResourceBundle.getString(this.trait.id()));

        if (trait.cost() > 0) costText.setText("+"+ trait.cost());
        else costText.setText(String.valueOf(trait.cost()));

        setButtonsVisibility(showChoose, showRemove);
        this.setStyle("-fx-background-image: url('/de/uniks/stp24/assets/empireConfiguration/trait_unhovered.png');" +
                "-fx-background-size: 80% 100%;" + "-fx-background-repeat: no-repeat;" + "-fx-background-position: center center;");
    }

    public void chooseTrait() {
        this.gangCreationController.addTrait(trait);
    }

    public void unChooseTrait() {
        this.gangCreationController.deleteTrait(trait);
    }

    public void setButtonsVisibility(boolean showChoose, boolean showRemove) {
        chooseTraitButton.setVisible(showChoose);
        unChooseTraitButton.setVisible(showRemove);
    }

    public void showDetails() {
        gangCreationController.showTraitDetails(trait);
        this.setStyle("-fx-background-image: url('/de/uniks/stp24/assets/empireConfiguration/trait_hovered.png');" +
                "-fx-background-size: 80% 100%;" + "-fx-background-repeat: no-repeat;" + "-fx-background-position: center center;");
    }

    public void unShowDetails() {
        gangCreationController.unShowTraitDetails();
        this.setStyle("-fx-background-image: url('/de/uniks/stp24/assets/empireConfiguration/trait_unhovered.png');" +
                "-fx-background-size: 85% 100%;" + "-fx-background-repeat: no-repeat;" + "-fx-background-position: center center;");
    }
}