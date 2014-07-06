package de.futjikato.stroiz.ui;

import de.futjikato.stroiz.task.PacketProcessor;
import de.futjikato.stroiz.ui.elements.MemberList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.util.ResourceBundle;

public class ListController implements Initializable {
    public TextField fieldHost;
    public TextField fieldPort;
    public Button btnConnect;
    public TreeView clientTree;

    private MemberList memberList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        memberList = new MemberList();

    }

    public void onConnect(ActionEvent actionEvent) {

    }
}
