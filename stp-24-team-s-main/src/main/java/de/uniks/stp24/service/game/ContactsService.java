package de.uniks.stp24.service.game;

import de.uniks.stp24.component.game.ContactsComponent;
import de.uniks.stp24.dto.CreateWarDto;
import de.uniks.stp24.dto.EmpirePrivate;
import de.uniks.stp24.dto.ReadEmpireDto;
import de.uniks.stp24.dto.WarDto;
import de.uniks.stp24.model.Contact;
import de.uniks.stp24.rest.EmpireApiService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.ws.EventListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.fulib.fx.annotation.event.OnDestroy;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;
import java.util.function.Consumer;

@Singleton
public class ContactsService {
    @Inject
    public IslandsService islandsService;
    @Inject
    public EmpireApiService empireApiService;
    @Inject
    public Subscriber subscriber;
    @Inject
    public TokenStorage tokenStorage;
    @Inject
    public EventListener eventListener;
    @Inject
    public WarService warService;

    public ObservableList<Contact> contactCells = FXCollections.observableArrayList();
    public final ArrayList<Contact> seenEnemies = new ArrayList<>();
    public List<String> hiddenEmpires = new ArrayList<>();
    public String gameID;
    public String myOwnEmpireID;
    public Map<String, WarDto> warsOnProcess = new HashMap<>();
    public final ObservableList<WarDto> warsInThisGame = FXCollections.observableArrayList();

    private Consumer<WarDto> onWarDeletedConsumer;
    private Consumer<WarDto> onWarCreatedRunnable;

    String attacker;
    boolean declaring;
    boolean declaringToDefender;

    public ContactsComponent contactsComponent;


    @Inject
    ContactsService() {

    }

    @OnDestroy
    public void dispose() {
        subscriber.dispose();
        contactCells.clear();
        seenEnemies.clear();
        warsOnProcess.clear();
        warsInThisGame.clear();
        this.gameID = null;
    }

    // add enemy after discovered an island of his empire
    public void addEnemy(String enemy, String islandID) {
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(enemy);
        // if enemy's ID == game owner's ID do nothing
        if (tokenStorage.getEmpireId().equals(enemy)) return;
        // if not, check if already discovered -> add or search it
        if (hiddenEmpires.contains(enemy)) {
            contact = new Contact(empireDto);
            contact.setMyOwnId(myOwnEmpireID);
            hiddenEmpires.remove(enemy);
            contactCells.add(contact);
            seenEnemies.add(contact);
        } else {
            contact = seenEnemies.stream()
                    .filter(element -> element.getEmpireID().equals(enemy)).findFirst().get();
        }

        contact.addIsland(islandID);
        contact.setMyOwnId(myOwnEmpireID);
        saveContacts();
        checkIfInvolveInWarWith(contact);

        // in case that a contact detail component is open, and you go to another island from same contact
        // the view will be updated
        if (Objects.nonNull(contact.getPane()) && contact.getPane().visibleProperty().get())
            contact.getPane().setContactInformation(contact);
    }

    public void addEnemyInPeace(String enemy) {
        ReadEmpireDto dto = islandsService.getEmpire(enemy);
        Contact contact = new Contact(dto);
        contact.setMyOwnId(myOwnEmpireID);
        hiddenEmpires.remove(dto._id());
        seenEnemies.add(contact);
        checkIfInvolveInWarWith(contact);
        contactCells.add(contact);
        this.contactsComponent.contactDetailsComponent.setContact(contact);
        this.contactsComponent.contactDetailsComponent.checkWarSituation();
        saveContacts();
    }

    public void addEnemyAfterDeclaration(String enemy) {
        Contact contact;
        ReadEmpireDto empireDto = islandsService.getEmpire(enemy);
        if (attacker(enemy) || defender(enemy)) {
            declaringToDefenderCheck(enemy);
            contact = new Contact(empireDto);
            contact.setMyOwnId(myOwnEmpireID);
            hiddenEmpires.remove(enemy);
            contact.setAtWarWith(true);
            contactCells.add(contact);
            seenEnemies.add(contact);
            saveContacts();
        }
    }

    public void checkIfInvolveInWarWith(Contact contact) {
        if (Objects.isNull(contact)) return;
        contact.setAtWarWith(attacker(contact.getEmpireID()) || defender(contact.getEmpireID()));
    }

    public void declaringToDefenderCheck(String attackID) {
        List<WarDto> warsFromAttacker = new ArrayList<>();
        for (WarDto warDto : warsInThisGame) {
            if (warDto.attacker().equals(attackID) || warDto.defender().equals(attackID)) {
                warsFromAttacker.add(warDto);
            }
        }
        if (warsFromAttacker.isEmpty()) {
            setDeclaringToDefender(true);
        } else {
            warsFromAttacker.sort(Comparator.comparing(WarDto::createAt).reversed());
            WarDto latestWar = warsFromAttacker.getFirst();
            setDeclaringToDefender(latestWar.defender().equals(tokenStorage.getEmpireId()));
        }
    }


    public boolean isDeclaring() {
        return declaring;
    }

    public void setDeclaring(boolean declaring) {
        this.declaring = declaring;
    }

    public String getAttacker() {
        return attacker;
    }

    public void setAttacker(String attacker) {
        this.attacker = attacker;
    }

    public boolean isDeclaringToDefender() {
        return declaringToDefender;
    }

    public void setDeclaringToDefender(boolean declaringToDefender) {
        this.declaringToDefender = declaringToDefender;

    }

    public void getEmpiresInGame() {
        contactsComponent.loadEmpireWars();
        this.gameID = tokenStorage.getGameId();
        this.myOwnEmpireID = tokenStorage.getEmpireId();
        this.hiddenEmpires = new ArrayList<>(islandsService.getEmpiresID());
        this.hiddenEmpires.remove(this.myOwnEmpireID);
        loadContactsData();
        createWarListener();
    }

    private Map<String, Object> mapContacts(Map<String, Object> map) {
        Map<String, Object> tmp = map.isEmpty() ? new HashMap<>() : map;
        for (Contact enemy : seenEnemies) {
            tmp.put(enemy.getEmpireID(), enemy.getDiscoveredIslands());
        }
        return tmp;
    }

    public void saveContacts() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.myOwnEmpireID),
                result -> {
                    final Map<String, Object> newPrivate = new HashMap<>(
                            mapContacts(Objects.nonNull(result._private()) ?
                                    result._private() : new HashMap<>()));
                    subscriber.subscribe(this.empireApiService.savePrivateContact(this.gameID, this.myOwnEmpireID, new EmpirePrivate(newPrivate)),
                            saved -> {
                            },
                            error -> System.out.println("error while saving contacts...."));
                },
                error -> System.out.println("error while saving contacts")
        );
    }

    public void loadContactsData() {
        this.subscriber.subscribe(this.empireApiService.getPrivate(this.gameID, this.myOwnEmpireID),
                result -> loadContacts(result._private()),
                error -> System.out.println("error while loading contacts"));
    }

    public void onWarDeleted(Consumer<WarDto> func) {
        this.onWarDeletedConsumer = func;
    }

    public void onWarCreated(Consumer<WarDto> func) {
        this.onWarCreatedRunnable = func;
    }

    public void loadContacts(Map<String, Object> map) {
        if (!map.isEmpty()) {
            Map<String, List<String>> tmp = new HashMap<>();
            for (String key : map.keySet()) {
                if (hiddenEmpires.contains(key) && map.get(key) instanceof List<?> value) {
                    tmp.put(key, value.isEmpty() ? new ArrayList<>() : (ArrayList<String>) value);
                }
            }
            recreateContacts(tmp);
        }
    }

    public void recreateContacts(Map<String, List<String>> data) {
        if (data.isEmpty()) return;
        for (String key : data.keySet()) {
            if (data.get(key).isEmpty()) this.addEnemyInPeace(key);
            else data.get(key).forEach(id -> this.addEnemy(key, id));
        }
    }

    public void createWarListener() {
        this.subscriber.subscribe(this.eventListener
                        .listen("games." + tokenStorage.getGameId() + ".wars.*.*", WarDto.class),
                event -> {
                    String attackerName = islandsService.getEmpire(event.data().attacker()).name();
                    switch (event.suffix()) {
                        case "created" -> {
                            warsInThisGame.add(event.data());
                            if (Objects.nonNull(this.onWarCreatedRunnable))
                                this.onWarCreatedRunnable.accept(event.data());
                            if (hiddenEmpires.contains(event.data().attacker())) {
                                addEnemyAfterDeclaration(event.data().attacker());
                            }
                        }
                        case "deleted" -> {
                            warsInThisGame.removeIf(w -> w._id().equals(event.data()._id()));
                            this.onWarDeletedConsumer.accept(event.data());
                        }
                        default -> { }
                    }

                    // search the corresponding contact in the list
                    Contact tmp = contactCells.stream()
                      .filter(element -> element.getEmpireID().equals(event.data().attacker()))
                      .findFirst().orElse(null);
                    if(Objects.nonNull(tmp)) this.contactsComponent.contactDetailsComponent.setContact(tmp);
                    this.contactsComponent.contactDetailsComponent.checkWarSituation();
                    this.contactsComponent.contactDetailsComponent.setWarMessagePopup(event.suffix(), attackerName, myOwnEmpireID, event.data());

                },
                Throwable::printStackTrace
        );
    }

    public void startWarWith(String enemyID) {
        CreateWarDto warDto = new CreateWarDto(myOwnEmpireID, enemyID, "");
        this.subscriber.subscribe(this.warService.createWar(gameID, warDto),
                result -> warsOnProcess.put(enemyID, result),
                error -> System.out.println("couldn't create war"));
    }

    public void stopWarWith(String enemyID) {
        if (Objects.isNull(warsOnProcess.get(enemyID))) return;
        String warId = warsOnProcess.get(enemyID)._id();
        this.subscriber.subscribe(this.warService.deleteWar(gameID, warId),
                result -> warsOnProcess.remove(enemyID),
                error -> System.out.println("couldn't stop war"));
    }

    public void setContactOverview(ContactsComponent contactsOverviewComponent) {
        this.contactsComponent = contactsOverviewComponent;
    }

    public boolean attacker(String empireID) {
        return warsInThisGame.stream()
                .anyMatch(warDto -> (myOwnEmpireID.equals(warDto.defender()) && empireID.equals(warDto.attacker())));
    }

    public boolean defender(String empireID) {
        return warsInThisGame.stream()
                .anyMatch(warDto -> (empireID.equals(warDto.defender()) && myOwnEmpireID.equals(warDto.attacker())));
    }


    public void addWarInformation(List<WarDto> dto) {
        this.warsInThisGame.addAll(dto);
        for (WarDto w : dto) {
            if (w.attacker().equals(myOwnEmpireID)) warsOnProcess.put(w.defender(),w);
            if (w.defender().equals(myOwnEmpireID)) warsOnProcess.put(w.attacker(),w);
        }
    }

    public boolean areAtWar(String empire1, String empire2) {
        for (WarDto warDto : warsInThisGame) {
            if (warDto.attacker().equals(empire1) && warDto.defender().equals(empire2) ||
                    warDto.attacker().equals(empire2) && warDto.defender().equals(empire1)) {
                return true;
            }
        }
        return false;
    }

}