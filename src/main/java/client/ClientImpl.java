package client;

import coordinator.Coordinator;
import task.Task;
import utility.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImpl extends UnicastRemoteObject implements Client {
    private State status;
    private String clientName;
    private Logger logger;
    private Coordinator coord;

    public ClientImpl(String clientName, Logger logger) throws RemoteException {
        this.status = State.IDLE;
        this.clientName = clientName;
        this.logger = logger;
        this.coord = null;
    }


    // private helper to connect to the coordinator when this client starts up
    public boolean connectToCoord() {
        String coordName = "coord";
        try {
            this.coord = (Coordinator) Naming.lookup("rmi://" + "localhost" + "/" + coordName);
            this.logger.printAndLog("Connected to Coordinator with RMI name: " + coordName);
            this.coord.login( this.clientName );
            return true;
        } catch (MalformedURLException e) {
            this.logger.printAndLog("Error! Could not connect to: " + "rmi://" + "localhost" + "/" + coordName);
            this.logger.printAndLog("Error message: " + e.getMessage());
            e.printStackTrace();
        } catch (NotBoundException e) {
            this.logger.printAndLog("Error! Coordinator with name: " + coordName + " not bound!");
            this.logger.printAndLog("Error message: " + e.getMessage());
            e.printStackTrace();
        } catch (RemoteException e) {
            this.logger.printAndLog("Error! RMI failed");
            this.logger.printAndLog("Error message: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public boolean isBusy() throws RemoteException {
        return this.status == State.BUSY;
    }

    @Override
    public Task work(Task task) throws RemoteException {
        // todo:
        return null;
    }
}
