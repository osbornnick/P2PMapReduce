package client;

import WordCountExample.WCMapTask;
import WordCountExample.WCReduceTask;
import com.healthmarketscience.rmiio.RemoteIterator;
import jobManager.JobManagerImpl;
import task.Task;
import util.RemoteFileIterator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.*;

public class ClientImpl extends AbstractClient implements Worker {
    public Path computedData;

    private final Map<UUID, Path> taskStorage;

    public static void main(String[] args) {
        // args: name hostname port
        if (args.length > 4 || args.length < 3) {
            System.out.println("Usage: java -jar <clientName> <hostname> <port> (--example)");
            System.exit(1);
        }
        String clientName = args[0];
        String hostname = args[1];
        int port = Integer.parseInt(args[2]);

        ClientImpl client = new ClientImpl(clientName, hostname, port);

        if (args.length == 4 && args[3].equals("--example")) {
            try (InputStream is = ClientImpl.class.getResourceAsStream("/EXAMPLE.txt")) {
                Task map = new WCMapTask();
                Task reduce = new WCReduceTask();
                InputStream[] streams = {is};
                client.mapReduce(map, reduce, streams, 1);
                client.logger.log("All done, exiting");
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientImpl(String clientName, String hostname, int port) {
        super(clientName, hostname, port);
        taskStorage = new HashMap<>();
    }

    @Override
    public boolean runTask(Task task, RemoteIterator<String> inputIterator, UUID workid) throws RemoteException {
        logger.log("Received run task request");
        this.setState(State.BUSY); // maybe? maybe can do multiple units of work;
        task.setInputData(inputIterator);
        computedData = Path.of(String.format("./temp-task-%s-%s-%s.csv", this.clientName, task.getType(), sub(workid)));
        try {
            Files.createFile(computedData);
            task.setOutputData(Files.newOutputStream(computedData));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        this.taskStorage.put(workid, computedData);
        logger.log("Running task: %s", task);
        task.run();
        this.setState(State.IDLE);
        return task.isComplete();
    }

    private String sub(Object o) {
        return o.toString().substring(0, 4);
    }

    @Override
    public RemoteIterator<String> getComputedData(UUID workid) throws RemoteException {
        try {
            return RemoteFileIterator.iterator(this.taskStorage.get(workid));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean mapReduce(Task map, Task reduce, InputStream[] dataStreams, int reducers) {
        logger.log("Requesting workers from coordinator");
        Map<String, Worker> workerMap;
        try {
            workerMap = this.coordinator.availableWorkers(this.clientName);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        logger.log("Workers received: %s", workerMap);
        logger.log("spawning job thread");
        Thread jobThread = new Thread(new JobManagerImpl(this.clientName, map, reduce, dataStreams, workerMap, reducers));
        jobThread.start();
        try {
            jobThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean taskCompleted(UUID workid) {
        Path toDelete = this.taskStorage.remove(workid);
        try {
            Files.delete(toDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
