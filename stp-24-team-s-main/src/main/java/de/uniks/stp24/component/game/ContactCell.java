package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Component(view = "ContactCell.fxml")
public class ContactCell extends HBox implements ReusableItemComponent<Contact> {
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireFlagImageView;
    @FXML
    ImageView warSituationIcon;

    @Inject
    ImageCache imageCache;
    ContactDetailsComponent contactDetailsComponent;

    public Contact contact;

    @Inject
    public ContactCell(ImageCache imageCache, ContactDetailsComponent pane) {
        this.imageCache = imageCache;
        this.contactDetailsComponent = pane;
    }

    @Override
    public void setItem(@NotNull Contact contact) {
        this.contact = contact;
        this.setId("contact_" + contact.empireID);
        empireNameText.setText(this.contact.getEmpireName());
        empireFlagImageView.setImage(imageCache.get(this.contact.getEmpireFlag()));
        contact.atWarWithProperty().addListener((obs, wasAtWar, isAtWar) -> updateWarSituationIcon());
        updateWarSituationIcon();
    }

    public Contact getContact() {
        return this.contact;

    }

    public void updateWarSituationIcon() {
        if (contact.isAtWarWith()) {
            warSituationIcon.setImage(imageCache.get("/de/uniks/stp24/assets/contactsAndWars/Contact_War_Icon.png"));
        } else {
            warSituationIcon.setImage(imageCache.get("/de/uniks/stp24/assets/contactsAndWars/Contact_Peace_Icon.png"));
        }
    }

}
