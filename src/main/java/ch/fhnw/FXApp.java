package ch.fhnw;

import javafx.application.Application;
import javafx.stage.Stage;

public class FXApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        FXModel fxModel = new FXModel();
        FXView fxView = new FXView( stage, fxModel);
        FXController controller = new FXController(fxModel, fxView);
        controller.show();
    }
}
