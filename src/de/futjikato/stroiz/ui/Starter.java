package de.futjikato.stroiz.ui;

import de.futjikato.stroiz.task.tasks.ClientAuthTask;
import de.futjikato.stroiz.task.PacketProcessor;
import de.futjikato.stroiz.task.tasks.ListTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Starter extends Application {

    private ListController listController;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("listview.fxml").openStream());
        listController = loader.getController();
        primaryStage.setTitle("Stroiz");
        primaryStage.setScene(new Scene(root, root.minWidth(1), root.minHeight(1)));
        primaryStage.show();
        primaryStage.setResizable(false);

        Invoker.getInstance().setApplication(this);
    }

    public static void doLaunch() {
        PacketProcessor.getInstance().addObserver(new ClientAuthTask());
        PacketProcessor.getInstance().addObserver(new ListTask());

        launch();
    }

    public ListController getController() {
        return listController;
    }
}