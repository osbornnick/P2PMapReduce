package coordinator;

import client.Client;
import utility.Logger;

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
        this.connectedClients = new HashMap<String, Client>();
    }


    @Override
    public boolean login(String clientName) throws RemoteException {

        this.logger.printAndLog("Client with name: '" + clientName + " connected.");

        if ( !this.connectedClients.containsKey( clientName )) {
            try {
                Client newClient = (Client) Naming.lookup("rmi://localhost/" + clientName);
                this.connectedClients.put( clientName, newClient );
                this.logger.printAndLog("Clients now connected are: " + this.connectedClients.keySet());
            } catch (NotBoundException e) {
                // todo: make error messages better, log them
                e.printStackTrace();
                return false;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }


    @Override
    public boolean logout(String clientName) throws RemoteException {

        this.logger.printAndLog("Client with name: '" + clientName + " disconnected.");

        if ( this.connectedClients.containsKey( clientName )) {
            this.connectedClients.remove( clientName );
            this.logger.printAndLog("Clients now connected are: " + this.connectedClients.keySet());
            return true;
        }

        return false;
    }




    @Override
    public List<Client> availableWorkers(String clientName) throws RemoteException {
        List<Client> availableClients = new ArrayList<Client>();
        for ( Client c : connectedClients.values() ) {
            if ( !c.isBusy() ) {
                availableClients.add( c );
            }
        }
        return availableClients;
    }


}
