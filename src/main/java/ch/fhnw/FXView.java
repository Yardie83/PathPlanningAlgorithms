package ch.fhnw;

import ch.fhnw.util.Algorithm;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

class FXView {

    private final FXModel fxModel;
    private final Stage stage;
    private Button runButton;
    private Button generateNewMapButton;
    private TextArea textArea;
    private CheckBox visualizeCheckbox;
    private Spinner<Integer> mapSizeSpinner;
    private ComboBox<String> originalMapComboBox;
    private ToggleButton addWallsToggleButton;
    private ToggleButton addCheckpointsToggleButton;
    private ToggleGroup mapFeaturesToggleGroup;
    private Button selectAllEmotionButton;
    private Button clearAllEmotionButton;
    private CheckBox noveltyCheckbox;
    private CheckBox happinessCheckbox;
    private CheckBox explorationCheckbox;
    private CheckBox confidenceCheckbox;
    private ToggleGroup mapTypeToggleGroup;
    private Button generateOriginalMapsButton;
    private Map map;
    private GridPane centerPane;
    private Button clearAllMapFeaturesButton;
    private ToggleGroup allowDiagonalToggleGroup;
    private ToggleGroup heuristicToggleGroup;
    private ComboBox<String> algorithmComboBox;
    private Slider stepDelaySlider;
    private ToggleButton addStartPositionToggleButton;
    private final int HEIGHT = 600;
    private final int WIDTH = 600;
    private boolean useNewMap;
    private Button stepButton;
    private Label scoreLabel;

    FXView(Stage stage, FXModel fxModel) {
        this.stage = stage;
        this.fxModel = fxModel;
        this.useNewMap = false;
        createLayout();
    }

    private void createLayout() {
        Pane centerPane = createCenterPane();
        VBox rightPane = createRightPane();
        TextArea bottomPane = createBottomPane();

        BorderPane borderPane = new BorderPane(centerPane, null, rightPane, bottomPane, null);
        Scene scene = new Scene(borderPane);
        scene.getStylesheets().add(getClass().getResource("../../fxapp.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("OBI Path Planning");
    }

    private TextArea createBottomPane() {
        textArea = new TextArea();
        textArea.setEditable(false);
        return textArea;
    }

    private Pane createCenterPane() {
        centerPane = new GridPane();
        centerPane.setPrefSize(WIDTH, HEIGHT);
        centerPane.setMinSize(WIDTH, HEIGHT);
        return centerPane;
    }

    private VBox createRightPane() {
        VBox root = new VBox();
        root.setPrefHeight(HEIGHT);
        root.getStyleClass().add("rightPane");
        GridPane mapOptionsGrid = mapOptionsLayout();
        root.getChildren().add(new BorderedTitledPane(" Map", mapOptionsGrid));
        GridPane emotionOptionsGrid = emotionOptionsLayout();
        root.getChildren().add(new BorderedTitledPane(" Emotion Modifiers", emotionOptionsGrid));
        GridPane simulationOptionsGrid = simulationOptionsLayout();
        root.getChildren().add(new BorderedTitledPane(" Simulation", simulationOptionsGrid));

        return root;
    }

    private GridPane mapOptionsLayout() {

        Text mapTypeText = new Text("Map type:");

        mapTypeToggleGroup = new ToggleGroup();

        RadioButton predefinedMapsRadioButton = new RadioButton("Predefined");
        predefinedMapsRadioButton.setUserData(0);
        predefinedMapsRadioButton.setToggleGroup(mapTypeToggleGroup);
        predefinedMapsRadioButton.setSelected(true);
        RadioButton newMapRadioButton = new RadioButton("New");
        newMapRadioButton.setUserData(1);
        newMapRadioButton.setToggleGroup(mapTypeToggleGroup);

        HBox toggleButtonGroupBox = new HBox(10, predefinedMapsRadioButton, newMapRadioButton);

        Text originalMapsText = new Text("Original maps:");
        ObservableList<String> originalMapOptions =
                FXCollections.observableArrayList(
                        "Stairs",
                        "Wall"
                );
        originalMapComboBox = new ComboBox<>(originalMapOptions);
        originalMapComboBox.setValue("Stairs");
        generateOriginalMapsButton = new Button("Generate");
        generateOriginalMapsButton.getStyleClass().add("button-primary");

        Separator separator = new Separator();
        separator.setHalignment(HPos.LEFT);
        separator.setValignment(VPos.CENTER);


        Text sizeText = new Text("Map Size");

        ObservableList<Integer> mapSizes = FXCollections.observableArrayList(//
                16, 32, 64);
        mapSizeSpinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory = //
                new SpinnerValueFactory.ListSpinnerValueFactory<>(mapSizes);
        valueFactory.setValue(16);
        mapSizeSpinner.setValueFactory(valueFactory);
        mapSizeSpinner.getStyleClass().add(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        mapSizeSpinner.setPrefWidth(75);
        mapSizeSpinner.setDisable(true);

        generateNewMapButton = new Button("Generate");
        generateNewMapButton.getStyleClass().add("button-primary");
        generateNewMapButton.setDisable(true);

        mapFeaturesToggleGroup = new ToggleGroup();
        addWallsToggleButton = new ToggleButton("Add Walls");
        addWallsToggleButton.setDisable(true);
        addWallsToggleButton.setPrefWidth(100);
        addWallsToggleButton.getStyleClass().addAll("button-primary", "btn-green");
        addCheckpointsToggleButton = new ToggleButton("Add Checkpoints");
        addCheckpointsToggleButton.setDisable(true);
        addCheckpointsToggleButton.getStyleClass().addAll("button-primary", "btn-green");
        addWallsToggleButton.setToggleGroup(mapFeaturesToggleGroup);
        addCheckpointsToggleButton.setToggleGroup(mapFeaturesToggleGroup);
        addStartPositionToggleButton = new ToggleButton("Add Start point");
        addStartPositionToggleButton.setDisable(true);
        addStartPositionToggleButton.getStyleClass().addAll("button-primary", "btn-green");
        addStartPositionToggleButton.setToggleGroup(mapFeaturesToggleGroup);
        clearAllMapFeaturesButton = new Button("Clear All");
        clearAllMapFeaturesButton.getStyleClass().addAll("button-primary");
        clearAllMapFeaturesButton.setDisable(true);
        Text wallsCheckPointInstructionsText = new Text("Left click/drag to add, right click/drag to delete");


        HBox originalMapButtonBox = new HBox(2, originalMapComboBox, generateOriginalMapsButton);
        HBox mapGenerationButtonBox = new HBox(2, mapSizeSpinner, generateNewMapButton);
        GridPane mapFeatureControlGrid = new GridPane();
        mapFeatureControlGrid.setVgap(2);
        mapFeatureControlGrid.setHgap(2);
        mapFeatureControlGrid.add(addWallsToggleButton, 0, 0);
        mapFeatureControlGrid.add(addCheckpointsToggleButton, 1, 0);
        mapFeatureControlGrid.add(clearAllMapFeaturesButton, 1, 1);
        mapFeatureControlGrid.add(addStartPositionToggleButton, 0, 1);

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(10);
        gridPane.setHgap(10);

        gridPane.setAlignment(Pos.CENTER);

        gridPane.add(mapTypeText, 0, 0);
        gridPane.add(toggleButtonGroupBox, 1, 0);
        gridPane.add(originalMapsText, 0, 1);
        gridPane.add(originalMapButtonBox, 1, 1);
        gridPane.add(separator, 0, 2, 2, 1);
        gridPane.add(sizeText, 0, 3);
        gridPane.add(mapGenerationButtonBox, 1, 3);
        gridPane.add(mapFeatureControlGrid, 1, 4);
        gridPane.add(wallsCheckPointInstructionsText, 0, 5, 2, 1);

        return gridPane;
    }

    private GridPane emotionOptionsLayout() {

        Text noveltyText = new Text("Novelty");
        Text happinessText = new Text("Happy");
        Text explorationText = new Text("Exploration");
        Text confidenceText = new Text("Confidence");

        //Creating Text Filed for email
        noveltyCheckbox = new CheckBox();
        happinessCheckbox = new CheckBox();
        explorationCheckbox = new CheckBox();
        confidenceCheckbox = new CheckBox();
        selectAllEmotionButton = new Button("Select All");
        selectAllEmotionButton.getStyleClass().add("button-primary");
        clearAllEmotionButton = new Button("Clear All");
        clearAllEmotionButton.getStyleClass().add("button-primary");


        HBox emotionSelectClearButtonBox = new HBox(2, selectAllEmotionButton, clearAllEmotionButton);

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(noveltyText, 0, 0);
        gridPane.add(noveltyCheckbox, 1, 0);
        gridPane.add(happinessText, 0, 1);
        gridPane.add(happinessCheckbox, 1, 1);
        gridPane.add(explorationText, 0, 2);
        gridPane.add(explorationCheckbox, 1, 2);
        gridPane.add(confidenceText, 0, 3);
        gridPane.add(confidenceCheckbox, 1, 3);
        gridPane.add(emotionSelectClearButtonBox, 1, 4);
        return gridPane;
    }

    private GridPane simulationOptionsLayout() {

        Text algorithmText = new Text("Algorithm");
        ObservableList<String> algorithmOptions =
                FXCollections.observableArrayList(
                        Algorithm.DIJKSTRA.toString(),
                        Algorithm.A_STAR.toString(),
                        Algorithm.D_STAR_LITE.toString(),
                        Algorithm.MPAA.toString()
                );
        algorithmComboBox = new ComboBox<>(algorithmOptions);
        algorithmComboBox.setValue(Algorithm.DIJKSTRA.toString());

        Text allowDiagonalText = new Text("Allow diagonal?");
        RadioButton allowDiagonalRadioButton = new RadioButton("Yes");
        allowDiagonalRadioButton.setUserData(true);
        allowDiagonalRadioButton.setSelected(true);
        RadioButton disallowDiagonalRadioButton = new RadioButton("No");
        disallowDiagonalRadioButton.setUserData(false);
        allowDiagonalToggleGroup = new ToggleGroup();
        allowDiagonalRadioButton.setToggleGroup(allowDiagonalToggleGroup);
        disallowDiagonalRadioButton.setToggleGroup(allowDiagonalToggleGroup);
        HBox toggleAllowDiagonalBox = new HBox(10, allowDiagonalRadioButton, disallowDiagonalRadioButton);

        Text heuristicText = new Text("Distance Heuristic");
        RadioButton euclideanRadioButton = new RadioButton("Euclidean");
        euclideanRadioButton.setUserData(0);
        euclideanRadioButton.setSelected(true);
        RadioButton manhattanRadioButton = new RadioButton("Manhattan");
        manhattanRadioButton.setUserData(1);
        heuristicToggleGroup = new ToggleGroup();
        euclideanRadioButton.setToggleGroup(heuristicToggleGroup);
        manhattanRadioButton.setToggleGroup(heuristicToggleGroup);
        HBox toggleHeuristicBox = new HBox(10, euclideanRadioButton, manhattanRadioButton);

        Text visualizeText = new Text("Visualize");
        visualizeCheckbox = new CheckBox();

        Text stepDelayText = new Text("Step delay (ms)");
        stepDelaySlider = new Slider(100, 500, 300);
        stepDelaySlider.setShowTickMarks(true);
        stepDelaySlider.setShowTickLabels(true);
        stepDelaySlider.setMajorTickUnit(100f);
        stepDelaySlider.setBlockIncrement(100f);
        stepDelaySlider.setMinorTickCount(0);
        stepDelaySlider.setSnapToTicks(true);

        runButton = new Button("Run");
        runButton.getStyleClass().add("button-primary");
        runButton.prefWidth(75);
        stepButton = new Button("Step");
        stepButton.getStyleClass().addAll("button-primary", "btn-green");
        stepButton.prefWidth(75);
        HBox simulationControlButtonBox = new HBox(10, runButton, stepButton);

        //Creating a Grid Pane
        GridPane gridPane = new GridPane();

        //Setting the padding
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        //Setting the vertical and horizontal gaps between the columns
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        //Setting the Grid alignment
        gridPane.setAlignment(Pos.CENTER);

        //Arranging all the nodes in the grid
        gridPane.add(algorithmText, 0, 0);
        gridPane.add(algorithmComboBox, 1, 0);
        gridPane.add(allowDiagonalText, 0, 1);
        gridPane.add(toggleAllowDiagonalBox, 1, 1);
        gridPane.add(heuristicText, 0, 2);
        gridPane.add(toggleHeuristicBox, 1, 2);
        gridPane.add(visualizeText, 0, 3);
        gridPane.add(visualizeCheckbox, 1, 3);
        gridPane.add(stepDelayText, 0, 4);
        gridPane.add(stepDelaySlider, 1, 4);
        gridPane.add(simulationControlButtonBox, 1, 5);
        return gridPane;
    }

    void show() {
        stage.show();
    }

    void toggleMapTypeSelection() {
        useNewMap = !useNewMap;
        originalMapComboBox.setDisable(!originalMapComboBox.isDisabled());
        mapSizeSpinner.setDisable(!mapSizeSpinner.isDisabled());
        generateOriginalMapsButton.setDisable(!generateOriginalMapsButton.isDisabled());
        generateNewMapButton.setDisable(!generateNewMapButton.isDisabled());
        if (generateNewMapButton.isDisabled()) {
            setMapFeatureToggleGroupDisabled(true);
        }
    }

    void setMapFeatureToggleGroupDisabled(boolean disabled) {
        this.mapFeaturesToggleGroup.getToggles().forEach(toggle -> {
            ToggleButton toggleButton = (ToggleButton) toggle;
            toggleButton.setDisable(disabled);
            clearAllMapFeaturesButton.setDisable(disabled);
        });
    }

    ToggleGroup getMapTypeToggleGroup() {
        return mapTypeToggleGroup;
    }

    Button getGenerateOriginalMapsButton() {
        return generateOriginalMapsButton;
    }

    Button getGenerateNewMapButton() {
        return generateNewMapButton;
    }

    ComboBox<String> getOriginalMapComboBox() {
        return originalMapComboBox;
    }

    Button getClearAllMapFeatureButton() {
        return clearAllMapFeaturesButton;
    }

    Spinner<Integer> getMapSizeSpinner() {
        return mapSizeSpinner;
    }

    Button getSelectAllEmotionButton() {
        return selectAllEmotionButton;
    }

    Button getClearAllEmotionButton() {
        return clearAllEmotionButton;
    }

    ComboBox<String> getAlgorithmComboBox() {
        return algorithmComboBox;
    }

    ToggleGroup getAllowDiagonalToggleGroup() {
        return allowDiagonalToggleGroup;
    }

    ToggleGroup getHeuristicToggleGroup() {
        return heuristicToggleGroup;
    }

    CheckBox getVisualizeCheckbox() {
        return visualizeCheckbox;
    }

    Slider getStepDelaySlider() {
        return stepDelaySlider;
    }

    Button getRunButton() {
        return runButton;
    }

    Button getStepButton() {
        return stepButton;
    }

    void appendOutputText(String text) {
        textArea.appendText(text);
    }

    void clearOutputText() {
        textArea.setText("");
    }

    void setAllEmotionCheckBox(boolean selected) {
        noveltyCheckbox.setSelected(selected);
        happinessCheckbox.setSelected(selected);
        explorationCheckbox.setSelected(selected);
        confidenceCheckbox.setSelected(selected);
    }

    void setMap(Map map) {
        this.map = map;
    }

    void showMap() {
        if (map != null) {
            centerPane.getChildren().clear();
            double paneWidth = centerPane.getWidth();
            double paneHeight = centerPane.getHeight();
            double height = paneHeight / map.getGrid().size();
            double width = paneWidth / map.getGrid().size();

            for (int i = 0; i < map.getGrid().size(); i++) {
                for (int j = 0; j < map.getGrid().get(i).size(); j++) {
                    Label label = new Label();
                    label.setPrefSize(width, height);
                    label.setAlignment(Pos.CENTER);
                    Pane pane = new Pane(label);
                    pane.setMaxSize(width, height);
                    pane.setPrefSize(width, height);
                    int row = i;
                    int col = j;
                    pane.getStyleClass().add("cell-style");
                    if (map.getGrid().get(row).get(col).isWall()) {
                        pane.getStyleClass().add("wall");
                    }
                    if (map.getGrid().get(row).get(col).isCheckPoint()) {
                        pane.getStyleClass().add("checkpoint");
                    }
                    if (map.getGrid().get(row).get(col).isStart()) {
                        pane.getStyleClass().add("start");
                    }
                    pane.setOnDragDetected(e -> pane.startFullDrag());
                    EventHandler<MouseEvent> simulationMouseEventHandler = e -> handleSimulationMapMouseEvents(e, pane, row, col);
                    pane.setOnMouseDragEntered(simulationMouseEventHandler);
                    pane.setOnMouseClicked(simulationMouseEventHandler);
                    centerPane.add(pane, i, j);
                    centerPane.setAlignment(Pos.CENTER);
                }
            }
        }
    }

    private void handleSimulationMapMouseEvents(MouseEvent e, Pane pane, int row, int col) {
        if (addWallsToggleButton.isSelected() || addCheckpointsToggleButton.isSelected() || addStartPositionToggleButton.isSelected()) {
            Cell selectedCell = map.getGrid().get(row).get(col);
            MouseButton mouseButton = e.getButton();
            if (mouseButton == MouseButton.PRIMARY) {
                // Add Walls
                if (addWallsToggleButton.isSelected()) {
                    selectedCell.setWall(true);
                    selectedCell.setCheckPoint(false);
                    selectedCell.setStart(false);
                    pane.getStyleClass().removeAll("path", "start", "checkpoint", "visited");
                    pane.getStyleClass().add("wall");
                }

                // Add Checkpoints
                else if (addCheckpointsToggleButton.isSelected()) {
                    selectedCell.setCheckPoint(true);
                    selectedCell.setWall(false);
                    selectedCell.setStart(false);
                    pane.getStyleClass().removeAll("path", "wall", "start", "visited");
                    pane.getStyleClass().add("checkpoint");
                    map.getCheckPoints().add(selectedCell);
                }

                // Add Start Position , delete any current Start position set first
                else if (addStartPositionToggleButton.isSelected()) {
                    map.getGrid().forEach(cellArrayList -> cellArrayList.forEach(cell -> {
                        if (cell.isStart()) {
                            cell.setStart(false);
                            Pane currentStart = (Pane) getNodeFromGridPane(centerPane, (int) cell.getIndex().i, (int) cell.getIndex().j);
                            if (currentStart != null) {
                                currentStart.getStyleClass().removeAll("start");
                            }
                        }
                    }));
                    selectedCell.setStart(true);
                    selectedCell.setCheckPoint(false);
                    selectedCell.setWall(false);
                    pane.getStyleClass().removeAll("path", "wall", "checkpoint", "visited");
                    pane.getStyleClass().add("start");
                }
            }
            // On right-click remove whatever is there
            else {
                selectedCell.setWall(false);
                pane.getStyleClass().removeAll("wall");
                selectedCell.setCheckPoint(false);
                pane.getStyleClass().removeAll("checkpoint");
                selectedCell.setStart(false);
                map.getCheckPoints().remove(selectedCell);
                pane.getStyleClass().removeAll("start");
            }
        }
    }

    void drawPath() {
        centerPane.getChildren().forEach(node -> {
            Pane pane = (Pane) node;
            pane.getStyleClass().removeAll("path");
        });

        map.getPath().forEach(cell -> {
            if (cell.isVisited() && !cell.isWall() && !cell.isStart() && !cell.isCheckPoint()) {

                Pane pane = (Pane) getNodeFromGridPane(centerPane, (int) cell.getIndex().i, (int) cell.getIndex().j);
                if (pane != null) {
                    pane.getStyleClass().removeAll("visited");
                    pane.getStyleClass().add("path");
                }
            }
        });

    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    void setPath(ArrayList<Cell> shortestPath) {
        map.setPath(shortestPath);
    }

    void updateMap() {
        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            Pane currentPane = (Pane) getNodeFromGridPane(centerPane, (int) cell.getIndex().i, (int) cell.getIndex().j);
            if (currentPane != null) {
                if (currentPane.getChildren().size() > 0) {
                    Label label = (Label) currentPane.getChildren().get(0);
                    label.setText(String.valueOf(Math.round(map.getGrid().get((int) cell.getIndex().i).get((int) cell.getIndex().j).getF_score() * 100.0) / 100.0));
                    if (cell.isWall()) {
                        label.setText(String.valueOf('\u221e'));
                        label.getStyleClass().add("white");
                    }
                }
                if (cell.isVisited() && !cell.isWall() && !cell.isStart() && !cell.isCheckPoint()) {
                    currentPane.getStyleClass().add("visited");
                }
                currentPane.getStyleClass().removeAll("robot");
                if (cell.isRobotPosition()) {
                    currentPane.getStyleClass().add("robot");
                }
            }
        }));
    }

    void reset() {
        map.getGrid().forEach(cells -> cells.forEach(cell -> {
            Pane currentPane = (Pane) getNodeFromGridPane(centerPane, (int) cell.getIndex().i, (int) cell.getIndex().j);
            if (currentPane != null) {
                if (currentPane.getChildren().size() > 0) {
                    Label label = (Label) currentPane.getChildren().get(0);
                    label.setText("0");
                }
                if (!cell.isWall() && !cell.isStart()) {
                    currentPane.getStyleClass().removeAll("visited", "robot", "path");
                }
                if (cell.isCheckPoint()) currentPane.getStyleClass().add("checkpoint");
            }
        }));
    }
}

class BorderedTitledPane extends StackPane {
    BorderedTitledPane(String titleString, Node content) {
        Label title = new Label("  " + titleString + "  ");
        title.getStyleClass().add("bordered-titled-title");
        StackPane.setAlignment(title, Pos.TOP_CENTER);

        StackPane contentPane = new StackPane();
        content.getStyleClass().add("bordered-titled-content");
        contentPane.getChildren().add(content);

        getStyleClass().add("bordered-titled-border");
        getChildren().addAll(title, contentPane);
    }
}


