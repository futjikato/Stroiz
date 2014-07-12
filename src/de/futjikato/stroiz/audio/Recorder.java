package de.futjikato.stroiz.audio;

import de.futjikato.stroiz.StroizLogger;
import de.futjikato.stroiz.ui.Invoker;
import de.futjikato.stroiz.ui.UiTask;
import javafx.scene.control.ComboBox;

import javax.sound.sampled.*;
import java.util.logging.Level;

public class Recorder {

    private Mixer currentInMixer;

    private Mixer currentOutMixer;

    private TargetDataLine currentTargetLine;

    private SourceDataLine currentSourceLine;

    private Echo echoThread;

    public void publishMixer() {
        final Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        Invoker.getInstance().invoke(new UiTask() {
            @Override
            public void run() {
                ComboBox inSelect = application.getController().mixerInSelect;
                ComboBox outSelect = application.getController().mixerOutSelect;

                for(Mixer.Info info : mixerInfos) {
                    AudioFormat format = getAudioFormat();
                    DataLine.Info targetDataLineInfo = new DataLine.Info(TargetDataLine.class, format);
                    DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, format);

                    Mixer mixer = AudioSystem.getMixer(info);
                    if(mixer.isLineSupported(targetDataLineInfo)) {
                        inSelect.getItems().add(info.getName());
                    }
                    if(mixer.isLineSupported(sourceDataLineInfo)) {
                        outSelect.getItems().add(info.getName());
                    }
                }
            }
        });
    }

    private AudioFormat getAudioFormat(){
        float sampleRate = 8000.0F;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;

        return new AudioFormat(sampleRate,
                sampleSizeInBits,
                channels,
                signed,
                bigEndian);
    }

    public void setInMixerByName(String name) {
        // close open line
        if(currentTargetLine != null && currentTargetLine.isOpen()) {
            currentTargetLine.close();
        }

        // get new mixer and new line
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for(Mixer.Info info : mixerInfos) {
            if(info.getName().equals(name)) {
                currentInMixer = AudioSystem.getMixer(info);

                AudioFormat format = getAudioFormat();
                DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, format);

                try {
                    currentTargetLine = (TargetDataLine) currentInMixer.getLine(dataLineInfo);
                    currentTargetLine.open();
                } catch (LineUnavailableException e) {
                    StroizLogger.getLogger().log(Level.WARNING, "Target line unavailable.", e);
                }
            }
        }
    }

    public void setOutMixerByName(String name) {
        // close open line
        if(currentSourceLine != null && currentSourceLine.isOpen()) {
            currentSourceLine.close();
        }

        // get new mixer and new line
        Mixer.Info[] mixerInfos = AudioSystem.getMixerInfo();
        for(Mixer.Info info : mixerInfos) {
            if(info.getName().equals(name)) {
                currentOutMixer = AudioSystem.getMixer(info);

                AudioFormat format = getAudioFormat();
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, format);

                try {
                    currentSourceLine = (SourceDataLine) currentOutMixer.getLine(dataLineInfo);
                    currentSourceLine.open();
                } catch (LineUnavailableException e) {
                    StroizLogger.getLogger().log(Level.WARNING, "Source line unavailable.", e);
                }
            }
        }
    }

    public boolean isReady() {
        return (
                (currentTargetLine != null && currentTargetLine.isOpen()) &&
                (currentSourceLine != null && currentSourceLine.isOpen())
        );
    }

    public void echo() {
        if(echoThread == null) {
            System.out.println("start echo");
            echoThread = new Echo(currentTargetLine, currentSourceLine);
            echoThread.start();
        } else {
            System.out.println("stop echo");
            echoThread.interrupt();
            echoThread = null;
        }
    }
}
