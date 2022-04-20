package coordinator;

import client.Client;
import logging.Logger;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
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
    public boolean login(String clientName, UnicastRemoteObject stub) throws RemoteException {

        this.logger.log("Client with name: '%s' connected", clientName);

        if ( !this.connectedClients.containsKey( clientName )) {
            this.connectedClients.put( clientName, (Client) stub );
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
    public List<Client> availableWorkers(String clientName) throws RemoteException {
        List<Client> availableClients = new ArrayList<>();
        for ( Client c : connectedClients.values() ) {
            if ( !c.isBusy() ) {
                availableClients.add( c );
            }
        }
        return availableClients;
    }

}
