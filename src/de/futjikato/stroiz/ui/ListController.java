package de.futjikato.stroiz.ui;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.client.ServerClient;
import de.futjikato.stroiz.ui.elements.MemberList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;

public class ListController implements Initializable {
    @FXML
    public TextField fieldHost;

    @FXML
    public TextField fieldPort;

    @FXML
    public TextField fieldUsername;

    @FXML
    public Button btnConnect;

    @FXML
    public TreeView<String> clientTree;

    private TreeItem<String> rootClientTreeItem;

    @FXML
    public Label serverLabel;

    @FXML
    public Label serverHeadline;

    @FXML
    public Label serverErrorLabel;

    private ServerClient selfClient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        rootClientTreeItem = new TreeItem<String>("Server");
        clientTree.setRoot(rootClientTreeItem);
    }

    @FXML
    public void onConnect(ActionEvent actionEvent) {
        String host = fieldHost.getText();
        int port = Integer.valueOf(fieldPort.getText());

        try {
            selfClient = new ServerClient(host, port);
            selfClient.start();

            selfClient.queryAuth(fieldUsername.getText());
        } catch (IOException e) {
            StroizLogger.getLogger().log(Level.WARNING, "Connection error", e);
            authError("Unable to connect to server.");
        }
    }

    public void authSucc(String server) {
        serverErrorLabel.setVisible(false);
        serverLabel.setText(server);
        serverLabel.setVisible(true);
        serverHeadline.setVisible(true);

        selfClient.queryMemberList();
    }

    public void authError(String message) {
        serverLabel.setVisible(false);
        serverHeadline.setVisible(false);
        serverErrorLabel.setText(message);
        serverErrorLabel.setVisible(true);
    }

    public TreeItem<String> getMemberListRoot() {
        return rootClientTreeItem;
    }
}
