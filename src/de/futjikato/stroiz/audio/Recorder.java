package de.futjikato.stroiz.audio;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;
import javafx.scene.control.ComboBox;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import java.util.logging.Level;

public class Recorder {

    private Mixer currentMixer;

    private Line currentLine;

    public void publishMixer() {
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        Invoker.getInstance().invoke(new UiTask() {
            @Override
            public void run() {
                ComboBox select = application.getController().mixerSelect;
                for(Mixer.Info info : mixerInfos) {
                    select.getItems().add(info.getName());
                }
            }
        });
    }

    public void setMixerByName(String name) {
        // close open line
        if(currentLine != null && currentLine.isOpen()) {
            currentLine.close();
        }

        // get new mixer and new line
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for(Mixer.Info info : mixerInfos) {
            if(info.getName().equals(name)) {
                currentMixer = AudioSystem.getMixer(info);
                Line[] lines = currentMixer.getTargetLines();
                // todo check how to decide with line to use
                currentLine = lines[0];

                try {
                    currentLine.open();
                } catch (LineUnavailableException e) {
                    StroizLogger.getLogger().log(Level.WARNING, "Unable to open line", e);
                }
            }
        }
    }
}
