package jobManager;

import client.Worker;
import com.healthmarketscience.rmiio.RemoteIterator;
import task.Task;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Assignment implements Runnable {

    List<WorkerContainer> history;
    WorkerContainer workerContainer;
    DataGetter dataGetter;
    Task task;
    UUID uid;
    ExecutorService timerExecutor;

    boolean isComplete = false;
    boolean needsReassign = false;

    Assignment(WorkerContainer wc, DataGetter dataGetter, Task task, ExecutorService timerExecutor) {
        this.uid = UUID.randomUUID();
        this.workerContainer = wc;
        this.dataGetter = dataGetter;
        this.task = task;
        this.timerExecutor = timerExecutor;
        this.history = new ArrayList<>();
        this.history.add(wc);
    }

    public void reassign(WorkerContainer wc) {
        this.workerContainer = wc;
        this.history.add(wc);
        needsReassign = false;
    }

    @Override
    public String toString() {
        return String.format("worker_name:%s task_type:%s taskUID:%s", workerContainer.workerName, task.getType(), task.getUID());
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

    @Override
    public void run() {
        Future<Boolean> future = timerExecutor.submit(() -> workerContainer.worker.runTask(task, dataGetter.get(), getUID()));
        try {
            future.get(10, TimeUnit.SECONDS);
            isComplete = true;
        } catch (Exception e) {
            // failed for some reason
            future.cancel(true);
            needsReassign = true;
        }
    }

    public RemoteIterator<String> getComputedData() throws RemoteException {
        return workerContainer.worker.getComputedData(this.uid);
    }

    public void cleanup() throws RemoteException {
        for (WorkerContainer wc : this.history) {
            Future<Boolean> future = timerExecutor.submit(() -> wc.worker.taskCompleted(this.uid));
            try {
                future.get(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                future.cancel(true);
            }
        }
    }
}
