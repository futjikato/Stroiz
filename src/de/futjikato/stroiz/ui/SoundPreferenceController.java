package de.futjikato.stroiz.ui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class SoundPreferenceController {

    @FXML
    public ComboBox mixerInSelect;

    @FXML
    public ComboBox mixerOutSelect;

    private Starter application;

    public void setApplication(Starter application) {
        this.application = application;
    }
}
