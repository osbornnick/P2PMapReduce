package client;

import com.healthmarketscience.rmiio.RemoteIterator;
import jobManager.JobManager;
import coordinator.Coordinator;
import jobManager.JobManagerImpl;
import task.Task;
import logging.Logger;

import java.io.InputStream;
import java.nio.Buffer;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client, Worker {
    private State status;
    private String clientName;
    private Logger logger;
    private Coordinator coordinator;
    public Buffer buffer;

    public static void main(String[] args) {
        // args: name hostname port
        if (args.length != 3) {
            System.out.println("no");
        }
        String clientName = args[0];
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);

        try {
            ClientImpl client = new ClientImpl(clientName, hostname, port);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public ClientImpl(String clientName, String hostname, int port) throws RemoteException {
        this.status = State.IDLE;
        this.clientName = clientName;
        this.logger = new Logger(clientName);
        Registry reg = null;
        UnicastRemoteObject stub = null;
        try {
            reg = LocateRegistry.getRegistry(hostname, port);
            stub = (UnicastRemoteObject) UnicastRemoteObject.exportObject(this, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.coordinator = (Coordinator) reg.lookup("coord");
            this.coordinator.login(clientName, stub);
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean isBusy() throws RemoteException {
        return this.status == State.BUSY;
    }

    @Override
    public boolean runTask(Task task, RemoteIterator<String> inputIterator) throws RemoteException {
        // maybe even a remote stream

        this.status = State.BUSY; // maybe? maybe can do multiple units of work;
        task.setInputData(inputIterator);
        task.run();
        this.status = State.IDLE;
        return task.isComplete();
    }

    @Override
    public boolean mapReduce(Task map, Task reduce, String[] dataFiles, int reducers) {
        // ping coordinator for a list of workers
        // give those workers to the job manager
        // split data into M iterators (where M is the number of map workers)
//        JobManager manager = new JobManagerImpl(Task map, Task reduce, String[] dataFiles, int reducers);

        return false;
    }
}
