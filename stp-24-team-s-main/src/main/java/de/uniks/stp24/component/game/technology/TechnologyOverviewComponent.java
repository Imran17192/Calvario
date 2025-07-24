package de.uniks.stp24.component.game.technology;

import de.uniks.stp24.model.TechnologyExtended;
import de.uniks.stp24.service.game.TechnologyService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.controller.Resource;
import org.fulib.fx.annotation.controller.SubComponent;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnRender;
import org.fulib.fx.controller.Subscriber;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Objects;
import java.util.ResourceBundle;

@Component(view = "TechnologyOverview.fxml")
public class TechnologyOverviewComponent extends AnchorPane {

    @FXML
    public StackPane researchJobContainer;
    @FXML
    VBox technologyOverviewBox;
    @FXML
    Button closeTechnologyOverviewButton;
    @FXML
    public Button crewRelationsButton;
    @FXML
    public Button shipbuildingButton;
    @FXML
    public Button marineSienceButton;

    @Inject
    public Subscriber subscriber;
    @Inject
    public TechnologyService technologyService;

    private Pane parent;

    @Inject
    @SubComponent
    public TechnologyCategoryComponent technologyCategoryComponent;

    @Inject
    @Resource
    @Named("technologiesResourceBundle")
    public ResourceBundle technologiesResourceBundle;


    @Inject
    public TechnologyOverviewComponent() {

    }

    @OnInit
    public void init() {
        technologyCategoryComponent.setTechnologyCategoryOverviewComponent(this);
    }

    @OnRender
    public void render() {

    }

    @OnDestroy
    public void destroy() {
        if (subscriber != null) {
            subscriber.dispose();
        }

        if (technologyService.subscriber != null) {
            technologyService.subscriber.dispose();
        }
    }

    public void close() {
        this.setVisible(false);
        parent.setVisible(false);
    }

    public void showWindow(){
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "engineering") && technologyCategoryComponent.technologyCategoryName.equals("engineering")){
                    initialJobDescriptionHandling(technology);
                    break outerLoop;
                } else if (Objects.equals(tag, "society") && technologyCategoryComponent.technologyCategoryName.equals("society")) {
                    initialJobDescriptionHandling(technology);
                    break outerLoop;
                } else if (Objects.equals(tag, "physics") && technologyCategoryComponent.technologyCategoryName.equals("physics")) {
                    initialJobDescriptionHandling(technology);
                    break outerLoop;
                } else {
                technologyCategoryComponent.unShowJobWindow();
                }
            }
        }
        technologyCategoryComponent.researchJobComponent.setEffectListView();
    }

    public void initialJobDescriptionHandling(TechnologyExtended technology){
        technologyCategoryComponent.setTechnology(technology);
        technologyCategoryComponent.researchJobComponent.setJobDescription(technology);
        technologyCategoryComponent.researchJobComponent.progressHandling();
        technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technologiesResourceBundle.getString(technology.id()));
        technologyCategoryComponent.showJobWindow();
    }

    public void engineering() {
        technologyCategoryComponent.researchJobComponent.handleJobsAlreadyRunning();
        show(technologyCategoryComponent.setCategory("engineering"));
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "engineering")){
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.researchJobComponent.setJobDescription(technology);
                    technologyCategoryComponent.researchJobComponent.progressHandling();
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technologiesResourceBundle.getString(technology.id()));
                    technologyCategoryComponent.showJobWindow();
                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }
        technologyCategoryComponent.researchJobComponent.setEffectListView();
    }

    public void society() {
        technologyCategoryComponent.researchJobComponent.handleJobsAlreadyRunning();
        show(technologyCategoryComponent.setCategory("society"));
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "society")){
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.researchJobComponent.setJobDescription(technology);
                    technologyCategoryComponent.researchJobComponent.progressHandling();
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technologiesResourceBundle.getString(technology.id()));
                    technologyCategoryComponent.showJobWindow();

                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }
        technologyCategoryComponent.researchJobComponent.setEffectListView();
    }

    public void physics() {
        technologyCategoryComponent.researchJobComponent.handleJobsAlreadyRunning();
        show(technologyCategoryComponent.setCategory("physics"));
        outerLoop:
        for (TechnologyExtended technology : technologyCategoryComponent.researchJobComponent.technologies) {
            for (String tag : technology.tags()) {
                if (Objects.equals(tag, "computing")){
                    technologyCategoryComponent.setTechnology(technology);
                    technologyCategoryComponent.researchJobComponent.setJobDescription(technology);
                    technologyCategoryComponent.researchJobComponent.progressHandling();
                    technologyCategoryComponent.researchJobComponent.technologyNameText.setText(technologiesResourceBundle.getString(technology.id()));
                    technologyCategoryComponent.showJobWindow();
                    break outerLoop;
                } else {
                    technologyCategoryComponent.unShowJobWindow();
                }
            }
        }
        technologyCategoryComponent.researchJobComponent.setEffectListView();

    }

    public void setContainer(@NotNull Pane parent) {
        parent.getChildren().add(technologyCategoryComponent);
        this.parent = parent;
        technologyCategoryComponent.setContainer(parent);
        technologyCategoryComponent.setVisible(false);
    }

    /**
     * First Child: TechnologyCategoryComponent
     * Second Child: TechnologyOverviewComponent
     */
    public void show(@NotNull TechnologyCategoryComponent technologyCategory) {
        setCategoryInfos(technologyCategory);
        this.setVisible(false);
        technologyCategory.setVisible(true);
        System.out.println(parent.getChildren().size());
        System.out.println("show " + technologyCategory.technologyCategoryName);

    }

    public void setCategoryInfos(@NotNull TechnologyCategoryComponent technologyCategory) {
        technologyCategory.technologyImage.setImage(technologyCategory.imageCache.get("assets/technologies/categories/" +technologyCategory.technologyCategoryName + ".png"));
        String technologyKey = technologyCategory.technologyCategoryName.replace("_", ".");
        technologyCategory.technologyNameText.setText(technologiesResourceBundle.getString("technologies." + technologyKey));
    }
}
