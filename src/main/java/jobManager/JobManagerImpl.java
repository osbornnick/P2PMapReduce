
package jobManager;

import client.Worker;
import task.Task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;
import com.healthmarketscience.rmiio.RemoteInputStream;
import com.healthmarketscience.rmiio.SimpleRemoteInputStream;

public class JobManagerImpl {
    Task map;
    Task reduce;
    List<Reader> data;
    List<Worker> workers;
    int reducers;

    JobManagerImpl(Task map, Task reduce, String[] localDataLocations, List<Worker> workers, int reducers) {
        this.map = map;
        this.reduce = reduce;
        this.workers = workers;
        this.reducers = reducers;
        this.openFiles(localDataLocations);
    }

    private void openFiles(String[] files) {
        String f = files[0];
        try {
            RemoteInputStream ris = new SimpleRemoteInputStream(new FileInputStream(f));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
