package Coordinator;

import Client.Client;

import java.rmi.RemoteException;
import java.util.List;

/**
 * Coordinator keeps a list of available clients,
 */
public interface Coordinator {
//    List<Client> clients;

    public List<Client> availableWorkers() throws RemoteException;
    public boolean login() throws RemoteException;
    public boolean logout() throws RemoteException;
}
