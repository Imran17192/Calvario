package de.uniks.stp24.component.game;

import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.IslandAttributeStorage;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.ResourcesService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static de.uniks.stp24.service.Constants.resourceImagePath;

@Component(view = "Details.fxml")
public class DetailsComponent extends AnchorPane {

    @FXML
    public Button showConsumption;
    @FXML
    public Button showProduction;
    @FXML
    public ImageView res1Pic;
    @FXML
    public Text amount1;
    @FXML
    public ImageView res2Pic;
    @FXML
    public Text amount2;
    @FXML
    public ImageView res3Pic;
    @FXML
    public Text amount3;
    @FXML
    public ImageView res4Pic;
    @FXML
    public Text amount4;
    @FXML
    public ImageView res5Pic;
    @FXML
    public Text amount5;
    @FXML
    public ImageView res6Pic;
    @FXML
    public Text amount6;
    @FXML
    public ImageView res7Pic;
    @FXML
    public Text amount7;
    @FXML
    public ImageView res8Pic;
    @FXML
    public Text amount8;
    @FXML
    public ImageView res9Pic;
    @FXML
    public Text amount9;
    @FXML
    public Text title;
    @Inject
    InGameService inGameService;
    @Inject
    ResourcesService resourcesService;
    @Inject
    ImageCache imageCache;
    @Inject
    public IslandAttributeStorage islandAttributes;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    Subscriber subscriber;
    @Inject
    @Resource
    @Named("gameResourceBundle")
    ResourceBundle gameResourceBundle;

    final List<ImageView> resImages = new ArrayList<>();
    final List<Text> resInf = new ArrayList<>();

    @Inject
    public DetailsComponent(){

    }

    /*
    The methods below are needed to set a HBOX for showing
    total consumption and total production of current selected island.
     */

    public void setSumProduction(Map<String, Double> totalProduction){
        resetResources();
        int i = 0;
        for (Map.Entry<String, Double> entry : totalProduction.entrySet()) {
            resImages.get(i).setImage(this.imageCache.get("/"+resourceImagePath.get(entry.getKey())));
            resInf.get(i).setText("+" + Math.round(entry.getValue() * 100.0) / 100.0);
            i ++;
        }
        title.setText("     " + gameResourceBundle.getString("total.production"));
    }

    public void setSumConsumption(Map<String, Double> totalConsumption){
        resetResources();
        int i = 0;
        for (Map.Entry<String, Double> entry : totalConsumption.entrySet()) {
            resImages.get(i).setImage(this.imageCache.get("/"+resourceImagePath.get(entry.getKey())));
            resInf.get(i).setText("-" + Math.round(entry.getValue() * 100.0) / 100.0);
            i++;
        }
        title.setText("  " + gameResourceBundle.getString("total.consumption"));
    }

    public void setResLists(){
        ImageView[] images = {res1Pic, res2Pic, res3Pic, res4Pic, res5Pic, res6Pic, res7Pic, res8Pic, res9Pic};
        resImages.addAll(List.of(images));

        Text[] amounts = {amount1, amount2, amount3, amount4, amount5, amount6, amount7, amount8, amount9};
        resInf.addAll(List.of(amounts));

        for(int i = 0; i < 9; i++){
            resInf.get(i).setStyle("-fx-font-family: 'Tempus Sans ITC';" +
                    "-fx-background-color: 'TRANSPARENT';" +
                    "-fx-font-weight: none;" +
                    "-fx-font-size: 12;");
        }
        title.setStyle("-fx-font-family: 'Blackadder ITC';" +
                "-fx-background-color: 'TRANSPARENT';" +
                "-fx-font-weight: none;" +
                "-fx-font-size: 22;");

        showProduction.setVisible(false);
        showConsumption.setVisible(true);
    }

    public void showConsumption(){
        showProduction.setVisible(true);
        showConsumption.setVisible(false);
        setSumConsumption(islandAttributes.mergeConsumption(islandAttributes.island));
    }

    public void showProduction(){
        showProduction.setVisible(false);
        showConsumption.setVisible(true);
        setSumProduction(islandAttributes.mergeProduction(islandAttributes.island));

    }

    public void resetResources() {
        for (ImageView resImage : resImages) {
            resImage.setImage(null);
        }

        for (Text text : resInf) {
            text.setText("");
        }
    }
}
