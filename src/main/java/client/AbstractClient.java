package client;

import coordinator.Coordinator;
import jobManager.JobManagerImpl;
import task.Task;
import util.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public abstract class AbstractClient implements Client {
    protected State status = State.IDLE;
    protected String clientName;
    protected Logger logger;
    protected Coordinator coordinator;
    protected HashMap<UUID, Path> taskStorage;

    AbstractClient(String clientName, String hostname, int port) {
        this.logger = new Logger(clientName);
        this.setState(State.IDLE);
        this.clientName = clientName;
        Registry reg = null;
        Client stub = null;
        try {
            reg = LocateRegistry.getRegistry(hostname, port);
            stub = (Client) UnicastRemoteObject.exportObject(this, 0);
        } catch (Exception e) {
            e.printStackTrace();
            logger.log("Failed to export self to rmi registry. Is there a registry running at %s:%d?", hostname, port);
            System.exit(1);
        }
        try {
            this.coordinator = (Coordinator) reg.lookup("coord");
            this.coordinator.login(clientName, stub);
        } catch (NotBoundException | RemoteException e) {
            logger.log("Failed to find coordinator registered on rmi registry %s:%d with name %s", hostname, port, "coord");
            logger.log("Exiting");
            System.exit(2);
        }
    }

    @Override
    public boolean isBusy() throws RemoteException {
        return this.status == State.BUSY;
    }



    @Override
    public String toString() {
        return String.format("[%s, %s]", clientName, status);
    }

    protected void setState(State state) {
        logger.log("Updating state to: %s", state);
        this.status = state;
    }

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }
}
