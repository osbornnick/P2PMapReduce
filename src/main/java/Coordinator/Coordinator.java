package Coordinator;

import Client.Client;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Coordinator keeps a list of available clients, and interface for clients to login and logout sessions.
 * Spawns an RMI registry
 */
public interface Coordinator {

    /**
     * Poll the list of registered clients, returning a list of those ready for work
     * @return list of clients available for scheduling work on
     * @throws RemoteException rmi
     */
    public List<Client> availableWorkers() throws RemoteException;

    /**
     * Clients can call this function to register with the Coordinator
     * @return true if successfully registered, false otherwise
     * @throws RemoteException rmi
     */
    public boolean login() throws RemoteException;

    /**
     * Clients can call this function to de-register with the Coordinator, to prevent further scheduling of work
     * @return true if successfully removed from worker pool, false otherwise
     * @throws RemoteException rmi
     */
    public boolean logout() throws RemoteException;
}
