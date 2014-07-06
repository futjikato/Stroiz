package de.futjikato.stroiz.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("listview.fxml"));
        primaryStage.setTitle("Stroiz");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void doLaunch() {
        launch();
    }
}
