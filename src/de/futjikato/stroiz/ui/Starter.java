package de.futjikato.stroiz.ui;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.audio.Manager;
import de.futjikato.stroiz.client.tasks.ClientUserList;
import de.futjikato.stroiz.task.PacketProcessor;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Level;

public class Starter extends Application {

    private ListController listController;

    private Manager recorder;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResource("listview.fxml").openStream());
        listController = loader.getController();
        listController.setApplication(this);

        Scene scene = new Scene(root, root.minWidth(1), root.minHeight(1));
        scene.getStylesheets().add(Starter.class.getResource("listview.css").toExternalForm());
        ((Pane)scene.getRoot()).getChildren().add(getSystemMenu());

        primaryStage.setTitle("Stroiz");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);

        Invoker.getInstance().setApplication(this);

        recorder = new Manager();
        recorder.publishMixer();
    }

    public static void doLaunch() {
        ClientUserList clientUserManager = new ClientUserList();
        PacketProcessor.getInstance().addObserver(clientUserManager);

        launch();
    }

    private MenuBar getSystemMenu() {
        MenuBar menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(true);

        // Stroiz main menu
        Menu mainMenu = new Menu("Stroiz");
        MenuItem soundPrefMenuItem = new MenuItem("Sound preferences");
        soundPrefMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Starter.this.openSoundPreference();
            }
        });
        mainMenu.getItems().add(soundPrefMenuItem);
        menuBar.getMenus().add(mainMenu);

        return menuBar;
    }

    public void openSoundPreference() {
        try {
            FXMLLoader loader = new FXMLLoader();
            Parent root = loader.load(getClass().getResource("soundPref.fxml").openStream());
            SoundPreferenceController soundPrefController = loader.getController();
            soundPrefController.setApplication(this);

            Scene scene = new Scene(root, root.minWidth(1), root.minHeight(1));
            scene.getStylesheets().add(Starter.class.getResource("soundPref.css").toExternalForm());
        } catch (IOException e) {
            StroizLogger.getLogger().log(Level.SEVERE, "Unable to open sound preference window.", e);
        }
    }

    public ListController getController() {
        return listController;
    }

    protected Manager getManager() {
        return recorder;
    }
}