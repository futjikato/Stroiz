package de.futjikato.stroiz.audio;

import de.futjikato.stroiz.network.UdpReceiver;

import javax.sound.sampled.SourceDataLine;

public class Speaker extends Thread {

    private UdpReceiver receiver;

    private SourceDataLine sourceLine;

    public Speaker(UdpReceiver receiver, SourceDataLine sourceLine) {
        this.receiver = receiver;
        this.sourceLine = sourceLine;
    }

    public void run() {
        sourceLine.start();
        sourceLine.flush();

        while(!isInterrupted()) {
            byte[] buff = receiver.receive();
            sourceLine.write(buff, 0, buff.length);
        }

        sourceLine.stop();
        sourceLine.flush();
    }
}
