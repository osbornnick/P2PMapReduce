package client;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client extends Remote {

    public boolean isBusy() throws RemoteException;
}
