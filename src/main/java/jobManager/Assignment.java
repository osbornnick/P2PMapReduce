package jobManager;

import client.Worker;
import task.Task;

import java.util.UUID;

public class Assignment {

    Worker worker;
    String workerName;
    DataGetter dataGetter;
    Task task;
    UUID uid;

    boolean isComplete = false;

    Assignment(Worker w, String name, DataGetter dataGetter, Task task) {
        this.uid = UUID.randomUUID();
        worker = w;
        workerName = name;
        this.dataGetter = dataGetter;
        this.task = task;
    }

    @Override
    public String toString() {
        return String.format("worker_name:%s task_type:%s taskUID:%s", workerName, task.getType(), task.getUID());
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    public UUID getUID() {
        return this.uid;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Task)) return false;
        Assignment t = (Assignment) obj;
        return t.getUID().equals(this.getUID());
    }

    public static Assignment fromAssignment(Assignment that) {
        return new Assignment(that.worker, that.workerName, that.dataGetter, that.task);
    }

}
