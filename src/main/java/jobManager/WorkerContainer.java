package jobManager;

import client.Worker;

import java.util.Objects;

public class WorkerContainer {
    Worker worker;
    String workerName;

    WorkerContainer(String s, Worker w) {
        workerName = s;
        worker = w;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof WorkerContainer)) return false;
        WorkerContainer t = (WorkerContainer) obj;
        return Objects.equals(t.workerName, this.workerName);
    }

    @Override
    public int hashCode() {
        return workerName.hashCode();
    }

    @Override
    public String toString() {
        return workerName;
    }
}