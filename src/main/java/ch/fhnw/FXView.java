package ch.fhnw;

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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;

class FXView {

    private final FXModel fxModel;
    private final Stage stage;
    private Button runButton;
    private Button generateNewMapButton;
    private final int HEIGHT = 800;
    private final int WIDTH = 800;
    private TextArea textArea;
    private CheckBox visualizeCheckbox;
    private Spinner<Integer> mapSizeSpinner;
    private ComboBox<String> originalMapComboBox;
    private ToggleButton addWallsToggleButton;
    private ToggleButton addCheckpointsToggleButton;
    private boolean useNewMap;
    private ToggleGroup mapFeaturesToggleGroup;
    private Button selectAllEmotionButton;
    private Button clearAllEmotionButton;
    private CheckBox noveltyCheckbox;
    private CheckBox happinessCheckbox;
    private CheckBox explorationCheckbox;
    private CheckBox confidenceCheckbox;
    private ToggleGroup mapTypeToggleGroup;
    private Button generateOriginalMapsButton;
    private SimulationMap map;
    private GridPane centerPane;
    private Button clearAllMapFeaturesButton;
    private ToggleGroup allowDiagonalToggleGroup;
    private ToggleGroup heuristicToggleGroup;

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
        addWallsToggleButton.getStyleClass().addAll("button-primary", "btn-green");
        addCheckpointsToggleButton = new ToggleButton("Add Checkpoints");
        addCheckpointsToggleButton.setDisable(true);
        addCheckpointsToggleButton.getStyleClass().addAll("button-primary", "btn-green");
        addWallsToggleButton.setToggleGroup(mapFeaturesToggleGroup);
        addCheckpointsToggleButton.setToggleGroup(mapFeaturesToggleGroup);
        clearAllMapFeaturesButton = new Button("Clear All");
        clearAllMapFeaturesButton.getStyleClass().addAll("button-primary");
        clearAllMapFeaturesButton.setDisable(true);

        Text wallsCheckPointInstructionsText = new Text("Left click/drag to add, right click/drag to delete");


        HBox originalMapButtonBox = new HBox(2, originalMapComboBox, generateOriginalMapsButton);
        HBox mapGenerationButtonBox = new HBox(2, mapSizeSpinner, generateNewMapButton);
        HBox mapFeatureControlBox = new HBox(2, addWallsToggleButton, addCheckpointsToggleButton, clearAllMapFeaturesButton);


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
        gridPane.add(mapFeatureControlBox, 1, 4);
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

        Text visualizeText = new Text("Visualize");

        Text stepdelayText = new Text("Step delay (ms)");

        ObservableList<String> algorithmOptions =
                FXCollections.observableArrayList(
                        "Dijkstra",
                        "A*",
                        "D*Lite",
                        "MPAA*"
                );
        ComboBox<String> algorithmComboBox = new ComboBox<>(algorithmOptions);
        algorithmComboBox.setValue("Dijkstra");

        Text allowDiagonalText = new Text("Allow diagonal?");

        RadioButton allowDiagonalRadioButton = new RadioButton("Yes");
        allowDiagonalRadioButton.setUserData(true);
        allowDiagonalRadioButton.setSelected(true);
        RadioButton disallowDigonalRadioButton = new RadioButton("No");
        disallowDigonalRadioButton.setUserData(false);
        allowDiagonalToggleGroup = new ToggleGroup();
        allowDiagonalRadioButton.setToggleGroup(allowDiagonalToggleGroup);
        disallowDigonalRadioButton.setToggleGroup(allowDiagonalToggleGroup);

        HBox toggleAllowDiagonalBox = new HBox(10, allowDiagonalRadioButton, disallowDigonalRadioButton);

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


        visualizeCheckbox = new CheckBox();

        Slider stepDelaySlider = new Slider(10, 50, 30);
        stepDelaySlider.setShowTickMarks(true);
        stepDelaySlider.setShowTickLabels(true);
        stepDelaySlider.setMajorTickUnit(10f);
        stepDelaySlider.setBlockIncrement(10f);
        stepDelaySlider.setMinorTickCount(0);
        stepDelaySlider.setSnapToTicks(true);

        runButton = new Button("Run");
        runButton.getStyleClass().add("button-primary");
        runButton.prefWidth(75);
        Button stepButton = new Button("Step");
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
        gridPane.add(heuristicText, 0,2);
        gridPane.add(toggleHeuristicBox, 1,2);
        gridPane.add(visualizeText, 0, 3);
        gridPane.add(visualizeCheckbox, 1, 3);
        gridPane.add(stepdelayText, 0, 4);
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

    ToggleGroup getAllowDiagonalToggleGroup() {
        return allowDiagonalToggleGroup;
    }

    ToggleGroup getHeuristicToggleGroup() {
        return heuristicToggleGroup;
    }

    Button getRunButton() {
        return runButton;
    }

    void appendOutputText(String text){
        textArea.appendText(text + "\n");
    }

    void setAllEmotionCheckBox(boolean selected) {
        noveltyCheckbox.setSelected(selected);
        happinessCheckbox.setSelected(selected);
        explorationCheckbox.setSelected(selected);
        confidenceCheckbox.setSelected(selected);
    }

    void setMap(SimulationMap map) {
        this.map = map;

    }

    void showSimulationMap() {
        if (map != null) {
            centerPane.getChildren().clear();
            double paneWidth = centerPane.getWidth();
            double paneHeight = centerPane.getHeight();
            double height = paneHeight / map.getGrid().size();
            double width = paneWidth / map.getGrid().size();

            for (int i = 0; i < map.getGrid().size(); i++) {
                for (int j = 0; j < map.getGrid().get(i).size(); j++) {
                    Label label = new Label(i + " " + j);
                    label.setPrefSize(height, width);
                    Pane pane = new Pane();
                    int row = i;
                    int col = j;
                    pane.getStyleClass().add("cell-style");
                    if (map.getGrid().get(row).get(col).isWall()) {
                        pane.getStyleClass().add("wall");
                    }
                    if (map.getGrid().get(row).get(col).isCheckPoint()) {
                        pane.getStyleClass().add("checkpoint");
                    }
                    pane.setMaxSize(height, width);
                    pane.setPrefSize(height, width);
                    pane.setOnDragDetected(e -> pane.startFullDrag());
                    EventHandler<MouseEvent> simulationMouseEventHandler = e -> handleSimulationMapMouseEvents(e, pane, row, col);
                    pane.setOnMouseDragEntered(simulationMouseEventHandler);
                    pane.setOnMouseClicked(simulationMouseEventHandler);
                    centerPane.add(pane, i, j);
                }
            }
        }
    }

    private void handleSimulationMapMouseEvents(MouseEvent e, Pane pane, int row, int col) {
        if (addWallsToggleButton.isSelected() || addCheckpointsToggleButton.isSelected()) {
            MouseButton mouseButton = e.getButton();
            if (mouseButton == MouseButton.PRIMARY) {
                if (addWallsToggleButton.isSelected()) {
                    map.getGrid().get(row).get(col).setWall(true);
                    map.getGrid().get(row).get(col).setCheckPoint(false);
                    pane.getStyleClass().removeAll("path");
                    pane.getStyleClass().add("wall");
                } else if (addCheckpointsToggleButton.isSelected()) {
                    map.getGrid().get(row).get(col).setWall(false);
                    map.getGrid().get(row).get(col).setCheckPoint(true);
                    pane.getStyleClass().removeAll("path");
                    pane.getStyleClass().add("checkpoint");
                }
            } else {
                map.getGrid().get(row).get(col).setWall(false);
                pane.getStyleClass().removeAll("wall");
                map.getGrid().get(row).get(col).setCheckPoint(false);
                pane.getStyleClass().removeAll("checkpoint");
            }
        }
    }

    void drawPath() {
        centerPane.getChildren().forEach(node -> {
            Pane pane = (Pane) node;
            pane.getStyleClass().removeAll("path");
        });

        map.getPath().forEach(cell -> {
            Pane pane =(Pane) getNodeFromGridPane(centerPane, cell.getCell_Index().i, cell.getCell_Index().j);
            if (pane !=null) {
                pane.getStyleClass().add("path");
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


