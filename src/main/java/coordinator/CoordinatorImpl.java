package coordinator;

import client.Client;
import client.Worker;
import util.Logger;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 */
public class CoordinatorImpl extends UnicastRemoteObject implements Coordinator {
    private String coordName;
    private final Logger logger;
    private final Map<String, Client> connectedClients;
    private Map<String, Timer> timers;

    // todo poll and remove clients from connected clients map

    /**
     * @param coordName
     */
    public CoordinatorImpl(String coordName, Logger logger) throws RemoteException {
        this.coordName = coordName;
        this.logger = logger;
        this.connectedClients = new HashMap<>();
        timers = new HashMap<>();
    }


    @Override
    public boolean login(String clientName, Client stub) throws RemoteException {

        this.logger.log("Client with name: '%s' connected", clientName);

        if (!this.connectedClients.containsKey(clientName)) {
            this.connectedClients.put(clientName, stub);
            this.logger.log("Starting timer for client %s", clientName);
            this.startTimer(clientName, stub);
            this.logger.log("Clients now connected are: %s", this.connectedClients.keySet());
        }
        return true;
    }

    @Override
    public boolean logout(String clientName) throws RemoteException {

        this.logger.log("Client with name: '%s' requesting log out", clientName);
        return this.localLogout(clientName);
    }

    private boolean localLogout(String clientName) {
        this.logger.log("Logging out client %s", clientName);
        Timer t = timers.remove(clientName);
        if (t != null) t.cancel();
        if (this.connectedClients.containsKey(clientName)) {
            this.connectedClients.remove(clientName);
            this.logger.log("Clients now connected are: " + this.connectedClients.keySet());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Worker> availableWorkers(String clientName) throws RemoteException {
        logger.log("%s requesting available workers", clientName);
        Map<String, Worker> availableClients = new HashMap<>();
        for (String cName : connectedClients.keySet()) {
            Client c = connectedClients.get(cName);
            logger.log("Asking %s if busy", cName);
            if (!timeout(cName, c::isBusy)) {
                availableClients.put(cName, (Worker) c);
            }
        }
        logger.log("Available workers are: %s", availableClients.keySet());
        return availableClients;
    }

    private void startTimer(String clientName, Client client) {
        Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.log("Is %s alive?", clientName);
                boolean result = timeout(clientName, client::isAlive);
                logger.log("%s is alive: %s", clientName, result);
            }
        }, 2000, 2000);
        timers.put(clientName, t);
    }

    private interface Timeoutable {
        boolean execute() throws RemoteException;
    }

    private boolean timeout(String clientName, Timeoutable timeoutable) {
        Future<Boolean> future = CompletableFuture.supplyAsync(() -> {
            try {
                return timeoutable.execute();
            } catch (RemoteException e) {
                localLogout(clientName);
                return false;
            }
        });
        boolean result;
        try {
            result = future.get(2, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            future.cancel(true);
            localLogout(clientName);
            result = false;
        }
        return result;
    }

}
