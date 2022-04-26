package client;

import coordinator.Coordinator;
import task.Task;

import java.io.InputStream;
import java.rmi.RemoteException;

public class FailingClient extends AbstractClient {

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
        while (true) {
            // hehe
        }
    }

    @Override
    public boolean mapReduce(Task map, Task reduce, InputStream[] data, int reducers) throws RemoteException {
        return false;
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
//        while (true) {
//            // hehe
//        }
    }
}
