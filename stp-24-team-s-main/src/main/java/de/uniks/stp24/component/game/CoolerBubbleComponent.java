package de.uniks.stp24.component.game;

import de.uniks.stp24.component.Captain;
import de.uniks.stp24.dto.AggregateItemDto;
import de.uniks.stp24.model.Announcement;
import de.uniks.stp24.model.Game;
import de.uniks.stp24.model.Jobs;
import de.uniks.stp24.model.Resource;
import de.uniks.stp24.service.Constants;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.game.*;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import org.fulib.fx.annotation.controller.Component;
import org.fulib.fx.annotation.event.OnInit;
import org.fulib.fx.annotation.event.OnKey;
import org.fulib.fx.annotation.event.OnRender;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.function.Consumer;

@Component(view = "HintBubble.fxml")
public class CoolerBubbleComponent extends Captain {

    @FXML
    Button forwardButton;
    @FXML
    Button nextButton;

    @Inject
    public TokenStorage tokenStorage;
    @Inject
    EventListener eventListener;
    @Inject
    public ResourcesService resourcesService;
    @Inject
    public JobsService jobsService;
    @Inject
    public AnnouncementsService announcementsService;
    @Inject
    public FleetService fleetService;
    @Inject
    public EmpireService empireService;
    @Inject
    @Named("gameResourceBundle")
    public  ResourceBundle gameResourceBundle;

    final Random random = new Random();
    int lastPeriod = 0;
    int hintCountdown;
    final ArrayList<String> possibleHints = Constants.hints;

    ObservableList<Announcement> announcements;
    ArrayList<Consumer<Jobs.Job>> forwardMethods = null;
    Jobs.Job job = null;

    @Inject
    public CoolerBubbleComponent(){

    }

    @OnInit
    public void init() {
        setHintCountDown();
        announcements = announcementsService.getAnnouncements();
        announcementsService.setFleetService(this.fleetService);
        this.jobsService.onJobCommonStart((job) -> this.jobsService.onJobCompletion(job._id(), (sameJob) -> announcementsService.addAnnouncement(sameJob)));
    }

    private void setHintCountDown() {
        hintCountdown = random.nextInt(20, 40);
    }

    @OnRender
    public void render() {
        rotateCaptain();

        announcements.addListener((ListChangeListener<Object>) change -> {
            if (announcements.isEmpty()) {
                nextButton.getStyleClass().removeAll("rightTriangleButton");
                nextButton.getStyleClass().add("closeButtonHint");
            }
            else  {
                nextButton.getStyleClass().removeAll("closeButtonHint");
                nextButton.getStyleClass().add("rightTriangleButton");
            }
        });

        subscriber.subscribe(this.eventListener.listen(String.format("games.%s.ticked", tokenStorage.getGameId()), Game.class),
                result -> {
                    if (lastPeriod != result.data().period()) {
                        lastPeriod = result.data().period();
                        subscriber.subscribe(empireService.getResourceAggregates(tokenStorage.getGameId(), tokenStorage.getEmpireId()),
                                aggregateResultDto -> {
                                    for (AggregateItemDto item : aggregateResultDto.items()) {
                                        Resource resource = resourcesService.aggregateItemDtoToResource(item);
                                        if (resource.count() == 0 || resource.count() + item.subtotal() <= 0) {
                                            announcementsService.addAnnouncement(resource);
                                        }
                                    }
                                    decideWhatToSay();
                                },
                                error -> System.out.println("ErrorAggregateSubscriber: " + error));
                    }
                },
                Throwable::printStackTrace);
    }

    public void forward() {
        for (Consumer<Jobs.Job> method : forwardMethods) {
            method.accept(job);
        }
        decideWhatToSay();
    }

    public void talk(String text) {
        setVisible(true);
        setCaptainText(text);
    }

    private void announce() {
        Announcement announcement = announcementsService.getNextAnnouncement();
        forwardButton.setVisible(true);
        forwardButton.setStyle(announcement.forwardIcon());
        forwardMethods = announcement.forwardMethods();
        job = announcement.job();
        talk(announcement.message());
    }

    public void decideWhatToSay(){
        forwardButton.setVisible(false);
        forwardMethods = null;
        job = null;
        if (announcements.isEmpty()) {
            sayTip();
        } else {
            announce();
        }
    }

    private void sayTip() {
        hintCountdown -= 1;
        if (hintCountdown <= 0) {
            talk(gameResourceBundle.getString(possibleHints.get(random.nextInt(possibleHints.size()))));
            setHintCountDown();
        } else {
            silence();
        }
    }

    @OnKey(code = KeyCode.S, alt = true)
    public void removeAltS(){
        silence();
        possibleHints.remove("hint.alt.s");
    }

    @OnKey(code = KeyCode.H, alt = true)
    public void removeAltH(){
        silence();
        possibleHints.remove("hint.alt.h");
    }

    @OnKey(code = KeyCode.E, alt = true)
    public void removeAltE(){
        silence();
        possibleHints.remove("hint.alt.e");
    }

    public void silence() {
        announcementsService.clearAnnouncements();
        setVisible(false);
    }
}
