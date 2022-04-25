package coordinator;

import client.Client;
import client.Worker;
import util.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class CoordinatorImpl extends UnicastRemoteObject implements Coordinator {
    private String coordName;
    private Logger logger;
    private Map<String, Client> connectedClients;

    // todo poll and remove clients from connected clients map
    /**
     *
     * @param coordName
     */
    public CoordinatorImpl(String coordName, Logger logger) throws RemoteException {
        this.coordName = coordName;
        this.logger = logger;
        this.connectedClients = new HashMap<>();
    }


    @Override
    public boolean login(String clientName, Client stub) throws RemoteException {

        this.logger.log("Client with name: '%s' connected", clientName);

        if ( !this.connectedClients.containsKey( clientName )) {
            this.connectedClients.put( clientName, stub );
            this.logger.log("Clients now connected are: %s", this.connectedClients.keySet());
        }
        return true;
    }

    @Override
    public boolean logout(String clientName) throws RemoteException {

        this.logger.log("Client with name: '%s' disconnected", clientName);

        if ( this.connectedClients.containsKey( clientName )) {
            this.connectedClients.remove( clientName );
            this.logger.log("Clients now connected are: " + this.connectedClients.keySet());
            return true;
        }

        return false;
    }

    @Override
    public Map<String, Worker> availableWorkers(String clientName) throws RemoteException {
        Map<String, Worker> availableClients = new HashMap<>();
        for ( String cName : connectedClients.keySet() ) {
            Client c = connectedClients.get(cName);
            if ( !c.isBusy() ) {
                availableClients.put( cName, (Worker) c );
            }
        }
        return availableClients;
    }

}
