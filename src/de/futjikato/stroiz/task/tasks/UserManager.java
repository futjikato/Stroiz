package de.futjikato.stroiz.task.tasks;

import java.util.List;

/**
 * Created by moritz on 10.07.14.
 */
public interface UserManager<C> {

    public List<C> getUsers();

    public void register(C client);
}
