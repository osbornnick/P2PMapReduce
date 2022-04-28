package client;

import task.Task;
import util.RemoteFileIterator;

import java.io.InputStream;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client extends Remote {

    /**
     * Determine if the client is busy.
     * @return true if client is busy, false otherwise
     * @throws RemoteException if rmi failure.
     */
    boolean isBusy() throws RemoteException;

    /**
     * Heartbeat
     * @return true if alive, timeout otherwise
     * @throws RemoteException if rmi failure.
     */
    boolean isAlive() throws RemoteException;
}
