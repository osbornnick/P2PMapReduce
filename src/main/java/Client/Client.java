package Client;

import java.rmi.RemoteException;
import Task.Task;
import Task.Result;

/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client {
    public boolean isBusy() throws RemoteException;

    public Result work(Task task) throws RemoteException;
}
