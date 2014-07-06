package de.futjikato.stroiz.task;

import java.util.concurrent.Future;

public abstract class AbstractTask<T> implements TaskInterface<T> {

    protected Future<T> future;

    protected void work() {
        future = TaskManager.getInstance().queue(this);
    }

}
