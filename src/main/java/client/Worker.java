package client;

import com.healthmarketscience.rmiio.RemoteIterator;
import task.Task;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Worker extends Remote {

    /**
     * Run a task defined by task parameter on data supplied via readable
     * @param task to perform on data
     * @return true if success, false otherwise
     * @throws RemoteException if disconnects or fails
     */
    boolean runTask(Task task, RemoteIterator<String> remoteIterator, UUID workid) throws RemoteException;

    RemoteIterator<String> getComputedData(UUID workid) throws RemoteException;

    void taskCompleted(UUID workid) throws RemoteException;
}
