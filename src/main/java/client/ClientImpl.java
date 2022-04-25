package client;

import WordCountExample.WCMapTask;
import WordCountExample.WCReduceTask;
import com.healthmarketscience.rmiio.RemoteIterator;
import coordinator.Coordinator;
import jobManager.JobManager;
import jobManager.JobManagerImpl;
import task.Task;
import util.Logger;
import util.RemoteFileIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class ClientImpl implements Client, Worker {
    private State status;
    private String clientName;
    private Logger logger;
    private Coordinator coordinator;
    public File computedData;

    private Map<Task, File> taskStorage;

    public static void main(String[] args) {
        // args: name hostname port
        if (args.length > 4 || args.length < 3) {
            System.out.println("no");
        }
        String clientName = args[0];
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);

        ClientImpl client = new ClientImpl(clientName, hostname, port);

        if (args.length == 4 && args[3].equals("--example")) {
            try (InputStream is = ClientImpl.class.getResourceAsStream("/EXAMPLE.txt")) {
                System.out.println(is);
                Task map = new WCMapTask();
                Task reduce = new WCReduceTask();
                InputStream[] streams = {is};
                client.mapReduce(map, reduce, streams, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientImpl(String clientName, String hostname, int port) {
        this.status = State.IDLE;
        this.clientName = clientName;
        this.logger = new Logger(clientName);
        Registry reg = null;
        Client stub = null;
        try {
            reg = LocateRegistry.getRegistry(hostname, port);
            stub = (Client) UnicastRemoteObject.exportObject(this, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            this.coordinator = (Coordinator) reg.lookup("coord");
            this.coordinator.login(clientName, stub);
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
        taskStorage = new HashMap<>();
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
        computedData = new File(String.format("./temp-task-%s-%s-%s.csv", this.clientName, task.getType(), task.getUID()));
        try {
            task.setOutputData(new FileOutputStream(computedData));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.taskStorage.put(task, computedData);
        task.run();
        this.status = State.IDLE;
        return task.isComplete();
    }

    @Override
    public RemoteIterator<String> getComputedData() throws RemoteException {
        try {
            return RemoteFileIterator.iterator(this.computedData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean mapReduce(Task map, Task reduce, InputStream[] dataStreams, int reducers) {
        Map<String, Worker> workerMap;
        try {
            workerMap = this.coordinator.availableWorkers(this.clientName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        Thread jobThread = new Thread(new JobManagerImpl(this.clientName, map, reduce, dataStreams, workerMap, reducers));
        jobThread.start();

        return true;
    }

    @Override
    public boolean taskCompleted(Task task) {
        File toDelete = this.taskStorage.remove(task);
        return toDelete.delete();
    }
}
