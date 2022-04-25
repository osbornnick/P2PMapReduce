package jobManager;

import client.Worker;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public class ReduceAssignment extends Assignment {

    List<Worker> stubs = new ArrayList<>();
    ReduceAssignment(Worker w, String name, Task task, Worker stub) {
        super(w, name, null, task);
    }

    public void addPeer(Worker stub) {
        stubs.add(stub);
    }
}
