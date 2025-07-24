 package de.uniks.stp24;

 import de.uniks.stp24.component.menu.BubbleComponent;
 import de.uniks.stp24.component.menu.GangComponent;
 import de.uniks.stp24.component.menu.GangDeletionComponent;
 import de.uniks.stp24.component.menu.TraitComponent;
 import de.uniks.stp24.controllers.GangCreationController;
 import de.uniks.stp24.dto.EffectDto;
 import de.uniks.stp24.dto.MemberDto;
 import de.uniks.stp24.model.Gang;
 import de.uniks.stp24.model.Trait;
 import de.uniks.stp24.rest.PresetsApiService;
 import de.uniks.stp24.service.ImageCache;
 import de.uniks.stp24.service.SaveLoadService;
 import de.uniks.stp24.service.TokenStorage;
 import de.uniks.stp24.service.menu.LobbyService;
 import io.reactivex.rxjava3.core.Observable;
 import javafx.application.Platform;
 import javafx.collections.FXCollections;
 import javafx.collections.ObservableList;
 import javafx.scene.control.Label;
 import javafx.scene.control.ListView;
 import javafx.scene.control.TextArea;
 import javafx.scene.control.TextField;
 import javafx.scene.input.MouseButton;
 import javafx.stage.Stage;
 import org.fulib.fx.controller.Subscriber;
 import org.junit.jupiter.api.BeforeEach;
 import org.junit.jupiter.api.Test;
 import org.junit.jupiter.api.extension.ExtendWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.Spy;
 import org.mockito.junit.jupiter.MockitoExtension;

 import javax.inject.Provider;

 import static org.junit.jupiter.api.Assertions.*;
 import static org.mockito.Mockito.*;
 import static org.testfx.util.WaitForAsyncUtils.waitForFxEvents;

 @ExtendWith(MockitoExtension.class)
 public class GangCreationControllerTest extends ControllerTest {
     @Mock
     TokenStorage tokenStorage;

     @Spy
     BubbleComponent bubbleComponent;

     @Spy
     ImageCache imageCache;

     @Spy
     Subscriber subscriber;

     @Mock
     LobbyService lobbyService;

     @Mock
     PresetsApiService presetsApiService;

     final ObservableList<Gang> gangs = FXCollections.observableArrayList();
     final Gang gang = new Gang("Test Gang", 0, "", 0, "", "", "#000000", 0, null);
     ListView<Gang> gangsListView;
     ListView<Trait> allTraitsListView;
     ListView<Trait> confirmedTraitsListView;
     ListView<Trait> selectedTraitsListView;

     @Mock
     SaveLoadService saveLoadService;

     @Spy
     Provider<GangComponent> gangComponentProvider = new Provider(){
         @Override
         public GangComponent get() {
             return new GangComponent();
         }
     };

     @InjectMocks
     GangCreationController gangCreationController;
     @InjectMocks
     GangDeletionComponent gangDeletionComponent;

     @Spy
     Provider<TraitComponent> traitComponentProvider = new Provider(){
         @Override
         public TraitComponent get() {
             return new TraitComponent(gangCreationController, variablesResourceBundle, true, true);
         }
     };

     Trait aTrait;
     Trait bTrait;
     Trait cTrait;
     Trait dTrait;

     @Override
     public void start(Stage stage) throws Exception{
         super.start(stage);
         bubbleComponent.subscriber = this.subscriber;

         this.gangCreationController.gangDeletionComponent = this.gangDeletionComponent;
         gangs.add(gang);
         doReturn(gangs).when(saveLoadService).loadGangs();
         doReturn(null).when(this.imageCache).get(any());
         aTrait = new Trait("__dev__", new EffectDto[]{new EffectDto("buildings.exchange.build_time", 0, 0, 5)}, 5, null);
         String[] conflictsOfB = {"prepared"};
         bTrait = new Trait("unprepared", new EffectDto[]{new EffectDto("buildings.exchange.cost.minerals", 0, 9, 0)}, 1, conflictsOfB);
         String[] conflictsOfC = {"unprepared"};
         cTrait = new Trait("prepared", new EffectDto[]{new EffectDto("buildings.exchange.upkeep.energy", 0, 0, 3)}, 1, conflictsOfC);
         dTrait = new Trait("strong", new EffectDto[]{new EffectDto("buildings.exchange.upkeep.consumer_goods", 0, 0, 2)}, 3, null);
         doReturn(Observable.just(new Trait[]{aTrait, bTrait, cTrait, dTrait})).when(presetsApiService).getTraitsPreset();
         doReturn(Observable.just(new MemberDto(false, "", null, ""))).when(lobbyService).getMember(any(), any());
         app.show(this.gangCreationController);
     }

     @BeforeEach
     public void initListViews() {
         gangsListView = lookup("#gangsListView").query();
         allTraitsListView = lookup("#allTraitsListView").query();
         confirmedTraitsListView = lookup("#confirmedTraitsListView").query();
         selectedTraitsListView = lookup("#selectedTraitsListView").query();
     }

     @Test
     public void testCreatingNewGang() {
         doNothing().when(saveLoadService).saveGang(any());

         int gangNums = gangsListView.getItems().size();

         clickOn("#showCreationButton");
         waitForFxEvents();

         String gangName = "Ashkanian";

         clickOn("#gangNameText");
         waitForFxEvents();

         release(MouseButton.PRIMARY);
         waitForFxEvents();

         write(gangName);
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         release(MouseButton.PRIMARY);
         waitForFxEvents();

         write(gangDescription);
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

          assertEquals(gangNums + 1, gangsListView.getItems().size());
          Gang selectedGang = gangsListView.getItems().get(1);
          assertEquals(gangName, selectedGang.name());
          assertEquals(gangDescription, selectedGang.description());
          verify(saveLoadService).saveGang(any());
     }

     @Test
     public void testEditGang() {
         waitForFxEvents();

         clickOn("Test Gang");
         waitForFxEvents();

         clickOn("#editButton");
         waitForFxEvents();

         clickOn("#gangNameText");
         waitForFxEvents();

         Platform.runLater(() -> ((TextField) lookup("#gangNameText").query()).clear());

         waitForFxEvents();
         write("Ashkanian");
         waitForFxEvents();

         clickOn("#confirmButton");
         waitForFxEvents();

         Gang selectedGang;
         selectedGang = gangsListView.getItems().getFirst();
         assertEquals("Ashkanian", selectedGang.name());
         assertTrue(selectedGang.description().isEmpty());

         clickOn("Ashkanian");
         waitForFxEvents();

         clickOn("#editButton");
         waitForFxEvents();

         String gangDescription = "Ruled by King Ashkan";

         clickOn("#gangDescriptionText");
         waitForFxEvents();

         waitForFxEvents();

         write(gangDescription);
         waitForFxEvents();

         clickOn("#nextPortraitButton");
         waitForFxEvents();

         clickOn("#nextFlagButton");
         waitForFxEvents();

         clickOn("#nextColorButton");
         waitForFxEvents();

         clickOn("#confirmButton");
         waitForFxEvents();

         selectedGang = gangsListView.getItems().getFirst();
         assertEquals("Ashkanian", selectedGang.name());
         assertEquals(gangDescription, selectedGang.description());
         assertEquals(1, selectedGang.flagIndex());
         assertEquals(1, selectedGang.portraitIndex());
         assertEquals(1, selectedGang.colorIndex());

         clickOn("Ashkanian");
         waitForFxEvents();

         clickOn("#editButton");
         waitForFxEvents();

         clickOn("#lastFlagButton");
         waitForFxEvents();

         clickOn("#lastFlagButton");
         waitForFxEvents();

         clickOn("#lastPortraitButton");
         waitForFxEvents();

         clickOn("#lastPortraitButton");
         waitForFxEvents();

         clickOn("#lastColorButton");
         waitForFxEvents();

         clickOn("#lastColorButton");
         waitForFxEvents();

         // before edit
         selectedGang = gangsListView.getItems().getFirst();
         assertEquals(1, selectedGang.flagIndex());
         assertEquals(1, selectedGang.portraitIndex());
         assertEquals(1, selectedGang.colorIndex());

         clickOn("#confirmButton");
         waitForFxEvents();

         // after edit
         selectedGang = gangsListView.getItems().getFirst();
         assertEquals(16, selectedGang.flagIndex());
         assertEquals(16, selectedGang.portraitIndex());
         assertEquals(15, selectedGang.colorIndex());
     }

     @Test
     public void loadingGangsFromSave() {
         assertEquals( 1, gangsListView.getItems().size());
         Gang loadedGang = gangsListView.getItems().getFirst();
         assertEquals("Test Gang", loadedGang.name());
         assertEquals(0, loadedGang.flagIndex());
         assertEquals(0, loadedGang.portraitIndex());
         assertEquals(0, loadedGang.colorIndex());
     }

     @Test
     public void testRandomGenerationAndLocking() {
         ObservableList<Trait> confirmedTraits;

         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#lockColorButton");
         waitForFxEvents();

         clickOn("#lockPortraitButton");
         waitForFxEvents();

         clickOn("#lockFlagButton");
         waitForFxEvents();

         TextField name = lookup("#gangNameText").query();
         assertEquals("", name.getText());

         TextArea description = lookup("#gangDescriptionText").query();
         assertEquals("", description.getText());

         confirmedTraits = confirmedTraitsListView.getItems();
         assertTrue(confirmedTraits.isEmpty());

         clickOn("#randomizeButton");
         waitForFxEvents();

         String generatedName = name.getText();
         String generatedDescription = description.getText();

         assertNotNull(generatedName);
         assertNotNull(generatedDescription);
         assertFalse(confirmedTraits.isEmpty());

         clickOn("#lockNameButton");
         waitForFxEvents();

         clickOn("#lockDescriptionButton");
         waitForFxEvents();

         clickOn("#lockTraitsButton");
         waitForFxEvents();

         clickOn("#randomizeButton");
         waitForFxEvents();

         assertEquals(generatedName, name.getText());
         assertEquals(generatedDescription, description.getText());
         assertEquals(confirmedTraits, confirmedTraitsListView.getItems());

         clickOn("#createButton");
         waitForFxEvents();

         Gang selectedGang = gangsListView.getItems().get(1);
         assertEquals(0, selectedGang.flagIndex());
         assertEquals(0, selectedGang.portraitIndex());
         assertEquals(0, selectedGang.colorIndex());
         assertEquals(confirmedTraits, selectedGang.traits());
     }

     @Test
     public void testDeletingGang() {
         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#gangNameText");
         waitForFxEvents();

         Platform.runLater(() -> ((TextField) lookup("#gangNameText").query()).clear());

         waitForFxEvents();
         write("Ashkanian");
         waitForFxEvents();

         clickOn("#createButton");
         waitForFxEvents();

         int gangNums = gangsListView.getItems().size();

         clickOn("Ashkanian");
         waitForFxEvents();

         clickOn("#showDeletePaneButton");
         waitForFxEvents();

         clickOn("#deleteGangButton");
         waitForFxEvents();

         assertEquals(gangNums - 1, gangsListView.getItems().size());
     }

     @Test
     public void testTraitsRandom() {
         clickOn("#showCreationButton");
         waitForFxEvents();
         clickOn("#chooseTraitsButton");
         waitForFxEvents();
         clickOn("#randomizeButton");
         waitForFxEvents();
         clickOn("#traitsConfirmButton");
         waitForFxEvents();
         ObservableList<Trait> confirmedTraits = confirmedTraitsListView.getItems();
         if (confirmedTraits.contains(aTrait)) {
             assertEquals(1, confirmedTraits.size());
             // A and D together are over limit
             assertFalse(confirmedTraits.contains(dTrait));
         } else if (confirmedTraits.contains(bTrait)) {
             // B and C have conflict
             assertFalse(confirmedTraits.contains(cTrait));
         } else if (confirmedTraits.contains(cTrait)) {
             // B and C have conflict
             assertFalse(confirmedTraits.contains(bTrait));
         } else if (confirmedTraits.contains(dTrait)) {
             // A and D together are over limit
             assertFalse(confirmedTraits.contains(aTrait));
         }
     }

     @Test
     public void testTraits() {
         ListView<Trait> alltraits = lookup("#allTraitsListView").queryListView();
         double currentHeight = alltraits.getHeight();
         double newHeight = currentHeight * 2;

         alltraits.setStyle("-fx-pref-height: " + newHeight + "px;");

         TextArea captainText = lookup("#captainText").query();

         clickOn("#showCreationButton");
         waitForFxEvents();

         clickOn("#chooseTraitsButton");
         waitForFxEvents();

         clickOn("#strongButtonChoose");
         waitForFxEvents();
         ObservableList<Trait> selectedTraits = selectedTraitsListView.getItems();
         assertTrue(selectedTraits.contains(dTrait));

         clickOn("#preparedButtonChoose");
         waitForFxEvents();
         selectedTraits = selectedTraitsListView.getItems();
         assertTrue(selectedTraits.contains(dTrait) && selectedTraits.contains(cTrait));

         clickOn("#unpreparedButtonChoose");
         waitForFxEvents();
         assertEquals(resources.getString("pirate.empireScreen.conflict").replace("{conflict1}", '"' + variablesResourceBundle.getString(cTrait.id()) + '"').replace("{conflict2}", '"' + variablesResourceBundle.getString(bTrait.id()) + '"')
                 , captainText.getText());
         selectedTraits = selectedTraitsListView.getItems();
         assertFalse(selectedTraits.contains(bTrait));


         Platform.runLater(() -> allTraitsListView.getItems().remove(1));
         waitForFxEvents();
         clickOn("#__dev__ButtonChoose");
         waitForFxEvents();
         captainText = lookup("#captainText").query();
         /*
         assertEquals(esources.getString("pirate.empireScreen.scoreOverLimit").replace("{conflict1}", '"' + variablesResourceBundle.getString(cTrait.id()) + '"').replace("{conflict2}", '"' + variablesResourceBundle.getString(bTrait.id()) + '"')
                 , captainText.getText());

          */
         selectedTraits = selectedTraitsListView.getItems();
         assertFalse(selectedTraits.contains(aTrait));

         clickOn("#traitsConfirmButton");
         waitForFxEvents();
         ObservableList<Trait> confirmedTraits = confirmedTraitsListView.getItems();
         assertTrue(confirmedTraits.contains(cTrait) && confirmedTraits.contains(dTrait));
     }

     @Test
     public void testTraitInfos() {
         Label traitName;
         Label traitConflicts;
         Label traitEffects;

         ListView<Trait> alltraits = lookup("#allTraitsListView").queryListView();
         double currentHeight = alltraits.getHeight();
         double newHeight = currentHeight * 2;

         alltraits.setStyle("-fx-pref-height: " + newHeight + "px;");


         clickOn("#showCreationButton");
         waitForFxEvents();
         clickOn("#chooseTraitsButton");
         waitForFxEvents();

         // Trait A
         moveTo(variablesResourceBundle.getString(aTrait.id()));

         traitName = lookup("#traitInfoName").query();
         traitConflicts = lookup("#traitInfoConflicts").query();
         traitEffects = lookup("#traitInfoEffects").query();

         assertEquals(variablesResourceBundle.getString(aTrait.id()), traitName.getText());
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(aTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(bTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(cTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(dTrait.id())));
         assertTrue(traitEffects.getText().contains(variablesResourceBundle.getString(aTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(bTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(cTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(dTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains("+5"));

         // Trait B
         moveTo(variablesResourceBundle.getString(bTrait.id()));

         traitName = lookup("#traitInfoName").query();
         traitConflicts = lookup("#traitInfoConflicts").query();
         traitEffects = lookup("#traitInfoEffects").query();

         assertEquals(variablesResourceBundle.getString(bTrait.id()), traitName.getText());
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(aTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(bTrait.id())));
         assertTrue(traitConflicts.getText().contains(variablesResourceBundle.getString(cTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(dTrait.id())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(aTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains(variablesResourceBundle.getString(bTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(cTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(dTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains("x9"));

         // Trait C
         moveTo("#" + cTrait.id());

         traitName = lookup("#traitInfoName").query();
         traitConflicts = lookup("#traitInfoConflicts").query();
         traitEffects = lookup("#traitInfoEffects").query();

         assertEquals(variablesResourceBundle.getString(cTrait.id()), traitName.getText());
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(aTrait.id())));
         assertTrue(traitConflicts.getText().contains(variablesResourceBundle.getString(bTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(cTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(dTrait.id())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(aTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(bTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains(variablesResourceBundle.getString(cTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(dTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains("+3"));

         // Trait D
         moveTo(variablesResourceBundle.getString(dTrait.id()));

         traitName = lookup("#traitInfoName").query();
         traitConflicts = lookup("#traitInfoConflicts").query();
         traitEffects = lookup("#traitInfoEffects").query();

         assertEquals(variablesResourceBundle.getString(dTrait.id()), traitName.getText());
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(aTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(bTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(cTrait.id())));
         assertFalse(traitConflicts.getText().contains(variablesResourceBundle.getString(dTrait.id())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(aTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(bTrait.effects()[0].variable())));
         assertFalse(traitEffects.getText().contains(variablesResourceBundle.getString(cTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains(variablesResourceBundle.getString(dTrait.effects()[0].variable())));
         assertTrue(traitEffects.getText().contains("+2"));
     }

     @Test
     public void testUserIsSpectator() {
         assertTrue(gangCreationController.spectatorBox.isVisible());
     }

//     @Test
//     public void testUserEmpireIsShown() {
//         ArrayList<String> traits = new ArrayList<>();
//         traits.add(variablesResourceBundle.getString(aTrait.id()));
//         traits.add(variablesResourceBundle.getString(bTrait.id()));
//         traits.add(variablesResourceBundle.getString(cTrait.id()));
//         traits.add(variablesResourceBundle.getString(dTrait.id()));
//
//         Empire empire = new Empire("Ashkanian", "Ruled by King Ashkan!", "#000000", 0, 0, traits, "");
//         doReturn(Observable.just(new MemberDto(false, "", empire, ""))).when(this.lobbyService).getMember(any(), any());
//
//         waitForFxEvents();
//     }

//     @Test
//     public void testGoingBackToLobbyNoGang() {
//         doReturn(null).when(this.app).show(eq("/lobby"), any());
//         doReturn(Observable.just(new MemberDto(false,"1", null, "1"))).when(lobbyService).getMember(any(), any());
//         doReturn(Observable.just(new MemberDto(false, "1", null, "1"))).when(lobbyService).updateMember(null, "1", false, null);
//
//         clickOn("#backButton");
//         waitForFxEvents();
//
//         verify(lobbyService).updateMember(null, "1", false, null);
//     }
 }
