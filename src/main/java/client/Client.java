package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import task.Task;


/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client extends Remote {

    public boolean connectToCoord() throws RemoteException;

    public boolean isBusy() throws RemoteException;

    public Task work(Task task) throws RemoteException;
}
