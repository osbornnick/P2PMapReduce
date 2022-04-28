package client;

import com.healthmarketscience.rmiio.RemoteIterator;
import coordinator.Coordinator;
import task.Task;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.UUID;

/**
 * A client that is implemented to wait infinitely when given a task. This allows for demonstration of the fault
 *   tolerance through reassignment of work.
 */
public class FailingClient extends AbstractClient implements Worker {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar <hostname> <port>");
            System.exit(1);
        }
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        Client failer = new FailingClient(hostname, port);
    }

    Coordinator coordinator;

    FailingClient(String hostname, int port) {
        super("failing client", hostname, port);
//        try {
//            this.coordinator.availableWorkers(clientName);
//        } catch (RemoteException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public boolean isBusy() throws RemoteException {
//        while (true) {}
        return false;
    }

    @Override
    public boolean mapReduce(Task map, Task reduce, InputStream[] data, int reducers) {
        return false;
    }

    @Override
    public boolean isAlive() throws RemoteException {
//        while (true) {}
        return true;
    }

    @Override
    public boolean runTask(Task task, RemoteIterator<String> remoteIterator, UUID workid) throws RemoteException {
        logger.log("Received task request %s", task);
        int i = 0;
        try {
            while (remoteIterator.hasNext() && i < 100) {
                remoteIterator.next();
                i++;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.log("waiting forever...");
        while (true) {}
    }

    @Override
    public RemoteIterator<String> getComputedData(UUID workid) throws RemoteException {
        return null;
    }

    @Override
    public boolean taskCompleted(UUID workid) throws RemoteException {
        logger.log("Received task complete message");
        logger.log("waiting forever...");
        while (true) {}
    }
}
