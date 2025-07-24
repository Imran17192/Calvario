package de.uniks.stp24.component.game;

import de.uniks.stp24.model.Contact;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.game.IslandsService;

import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ContactsService;
import de.uniks.stp24.service.game.EmpireService;
import de.uniks.stp24.service.game.WarService;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "ContactDetailsComponent.fxml")
public class ContactDetailsComponent extends StackPane {
    @FXML
    public Text intelText;
    @FXML
    public Text popText;
    @FXML
    public Text homeText;
    @FXML
    public ImageView populationIcon;
    @FXML
    public Text siteText;
    @FXML
    public Text buildingsText;
    @FXML
    public ImageView homeIcon;
    @FXML
    public Text strengText;
    @FXML
    public ImageView strengthIcon;
    @FXML
    Button closeContactDetailComponentButton;
    @FXML
    Text empireNameText;
    @FXML
    ImageView empireImageView;
    @FXML
    ToggleButton warButton;
    @FXML
    Text warStateText;

    @SubComponent
    @Inject
    public WarComponent warComponent;


    public Pane parent;

    @Inject
    public ImageCache imageCache;
    @Inject
    public IslandsService islandsService;

    @Inject
    @Resource
    @Named("gameResourceBundle")
    public ResourceBundle resources;

    @Inject
    WarService warService;
    @Inject
    Subscriber subscriber;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    EventListener eventListener;
    @Inject
    EmpireService empireService;
    @Inject
    public ContactsService contactsService;

    private Contact contact;
    private final ObservableList<WarDto> wars = FXCollections.observableArrayList();


    @Inject
    public ContactDetailsComponent() {
        this.populationIcon = new ImageView();
        this.homeIcon = new ImageView();
        this.strengthIcon = new ImageView();
    }

    @OnInit
    public void init() {
    }

    public void setContactInformation(Contact contact) {

        this.contact = contact;
        contact.checkIslands();
        setInfo();
        intelText.setText("Intel: " + calculateIntel(contact.getIntel()));
        empireNameText.setText(contact.getEmpireName());
        empireImageView.setImage(imageCache.get(contact.getEmpireFlag()));
        populationIcon.setImage(imageCache.get("icons/resources/population.png"));
        homeIcon.setImage(imageCache.get("assets/contactsAndWars/home.png"));
        strengthIcon.setImage(imageCache.get("assets/contactsAndWars/cannon.png"));

        popText.setText("Population" + ": " +
                contact.getDiscoveryStats().get("pop"));

        siteText.setText("Sites" + ": " +
                contact.getDiscoveryStats().get("sites"));

        buildingsText.setText("Buildings" + ": " +
                contact.getDiscoveryStats().get("buildings"));

        calculateStrength();

        updateWarButtonText();
        checkWarSituation();
        warButton.setOnAction(event -> toggleWarState());
    }

    public void checkWarSituation() {
        if (Objects.isNull(this.contact)) return;
        boolean attacker = contactsService.attacker(contact.getEmpireID());
        boolean defender = contactsService.defender(contact.getEmpireID());
        contact.setAtWarWith(!contact.atWarWith.get());
        contact.setAtWarWith(attacker || defender);
        warButton.setSelected(attacker || defender);
        warButton.setDisable(attacker);
        updateWarButtonText();

    }

    public void closeContactDetailsComponent() {
        this.getParent().visibleProperty().setValue(false);
        visibleProperty().setValue(false);
    }

    public void openDetail(Contact contact) {
        setContactInformation(contact);
        this.getParent().visibleProperty().setValue(true);
        visibleProperty().setValue(true);
    }

    public void setParent(Pane parent) {
        this.parent = parent;
        this.parent.getChildren().add(this);
    }

    private void setInfo() {
        contact.setEmpireDtos(islandsService.getDevIsles());
    }

    @OnDestroy
    public void removeData() {
        this.contact = null;
    }

    private String calculateIntel(double value) {
        if (value == 0) return "none";
        if (value > 0 && value <= 30) return "low";
        if (value > 30 && value <= 60) return "medium";
        if (value > 60 && value <= 90) return "high";
        return "exactly";
    }

    public void calculateStrength() {
        islandsService.getEnemyStrength(contact.getMyOwnId(), contact.getEmpireID(), this.contact);
    }

    public void calculateStrength(double value) {
        String text = "";
        if (value > 2.1) text = "mighty";
        if (value < 2.1 && value > 1.1) text = "very strong";
        if (value < 1.1 && value > 0.1) text = "strong";
        if (value < 0.1 && value > -1.1) text = "weak";
        if (value < -1.1 && value > -2.1) text = "very weak";
        if (value < -2.1) text = "dust";
        this.strengText.setText("Strength: " + text);

    }

    public void updateWarButtonText() {
        warStateText.setStyle("-fx-font-size: 12px;");
        warStateText.setFill(Color.WHITE);
        warButton.setText((warButton.isSelected() ? "Stop" : "Start") + " war" );
        warStateText.setText("You are at " + (warButton.isSelected() ? "war" : "peace") +  " with " + contact.getEmpireName());
        if (warButton.isDisabled()) {
            warButton.setText("");
        }
    }

    private void toggleWarState() {
        updateWarButtonText();
        contact.setAtWarWith(warButton.isSelected());
        warSetUp();
    }

    private void warSetUp() {
        if (contact.isAtWarWith()) {
            contactsService.startWarWith(contact.getEmpireID());
        } else {
            contactsService.stopWarWith(contact.getEmpireID());
        }
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setWarMessagePopup(String messageType, String attackerName, String myOwnEmpireID, WarDto warDto) {
        if (messageType.equals("updated")) return;
        if (myOwnEmpireID.equals(warDto.defender())) {
            contactsService.setAttacker(attackerName);
            warComponent.getParent().setVisible(true);
            warComponent.setVisible(true);
            warComponent.showWarMessage(messageType, attackerName);
        }
    }

    public void setWarComponent(WarComponent warComponent) {
        this.warComponent = warComponent;
    }
}
