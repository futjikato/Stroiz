package de.futjikato.stroiz.ui;

import javafx.application.Platform;

public final class Invoker {

    private static Invoker instance;

    private Starter application;

    private Invoker() {}

    public static Invoker getInstance() {
        if(instance == null) {
            instance = new Invoker();
        }

        return instance;
    }

    protected void setApplication(Starter application) {
        this.application = application;
    }

    public void invoke(UiTask runnable) {
        runnable.setApplication(application);
        Platform.runLater(runnable);
    }
}