package client;

import coordinator.Coordinator;
import task.Task;

import java.io.InputStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class FailingClient implements Client {

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
        String clientName = "failing client";
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