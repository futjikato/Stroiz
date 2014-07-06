package de.futjikato.stroiz;

import de.futjikato.stroiz.task.TaskManager;
import de.futjikato.stroiz.ui.Starter;

public class ClientMain {
    public static void main(String[] args) {
        Starter.doLaunch();

        TaskManager taskManager = TaskManager.getInstance();
        taskManager.start();
    }
}
