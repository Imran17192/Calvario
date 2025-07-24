package de.uniks.stp24.service.game;

import de.uniks.stp24.controllers.InGameController;
import de.uniks.stp24.dto.ExplainedVariableDTO;
import de.uniks.stp24.rest.GameLogicApiService;
import de.uniks.stp24.service.InGameService;
import de.uniks.stp24.service.TokenStorage;
import de.uniks.stp24.service.VariablesTree;
import io.reactivex.rxjava3.core.Observable;
import org.fulib.fx.controller.Subscriber;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.*;

@Singleton
public class VariableService {
    @Inject
    public InGameService inGameService;
    @Inject
    public Subscriber subscriber;
    @Inject
    GameLogicApiService gameLogicApiService;
    @Inject
    TokenStorage tokenStorage;
    @Inject
    public TechnologyService technologyService;

    public InGameController inGameController;
    public final ArrayList<String> allVariables = new ArrayList<>();
    public final Map<String, ExplainedVariableDTO> data = new HashMap<>();
    public VariablesTree<ExplainedVariableDTO> buildingsTree;
    public VariablesTree<ExplainedVariableDTO> districtsTree;
    public VariablesTree<ExplainedVariableDTO> systemsTree;
    public VariablesTree<ExplainedVariableDTO> empireTree;
    public VariablesTree<ExplainedVariableDTO> resourcesTree;
    public VariablesTree<ExplainedVariableDTO> technologiesTree;
    public VariablesTree<ExplainedVariableDTO> shipTree;
    public ArrayList<Runnable> runnables = new ArrayList<>();


    @Inject
    public VariableService() {

    }

    public void dispose() {
        allVariables.clear();
        data.clear();
        buildingsTree = null;
        districtsTree = null;
        systemsTree = null;
        empireTree = null;
        resourcesTree = null;
        technologiesTree = null;
        shipTree = null;
    }

    /*
    Initialize all existing variables from Server and saves it into list.
     */
    public void initVariables(){
        this.subscriber.subscribe(inGameService.getVariablesPresets(),
                result -> {
                    for (Map.Entry<String, Integer> entry : result.entrySet()) {
                        allVariables.add(entry.getKey());
                    }
                    loadVariablesDataStructure();
                },
                error -> System.out.println("error in loading variable presets:\n" + error.getMessage()));
    }


    public void loadVariablesDataStructure() {
        if(this.tokenStorage.isSpectator()) return;
        subscriber.subscribe(getFirstHalfOfVariables(),
                firstHalf -> {
                    for (ExplainedVariableDTO explainedVariableDTO : firstHalf) {
                        data.put(explainedVariableDTO.variable(), explainedVariableDTO);
                    }
                    subscriber.subscribe(getSecondHalfOfVariables(),
                            secondHalf -> {
                                for (ExplainedVariableDTO explainedVariableDTO : secondHalf) {
                                    data.put(explainedVariableDTO.variable(), explainedVariableDTO);
                                }
                                createAllTrees();
                                runRunnables();
                            }, error -> System.out.println("error while loading second half of variables:\n" + error.getMessage()));
                }, error -> System.out.println("error while loading first half of variables:\n" + error.getMessage()));
    }

    /**
     * The two function are split because the server can't handle one request that saves all variables at once at the moment (v4.0.0).
     * If you want to get all variables, please nest the two functions in each other and save the results in the same list.
     */
    public Observable<ArrayList<ExplainedVariableDTO>> getFirstHalfOfVariables() {
        ArrayList<String> firstHalf = new ArrayList<>(allVariables.subList(0, allVariables.size() / 2));
        return gameLogicApiService.getVariablesExplanations(inGameController.tokenStorage.getEmpireId(), firstHalf);
    }

    public Observable<ArrayList<ExplainedVariableDTO>> getSecondHalfOfVariables() {
        ArrayList<String> secondHalf = new ArrayList<>(allVariables.subList(allVariables.size() / 2, allVariables.size()));
        return gameLogicApiService.getVariablesExplanations(inGameController.tokenStorage.getEmpireId(), secondHalf);
    }

    public void createAllTrees(){
        buildingsTree = new VariablesTree<>("buildings");
        districtsTree = new VariablesTree<>("districts");
        systemsTree = new VariablesTree<>("systems");
        empireTree = new VariablesTree<>("empire");
        technologiesTree = new VariablesTree<>("technologies");
        resourcesTree = new VariablesTree<>("resources");
        shipTree = new VariablesTree<>("ships");

        createTree(buildingsTree);
        createTree(districtsTree);
        createTree(systemsTree);
        createTree(empireTree);
        createTree(technologiesTree);
        createTree(resourcesTree);
        createTree(shipTree);
    }

    /*
    Logic for creating trees
     */

    public void createTree(VariablesTree<ExplainedVariableDTO> tree){
        for (Map.Entry<String, ExplainedVariableDTO> entry : data.entrySet()) {
            if(entry.getKey().contains(tree.getRoot().getKey())){
                String[] keysArray = entry.getKey().split("\\.");
                List<String> keys = new ArrayList<>();
                Collections.addAll(keys, keysArray);
                keys.removeFirst();
                createChildrenNodes(tree.getRoot(), keys, entry.getKey());
            }
        }
    }

    public void createChildrenNodes(VariablesTree.Node<ExplainedVariableDTO> currentNode, List<String> keys, String variable){
        if(!keys.isEmpty()){
            VariablesTree.Node<ExplainedVariableDTO> tmp = nodeExists(currentNode, keys.getFirst());
            if(tmp != null){
                keys.removeFirst();
                createChildrenNodes(tmp, keys, variable);
            } else {
                if(keys.size() == 1){
                    currentNode.addChild(new VariablesTree.Node<>(keys.getFirst(), data.get(variable)));
                    keys.removeFirst();
                } else {
                    VariablesTree.Node<ExplainedVariableDTO> newChild = new VariablesTree.Node<>(keys.getFirst());
                    currentNode.addChild(newChild);
                    keys.removeFirst();
                    createChildrenNodes(newChild, keys, variable);
                }
            }
        }
    }

    public VariablesTree.Node<ExplainedVariableDTO> nodeExists(VariablesTree.Node<ExplainedVariableDTO> currentNode, String key){
        for(VariablesTree.Node<ExplainedVariableDTO> node: currentNode.getChildren()){
            if(node.getKey().equals(key)){
                return node;
            }
        }
        return null;
    }

    public void setIngameController(InGameController inGameController) {
        this.inGameController = inGameController;
    }

    public Map<String, Double> convertVariablesToMap(ArrayList<ExplainedVariableDTO> variables) {
        Map<String, Double> variablesMap = new HashMap<>();
        for (ExplainedVariableDTO variableDTO : variables) {
            variablesMap.put(variableDTO.variable(), variableDTO.finalValue());
        }
        return variablesMap;
    }

    public void addRunnable(Runnable func) {
        runnables.add(func);
    }

    public void runRunnables() {

        runnables.forEach(Runnable::run);
    }
}
