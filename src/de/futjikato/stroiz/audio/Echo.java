package de.futjikato.stroiz.audio;

import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.util.Arrays;

public class Echo extends Thread {

    private TargetDataLine targetLine;

    private SourceDataLine sourceLine;

    public Echo(TargetDataLine targetLine, SourceDataLine sourceLine) {
        this.targetLine = targetLine;
        this.sourceLine = sourceLine;
    }

    public void run() {
        byte[] buff = new byte[256];

        targetLine.start();
        sourceLine.start();

        while(!isInterrupted()) {

            targetLine.read(buff, 0, 256);
            System.out.println(String.format("read : %s", Arrays.toString(buff)));
            sourceLine.write(buff, 0, 256);

        }

        targetLine.stop();
        sourceLine.stop();
    }
}
