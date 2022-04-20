package client;

import coordinator.Coordinator;
import task.Task;
import logging.Logger;

import java.io.Reader;
import java.net.MalformedURLException;
import java.nio.Buffer;
import java.rmi.Naming;
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
    }

    public ClientImpl(String clientName, String hostname, int port) throws RemoteException{
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
    public boolean runTask(Task task, Reader reader) throws RemoteException {
        this.status = State.BUSY; // maybe? maybe can do multiple units of work;
        task.setInputData(reader);
        task.run();
        this.status = State.IDLE;
        return task.isComplete();
    }
}
