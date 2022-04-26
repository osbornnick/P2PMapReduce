package client;

import coordinator.Coordinator;
import util.Logger;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public abstract class AbstractClient implements Client {
    protected State status = State.IDLE;
    protected String clientName;
    protected Logger logger;
    protected Coordinator coordinator;

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
        }
        try {
            this.coordinator = (Coordinator) reg.lookup("coord");
            this.coordinator.login(clientName, stub);
        } catch (NotBoundException | RemoteException e) {
            e.printStackTrace();
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
