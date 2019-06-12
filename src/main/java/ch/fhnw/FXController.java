package ch.fhnw;

import ch.fhnw.Pathfinder.Pathfinder;
import ch.fhnw.Pathfinder.PathfinderFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class FXController {

    private final FXView view;
    private final FXModel model;
    private Map map;
    private Pathfinder pathfinder;
    private Timeline fiveSecondsWonder;

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
        generateOriginalMapsButton.setOnAction(e -> handlerGenerateOriginalMapButtonAction());
        Button generateNewMapButton = view.getGenerateNewMapButton();
        generateNewMapButton.setOnAction(e -> handleGenerateNewMapButtonAction());
        Button clearAllMapFeaturesButton = view.getClearAllMapFeatureButton();
        clearAllMapFeaturesButton.setOnAction(e -> handleGenerateNewMapButtonAction());

        //Emotion Options UI Controls and EventHandlers
        Button selectAllEmotionButton = view.getSelectAllEmotionButton();
        selectAllEmotionButton.setOnAction(e -> handleSelectAllEmotionAction());

        Button clearAllEmotionButton = view.getClearAllEmotionButton();
        clearAllEmotionButton.setOnAction(e -> handleClearAllEmotionAction());

        // Simulation Options UI Controls and EventHandlers
        Button runButton = view.getRunButton();
        runButton.setOnAction(e -> handleRunButtonAction());
        Button stepButton = view.getStepButton();
        stepButton.setOnAction(e -> handleStep());

    }

    //  Generate a original map
    private void handlerGenerateOriginalMapButtonAction() {
        int mapIndex = view.getOriginalMapComboBox().getSelectionModel().getSelectedIndex();
        map = model.createOriginalMap(mapIndex);
        setMap();
        showMap();
        view.clearOutputText();
        view.getStepButton().setDisable(false);
    }

    //    Generate a new map
    private void handleGenerateNewMapButtonAction() {
        view.setMapFeatureToggleGroupDisabled(false);
        int mapSize = view.getMapSizeSpinner().getValue();
        map = model.createNewMap(mapSize);
        setMap();
        showMap();
        view.clearOutputText();
        view.getStepButton().setDisable(false);
    }

    private void setMap() {
        view.setMap(map);
    }

    private void showMap() {
        view.showMap();
    }

    private void handleSelectAllEmotionAction() {
        view.setAllEmotionCheckBox(true);
    }

    private void handleClearAllEmotionAction() {
        view.setAllEmotionCheckBox(false);
    }

    private boolean isSetup = false;

    private void handleRunButtonAction() {

        Platform.runLater(() -> {

            long stepDelay = (long) view.getStepDelaySlider().getValue();
            fiveSecondsWonder = new Timeline(new KeyFrame(Duration.millis(stepDelay), event -> {
                handleStep();
            }));

            fiveSecondsWonder.setCycleCount(Timeline.INDEFINITE);
            fiveSecondsWonder.play();

        });
    }

    private void simulationStop() {
        try {
            fiveSecondsWonder.stop();
        } catch (NullPointerException ex) {

        }
    }

    private void handleStep() {
        if (!isSetup) {
            view.clearOutputText();
            if (!simulationSetup()) {
                view.reset();
                simulationStop();
            }
        }
        if (pathfinder.isRunning) {
            pathfinder.step();
            view.updateMap();
            return;
        }
        drawShortestPath();
        simulationStop();
        isSetup = false;
    }

    private boolean simulationSetup() {
        //     Get all the user options (Emotion options, Simulation options)

//        Emotion options

//        Simulation options
        String algorithm = view.getAlgorithmComboBox().getSelectionModel().getSelectedItem();
        boolean allowDiagonals = (boolean) view.getAllowDiagonalToggleGroup().getSelectedToggle().getUserData();
        int heuristic = (int) view.getHeuristicToggleGroup().getSelectedToggle().getUserData();
        boolean visualize = view.getVisualizeCheckbox().isSelected();
        double stepDelay = view.getStepDelaySlider().getValue();

        pathfinder = PathfinderFactory.getPathfinder(algorithm);
        if (pathfinder != null) {
            pathfinder.setAllowDiagonals(allowDiagonals);
            pathfinder.setAllowCrossingCorners(true);
            pathfinder.setHeuristic(heuristic);
            if (map == null) {
                view.appendOutputText("Generate a map first" + "\n");
//                return;
            } else {
                pathfinder.setMap(map);
                pathfinder.init();
            }

            if (map.getGrid().stream().flatMap(List::stream).collect(Collectors.toList()).stream().filter(Cell::isStart).findAny().isEmpty()) {
                view.appendOutputText("Add a start point");
                return false;
            }
            if (map.getCheckPoints().isEmpty()) {
                view.appendOutputText("Add a checkpoint");
                return false;
            }
        }
        view.appendOutputText("-------------------------------------------------------------------------------\n");
        isSetup = true;
        return true;
    }


    private void drawShortestPath() {

        ArrayList<Cell> shortestPath = pathfinder.getLocalPath();
        if (shortestPath != null) {
            view.appendOutputText("[ShortestPath]" + "\n");
            shortestPath.forEach(cell -> view.appendOutputText(cell.getIndex().i + "," + cell.getIndex().j + "->"));
            view.appendOutputText("\n[Number of steps] " + (shortestPath.size() - 1));
            view.setPath(shortestPath);
            view.drawPath();
        } else {
            view.appendOutputText("No path found" + "\n");
        }
    }


    void show() {
        view.show();
    }
}
