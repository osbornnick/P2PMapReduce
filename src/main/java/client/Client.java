package client;

import task.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * On startup, login to the Coordinator.
 * Can request work to be scheduled
 * Can accept a task to work on it
 */
public interface Client extends Remote {

    boolean isBusy() throws RemoteException;

    /**
     * Schedule a map and reduce task with given tasks, data, and requested number of reduce workers
     * @param map the map task
     * @param reduce the reduce task
     * @param data paths to data files
     * @param reducers how many reducers the user would like
     * @return
     */
    boolean mapReduce(Task map, Task reduce, String[] data, int reducers);
}
