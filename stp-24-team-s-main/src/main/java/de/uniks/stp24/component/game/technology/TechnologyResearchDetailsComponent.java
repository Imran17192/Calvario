package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.rest.PresetsApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.ResourceBundle;

@Component(view = "TechnologyResearchDetails.fxml")
public class TechnologyResearchDetailsComponent extends VBox {

    @FXML
    public Label base;
    @FXML
    public VBox costVBox;
    @FXML
    public Label total;

    @Inject
    public Subscriber subscriber;

    @Inject
    public PresetsApiService presetsApiService;

    @Inject
    public TechnologyService technologyService;

    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public GameLogicApiService gameLogicApiService;

    @Inject
    public TokenStorage tokenStorage;

    @Inject
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;

    @Inject
    @Named("variablesResourceBundle")
    public ResourceBundle variablesResourceBundle;

    public TechnologyExtended technology;

    public TechnologyCategorySubComponent categoryComponent;

    @Inject
    public TechnologyResearchDetailsComponent() {

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
        costVBox.getChildren().clear();
        this.technology = null;
    }

    public void setTechnologyInfos(TechnologyExtended technology) {
        this.technology = technology;
        subscriber.subscribe(gameLogicApiService.getTechnologyCostAndTime(tokenStorage.getEmpireId(), "technology.cost", technology.id()),
                aggregateResultDto -> {
                    HashSet<Node> nodes = new HashSet<>();
                    base.setText("Base: " + technology.cost() * 100);
                    costVBox.getChildren().clear();
                    for (AggregateItemDto item : aggregateResultDto.items()) {
                        if (item.subtotal() == 0 || item.subtotal() == technology.cost() * 100) {
                            continue;
                        }
                        Label l = new Label();
                        l.setStyle("-fx-font-size: 16px; -fx-text-fill: black");
                        String s = variablesResourceBundle.getString(item.variable());
                        int index = s.indexOf("-");
                        if (index != -1) {
                            s = s.substring(0, index);
                        }
                        if (item.variable().contains("multiplier")) {
                            l.setText(String.format("%+d", (int) item.subtotal()) + " " + s);
                        }
                        nodes.add(l);
                    }
                    total.setText("Total: " + aggregateResultDto.total());
                    costVBox.getChildren().addAll(nodes);
                    if (costVBox.getChildren().isEmpty()) costVBox.setStyle("-fx-padding: 0 0 0 0");
                },
                error -> System.out.println("Error try to get costs of " + technology.id() + " because: " + error.getMessage()));
    }

    public void setCategory(TechnologyCategorySubComponent categoryComponent) {
        this.categoryComponent = categoryComponent;
    }
}