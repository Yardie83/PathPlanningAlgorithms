package ch.fhnw;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;

class FXController {

    private final FXView view;
    private final FXModel model;
    private SimulationMap map;

    FXController(FXModel model, FXView view) {
        this.view = view;
        this.model = model;

        init();
    }

    private void init() {
        //Map Options UI Controls and EventHandlers
        ToggleGroup mapTypeToggleGroup = view.getMapTypeToggleGroup();
        ChangeListener<Toggle> changeListener = (obs, toggle, t1) -> view.toggleMapTypeSelection();
        mapTypeToggleGroup.selectedToggleProperty().addListener(changeListener);

        //... New Map options
        Button generateOriginalMapsButton = view.getGenerateOriginalMapsButton();
        generateOriginalMapsButton.setOnAction(e -> handlerGenerateNewMapButtonAction());
        Button generateNewMapButton = view.getGenerateNewMapButton();
        generateNewMapButton.setOnAction(e -> handleGenerateButtonAction());
        Button clearAllMapFeaturesButton = view.getClearAllMapFeatureButton();
        clearAllMapFeaturesButton.setOnAction(e -> handleGenerateButtonAction());

        //Emotion Options UI Controls and EventHandlers
        Button selectAllEmotionButton = view.getSelectAllEmotionButton();
        selectAllEmotionButton.setOnAction(e -> handleSelectAllEmotionAction());

        Button clearAllEmotionButton = view.getClearAllEmotionButton();
        clearAllEmotionButton.setOnAction(e -> handleClearAllEmotionAction());

        // Simulation Options UI Controls and EventHandlers
        Button runButton = view.getRunButton();
        runButton.setOnAction(e -> handleRunButtonAction());

    }

    private void handlerGenerateNewMapButtonAction() {
        int mapIndex = view.getOriginalMapComboBox().getSelectionModel().getSelectedIndex();
        map = model.createOriginalMap(mapIndex);
        view.setSimulationMap(map);
        view.showSimulationMap();
    }

    private void handleSelectAllEmotionAction() {
        view.setAllEmotionCheckBox(true);
    }

    private void handleClearAllEmotionAction() {
        view.setAllEmotionCheckBox(false);
    }

    private void handleGenerateButtonAction() {
        view.setMapFeatureToggleGroupDisabled(false);
        int mapSize = view.getMapSizeSpinner().getValue();
        map = model.createNewMap(mapSize);
        view.setSimulationMap(map);
        view.showSimulationMap();
    }

    private void handleRunButtonAction() {

        boolean allowDiagonals = (boolean) view.getAllowDiagonalToggleGroup().getSelectedToggle().getUserData();
        int heuristic = (int) view.getHeuristicToggleGroup().getSelectedToggle().getUserData();

        Dijkstra dijkstra = new Dijkstra(map, 0, 0, allowDiagonals, heuristic);
        while (dijkstra.isRunning) {
            dijkstra.step();
        }
        ArrayList<Cell> shortestPath = dijkstra.getShortestPath();
        if (shortestPath != null) {
            shortestPath.forEach(cell -> {
                view.appendOutputText(cell.getCell_Index().i + " : " + cell.getCell_Index().j);
            });
            view.setPath(shortestPath);
        }

        view.drawPath();
    }

    void show() {
        view.show();
    }
}
