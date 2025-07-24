package de.uniks.stp24.component.menu;

import de.uniks.stp24.model.MemberUser;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.menu.LobbyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.util.*;

@Component(view = "User.fxml")
public class UserComponent extends StackPane implements ReusableItemComponent<MemberUser> {
    @FXML
    Text usernameText;
    @FXML
    Button kickButton;
    @Inject
    LobbyService lobbyService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @FXML
    StackPane backgroundPane;
    @FXML
    ImageView readyImage;
    @FXML
    ImageView flagImage;

    @Resource
    final ResourceBundle resources;
    private final ImageCache imageCache;
    private MemberUser member;
    @FXML
    ImageView backgroundImage;
    @FXML
    ImageView portraitImage;
    @FXML
    ImageView frameImage;

    final Map<String, Integer> avatarMap = new HashMap<>();

    final ArrayList<Image> backgroundsList = new ArrayList<>();
    final ArrayList<Image> portraitsList = new ArrayList<>();
    final ArrayList<Image> framesList = new ArrayList<>();

    @Inject
    public UserComponent(ImageCache imageCache, ResourceBundle resources) {
        this.imageCache = imageCache;
        this.resources = resources;
    }

    public void kickUser() {
        this.subscriber.subscribe(this.lobbyService.leaveLobby(
                this.member.game()._id(), this.member.user()._id()
        ));
    }

    @Override
    public void setItem(@NotNull MemberUser member) {
        this.member = member;
        this.kickButton.setId("kick" + member.user()._id());

        if (member.user()._id().equals(member.game().owner()) || member.asHost()) this.kickButton.setVisible(false);

        this.backgroundPane.getStyleClass().clear();
        if (member.user()._id().equals(member.game().owner()))
            this.backgroundPane.getStyleClass().add("lobbyListCellHost");
        else this.backgroundPane.getStyleClass().add("lobbyListCellMember");

        if (member.ready()) this.readyImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/ready_gradient.png"));
        else this.readyImage.setImage(this.imageCache.get("/de/uniks/stp24/assets/other/not_ready_gradient.png"));

        if (Objects.isNull(member.empire())) this.flagImage.setImage(this.imageCache.get("/de/uniks/stp24/icons/spectatorSign.png"));
        else this.flagImage.setImage(this.imageCache.get(String.format("/de/uniks/stp24/assets/flags/flag_%d.png", member.empire().flag())));

        this.usernameText.setText(member.user().name());

        if (Objects.isNull(this.member.user()._public())) {
            avatarMap.put("backgroundIndex", 0);
            avatarMap.put("portraitIndex", 8);
            avatarMap.put("frameIndex", 8);
            tokenStorage.setAvatarMap(avatarMap);
            initializeAvatarImage(avatarMap);
        } else initializeAvatarImage(this.member.user()._public());

    }

    public void initializeAvatarImage(Map<String, Integer> avatarMap) {
        String resourcesPaths = "/de/uniks/stp24/assets/avatar/";
        String backgroundFolderPath = "backgrounds/background_";
        String frameFolderPath = "frames/frame_";
        String portraitsFolderPath = "portraits/portrait_";

        for (int i = 0; i <= 9; i++) {
            backgroundsList.add(this.imageCache.get(resourcesPaths + backgroundFolderPath + i + ".png"));
            framesList.add(this.imageCache.get(resourcesPaths + frameFolderPath + i + ".png"));
            portraitsList.add(this.imageCache.get(resourcesPaths + portraitsFolderPath + i + ".png"));
        }
        setImageCode(avatarMap.get("backgroundIndex"), avatarMap.get("portraitIndex"), avatarMap.get("frameIndex"));
    }

    private void setImageCode(int backgroundIndex, int potraitIndex, int frameIndex) {
        backgroundImage.setImage(backgroundsList.get(backgroundIndex));
        portraitImage.setImage(portraitsList.get(potraitIndex));
        frameImage.setImage(framesList.get(frameIndex));
    }

}
