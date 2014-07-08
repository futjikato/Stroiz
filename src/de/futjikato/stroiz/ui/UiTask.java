package de.futjikato.stroiz.ui;

public abstract class UiTask implements Runnable {

    protected Starter application;

    protected void setApplication(Starter application) {
        this.application = application;
    }
}
