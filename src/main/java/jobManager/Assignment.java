package jobManager;

import client.Worker;
import task.Task;

public class Assignment {

    Worker worker;
    String workerName;
    DataGetter dataGetter;
    Task task;

    boolean isComplete = false;

    Assignment(Worker w, String name, DataGetter dataGetter, Task task) {
        worker = w;
        workerName = name;
        this.dataGetter = dataGetter;
        this.task = task;
    }

    @Override
    public String toString() {
        return String.format("worker_name:%s task_type:%s taskUID:%s", workerName, task.getType(), task.getUID());
    }
}
