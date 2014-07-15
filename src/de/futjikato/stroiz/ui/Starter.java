package de.futjikato.stroiz.ui;

import de.futjikato.stroiz.audio.Manager;
import de.futjikato.stroiz.client.ClientUserManager;
import de.futjikato.stroiz.client.RemoteClient;
import de.futjikato.stroiz.task.tasks.ClientAuthTask;
import de.futjikato.stroiz.task.PacketProcessor;
import de.futjikato.stroiz.task.tasks.ListTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.stage.Stage;

public class Starter extends Application {

    private ListController listController;

    private static ClientUserManager clientUserManager;

    private Manager recorder;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("listview.fxml").openStream());
        listController = loader.getController();
        listController.setApplication(this);

        Scene scene = new Scene(root, root.minWidth(1), root.minHeight(1));
        scene.getStylesheets().add(Starter.class.getResource("listview.css").toExternalForm());

        primaryStage.setTitle("Stroiz");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

        Invoker.getInstance().setApplication(this);

        recorder = new Manager();
        recorder.publishMixer();
    }

    public static void doLaunch() {
        clientUserManager = new ClientUserManager();

        PacketProcessor.getInstance().addObserver(new ClientAuthTask());
        PacketProcessor.getInstance().addObserver(new ListTask(clientUserManager));

        launch();
    }

    public ListController getController() {
        return listController;
    }

    public void updateUsers() {
        TreeItem<String> root = listController.getMemberListRoot();
        root.getChildren().clear();

        for(RemoteClient client : clientUserManager.getUsers()) {
            root.getChildren().add(new TreeItem<String>(client.getListName()));
        }
    }

    protected Manager getManager() {
        return recorder;
    }

    public ClientUserManager getClientUserManager() {
        return Starter.clientUserManager;
    }
}