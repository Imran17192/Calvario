package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.App;
import de.uniks.stp24.model.Effect;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.ImageCache;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.constructs.listview.ComponentListCell;
import org.fulib.fx.constructs.listview.ReusableItemComponent;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.ResourceBundle;

@Component(view = "TechnologyCategorySubComponent.fxml")
public class TechnologyCategorySubComponent extends VBox implements ReusableItemComponent<TechnologyExtended> {
    private final TechnologyCategoryComponent technologyCategoryComponent;

    @FXML
    public ImageView tagImage3;
    @FXML
    public ImageView tagImage2;
    @FXML
    public ImageView tagImage1;

    @FXML
    public ListView<Effect> descriptionListView;
    @FXML
    public ImageView timeImage;
    @FXML
    public Label timeLabel;
    @FXML
    public ImageView researchImage;
    @FXML
    public Label researchLabel;
    @FXML
    public Button researchButton;
    @FXML
    public HBox researchHBox;
    @FXML
    public Label technologyLabel;
    @FXML
    public Label showEffectLabel;
    @FXML
    public Tooltip showEffectTooltip;
    @FXML
    public Tooltip researchLabelTooltip;

    public Subscriber subscriber;

    final App app;

    TechnologyExtended technology;

    final TechnologyService technologyService;

    @Inject
    TechnologyOverviewComponent technologyOverviewComponent;
    ImageCache imageCache;

    final ObservableList<Effect> description = FXCollections.observableArrayList();

    @Inject
    @Named("gameResourceBundle")
    public ResourceBundle gameResourceBundle;

    public ResourceBundle variablesResourceBundle;

    @Resource
    public final ResourceBundle technologiesResourceBundle;
    TokenStorage tokenStorage;

    public TechnologyResearchDetailsComponent technologyResearchDetailsComponent;

    public TechnologyEffectDetailsComponent technologyEffectDetailsComponent;

    final Provider<TechnologyCategoryDescriptionSubComponent> provider = () -> new TechnologyCategoryDescriptionSubComponent(variablesResourceBundle);

    /**
     * This class is for the components of the listView in the technology category
     */
    @Inject
    public TechnologyCategorySubComponent(TechnologyCategoryComponent technologyCategoryComponent, TechnologyService technologyService,
                                          App app, ResourceBundle technologiesResourceBundle, TokenStorage tokenStorage,
                                          Subscriber subscriber, ResourceBundle variablesResourceBundle,
                                          TechnologyEffectDetailsComponent technologyEffectDetailsComponent,
                                          TechnologyResearchDetailsComponent technologyResearchDetailsComponent, ImageCache imageCache) {

        this.technologyCategoryComponent = technologyCategoryComponent;
        this.technologyService = technologyService;
        this.app = app;
        this.technologiesResourceBundle = technologiesResourceBundle;
        this.variablesResourceBundle = variablesResourceBundle;
        this.tokenStorage = tokenStorage;
        this.subscriber = subscriber;
        this.technologyEffectDetailsComponent = technologyEffectDetailsComponent;
        this.technologyResearchDetailsComponent = technologyResearchDetailsComponent;
        this.imageCache = imageCache;
    }

    /**
     * Set the item inclusive the attributes tags, costs, and id
     *
     * @param technologyExtended is the technology for the subcomponent in the ListView
     */
    @Override
    public void setItem(@NotNull TechnologyExtended technologyExtended) {
        researchLabel.setVisible(true);
        timeLabel.setVisible(true);

        this.technology = technologyExtended;
        technologyLabel.setText(technologiesResourceBundle.getString(technologyExtended.id()));

        if (technologyService.getUnlockedList().stream().anyMatch(tech -> tech.id().equals(technology.id()))) {
            researchHBox.getChildren().removeAll(researchLabel, timeImage, timeLabel, researchButton, researchImage);
        } else {
            /* get Time and Costs of Technology only if it isn't unlocked yet */
            subscriber.subscribe(technologyService.getTechnologyTimeAndCost(tokenStorage.getEmpireId(), "technology.cost", technology.id()),
                    aggregateResultDto -> researchLabel.setText(String.valueOf(aggregateResultDto.total())),
                    error -> System.out.println("Error after try to get cost of technology " + technology.id() + " reason: " + error.getMessage())
            );
            subscriber.subscribe(technologyService.getTechnologyTimeAndCost(tokenStorage.getEmpireId(), "technology.time", technology.id()),
                    aggregateResultDto -> timeLabel.setText(String.valueOf(aggregateResultDto.total())),
                    error -> System.out.println("Error after ty to get time of technology " + technology.id() + " reason: " + error.getMessage())
            );
        }

        int i = technologyExtended.tags().length;

        if (i > 0 && technologyExtended.tags()[0] != null) {
            tagImage1.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[0] + ".png"));
        }
        if (i > 1 && technologyExtended.tags()[1] != null) {
            tagImage2.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[1] + ".png"));
        }
        if (i > 2 && technologyExtended.tags()[2] != null) {
            tagImage3.setImage(imageCache.get("assets/technologies/tags/" + technologyExtended.tags()[2] + ".png"));
        }

        descriptionListView.getItems().clear();
        if (technology.effects().length != 0) {
            description.addAll(technology.effects());
            descriptionListView.setItems(description);
            descriptionListView.setCellFactory(list -> new ComponentListCell<>(this.app, this.provider));
        }

        if (technology.effects().length > 1) {
            showEffectLabel.setVisible(true);
            showEffectLabel.setMouseTransparent(false);
        } else {
            showEffectLabel.setVisible(false);
            showEffectLabel.setMouseTransparent(true);
        }
    }

    @OnInit
    public void init() {

    }

    @OnRender
    public void render() {
        timeImage.setImage(imageCache.get("icons/time.png"));
        researchImage.setImage(imageCache.get("icons/resources/research.png"));

        showEffectTooltip.setGraphic(technologyEffectDetailsComponent);
        showEffectTooltip.setShowDelay(Duration.ZERO);
        showEffectTooltip.setShowDuration(Duration.INDEFINITE);

        researchLabelTooltip.setGraphic(technologyResearchDetailsComponent);
        researchLabelTooltip.setShowDelay(Duration.ZERO);
        researchLabelTooltip.setShowDuration(Duration.INDEFINITE);

        researchLabelTooltip.setOnShowing(event -> {
            researchLabelTooltip.setGraphic(technologyResearchDetailsComponent);
            technologyResearchDetailsComponent.setTechnologyInfos(technology);
        });

        showEffectTooltip.setOnShowing(event -> {
            showEffectTooltip.setGraphic(technologyEffectDetailsComponent);
            technologyEffectDetailsComponent.setTechnologyInfos(technology);
        });

        researchLabelTooltip.setOnHiding(event -> {
            researchLabelTooltip.setGraphic(null);
            technologyResearchDetailsComponent.clear();
        });

        showEffectTooltip.setOnHiding(event -> {
            showEffectTooltip.setGraphic(null);
            technologyEffectDetailsComponent.clear();
        });

        technologyResearchDetailsComponent.setCategory(this);
    }

    public void researchClicked() {
        technologyCategoryComponent.showResearchComponent(technology);
    }

    @OnDestroy
    public void destroy() {
        description.clear();
        descriptionListView.getItems().clear();
    }
}
