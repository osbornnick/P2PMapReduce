package client;

import task.Task;

import java.io.Reader;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Worker extends Remote {

    /**
     * Run a task defined by task parameter on data supplied via readable
     * @param task to perform on data
     * @param reader to get data from and perform task on
     * @return true if success, false otherwise
     * @throws RemoteException if disconnects or fails
     */
    boolean runTask(Task task, Reader reader) throws RemoteException;
}
