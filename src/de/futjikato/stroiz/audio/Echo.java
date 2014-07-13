package de.futjikato.stroiz.audio;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class Echo extends Thread {

    private static final int BUFF_SIZE = 16;

    private TargetDataLine targetLine;

    private SourceDataLine sourceLine;

    public Echo(TargetDataLine targetLine, SourceDataLine sourceLine) {
        this.targetLine = targetLine;
        this.sourceLine = sourceLine;
    }

    public void run() {
        byte[] buff = new byte[128];

        targetLine.start();
        targetLine.flush();

        sourceLine.start();
        sourceLine.flush();

        while(!isInterrupted()) {

            targetLine.read(buff, 0, BUFF_SIZE);
            sourceLine.write(buff, 0, BUFF_SIZE);

        }
    }

    public void stopLines() {
        targetLine.stop();
        targetLine.flush();

        sourceLine.stop();
        sourceLine.flush();
    }
}
