package client;

import utility.Logger;
import utility.Utils;

import java.io.IOException;
import java.rmi.Naming;

public class ClientApp {


    public static void main(String[] args) throws IOException {

        // need at least 1 cmd line argument for the client name / id
        if ( args.length < 1 ) {
            System.out.println("User must give a name to this client");
            System.exit(1);
        }

        // create the client and expose it in the registry
        String clientName = args[0];
        Utils u = new Utils();
        Logger logger = new Logger(clientName, u);
        Client client = new ClientImpl(clientName, logger);
        Naming.rebind(clientName, client);

        // this is jank, doing this after the rebind call so that the coordinator can find it in the registry
        //   but it creates another public method
        //   this prolly means I should refactor some methods so only some are remote exposed hmm, that'll be a todo:
        client.connectToCoord();

        logger.printAndLog("RMI Client object named: " + clientName );
    }


}
