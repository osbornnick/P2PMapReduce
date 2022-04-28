package client;

import com.healthmarketscience.rmiio.RemoteIterator;
import task.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * An interface to represent a node that is available to do work, work being either map or reduce tasks.
 */
public interface Worker extends Remote {

    /**
     * Run a task defined by task parameter on data supplied via readable
     * @param task to perform on data
     * @return true if success, false otherwise
     * @throws RemoteException if disconnects or fails
     */
    boolean runTask(Task task, RemoteIterator<String> remoteIterator, UUID workid) throws RemoteException;

    /**
     * Return a remote iterator that reads the data from the previous completed piece of work.
     *
     * @param workid the unique ID of the work to read the data from
     * @return a remote iterator that reads data from the result of the given completed work
     * @throws RemoteException if remote communication fails
     */
    RemoteIterator<String> getComputedData(UUID workid) throws RemoteException;

    /**
     * Return true if the task with the given unique id is complete, false if otherwise
     * @param workid the unique id associated with the work to be checked
     * @return True if the task with the id is complete, false if otherwise
     * @throws RemoteException if remote communication fails
     */
    boolean taskCompleted(UUID workid) throws RemoteException;
}
