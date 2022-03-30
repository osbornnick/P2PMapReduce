package Client;

import java.rmi.RemoteException;
import Task.Task;


/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client {
    public boolean isBusy() throws RemoteException;

    public Task work(Task task) throws RemoteException;
}
