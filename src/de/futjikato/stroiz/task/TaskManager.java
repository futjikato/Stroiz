package de.futjikato.stroiz.task;

import java.util.concurrent.*;

/**
 * Task Manager
 * You can add tasks which will be processed in one of the background tasks
 */
public final class TaskManager extends Thread {

    private static TaskManager instance;

    private ExecutorService executor = Executors.newCachedThreadPool();

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        if(instance == null) {
            instance = new TaskManager();
        }

        return instance;
    }

    public <V> Future<V> queue(TaskInterface<V> task) {
        return executor.submit(task);
    }
}
