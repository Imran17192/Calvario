package de.uniks.stp24.game.islandOverview;

import de.uniks.stp24.dto.ExplainedVariableDTO;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestVariableExplanation extends VariableTestComponent{

    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        stage.getScene().getStylesheets().clear();
        initComponents();
        inGameController.rootPane.getStylesheets().clear();
        inGameController.overviewSitesComponent.imagePane.getStylesheets().clear();
        inGameController.overviewSitesComponent.islandFlag.getStylesheets().clear();
        inGameController.showOverview();
    }

    @Test
    public void testVariablesTree(){
        for (Map.Entry<String, Integer> entry : variablesPresets.entrySet()) {
            assertTrue(variableService.allVariables.contains(entry.getKey()));
        }

        for (Map.Entry<String, ExplainedVariableDTO> entry : variableService.data.entrySet()) {
            for(ExplainedVariableDTO explainedVariableDTO: explainedVariableDTOS){
                if(explainedVariableDTO.variable().equals(entry.getKey())){
                    assertEquals(entry.getValue(), explainedVariableDTO);
                }
            }
        }

        assertEquals("buildings", variableService.buildingsTree.getRoot().getKey());
        assertEquals(1, variableService.buildingsTree.getRoot().getChildren().size());
        assertEquals(3, variableService.buildingsTree.getRoot().getChildren().getFirst().getChildren().size());
        assertEquals(0, variableService.buildingsTree.getRoot().getChildren().getFirst().getChildren().getFirst().getChildren().size());
        assertEquals(1, variableService.buildingsTree.getRoot().getChildren().getFirst().getChildren().getLast().getChildren().size());
    }

    @Test
    public void variableDependencies(){

        verify(variableDependencyService, times(2)).createVariableDependencyUpgrades();
        verify(variableDependencyService, times(2)).createVariableDependencyBuildings();
        verify(variableDependencyService, times(2)).createVariableDependencyDistricts();

        assertEquals(1, islandAttributeStorage.buildingsAttributes.size());
        assertEquals("church", islandAttributeStorage.buildingsAttributes.getFirst().id());
        assertEquals(1.0, islandAttributeStorage.buildingsAttributes.getFirst().build_time());
        assertEquals(1, islandAttributeStorage.buildingsAttributes.getFirst().cost().size());
        assertEquals(1, islandAttributeStorage.buildingsAttributes.getFirst().upkeep().size());
        assertTrue(islandAttributeStorage.buildingsAttributes.getFirst().production().isEmpty());
        assertEquals(1, islandAttributeStorage.buildingsAttributes.getFirst().cost().get("energy"));
        assertEquals(1, islandAttributeStorage.buildingsAttributes.getFirst().upkeep().get("minerals"));
    }
}