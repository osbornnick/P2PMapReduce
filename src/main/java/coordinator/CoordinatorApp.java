package coordinator;


import utility.Logger;
import utility.Utils;

import java.io.IOException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * A coordinator to manage list of known clients
 */
public class CoordinatorApp {

    public static void main(String[] args) throws IOException {
        // todo: hardcoded for now;
        int portNum = 1099;

        // programatically open the registry in the coordinator app
        LocateRegistry.createRegistry( portNum );

        // create the coordinator and expose it in the registry
        // todo: could have this an input from user, if desired
        String coordName = "coord";
        Utils u = new Utils();
        Logger logger = new Logger(coordName, u);
        Coordinator coord = new CoordinatorImpl(coordName, logger);
        Naming.rebind(coordName, coord);
        logger.printAndLog("RMI Coordinator object named: " + coordName );
    }

}
