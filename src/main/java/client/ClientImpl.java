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

/**
 * A basic client implementation, it publishes its stub on the RMI registry and becomes available for work.
 *   It can be also given a command line arguement --example, that runs the included MapReduce
 *   word count example.
 */
public class ClientImpl extends AbstractClient implements Worker, MapReducer {
    public Path computedData;

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
            Path examplePath = Path.of("./EXAMPLE.txt");
            try (InputStream is = Files.newInputStream(examplePath)) {
                Task map = new WCMapTask();
                Task reduce = new WCReduceTask();
                InputStream[] streams = {is};
                int reducers = 1;
                client.mapReduce(map, reduce, streams, reducers);
                client.logger.log("All done, exiting");
                System.exit(0);
            } catch (IOException e) {
                System.out.println("Can't find EXAMPLE.txt in this directory. Run this jar file from the same directory as EXAMPLE.txt");
                System.exit(1);
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
            logger.log("Can't communicate with coordinator. Shutting down.");
            cleanup();
            System.exit(1);
            return false;
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

    private void cleanup() {
        for (UUID uid : this.taskStorage.keySet()) {
            Path toDelete = this.taskStorage.remove(uid);
            try {
                Files.delete(toDelete);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public boolean taskCompleted(UUID workid) throws RemoteException {
        Path toDelete = this.taskStorage.remove(workid);
        try {
            Files.delete(toDelete);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
