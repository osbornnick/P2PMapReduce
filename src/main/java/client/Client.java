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
     * Schedule a map and reduce task with given tasks, data, and requested number of reduce workers
     * @param map the map task
     * @param reduce the reduce task
     * @param data streams data files
     * @param reducers how many reducers the user would like
     * @return true once operation is complete.
     */
    boolean mapReduce(Task map, Task reduce, InputStream[] data, int reducers);

    /**
     * Heartbeat
     * @return true if alive, timeout otherwise
     * @throws RemoteException if rmi failure.
     */
    boolean isAlive() throws RemoteException;
}
