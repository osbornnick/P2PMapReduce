package jobManager;

public interface JobManager extends Runnable {
    /**
     * Start this JobManager.
     *
     * Given the Map and Reduce functions, as well as the data file paths and # of reducers and list of worker nodes:
     *
     * create a map thread for each worker, waiting on that worker to complete the assigned task
     * keep track of finished tasks, when they are complete, reschedule work that is taking awhile
     *
     * with R reducers, create a reduce for each worker, wait on that worker to complete assigned task
     * keep track of finished tasks, reassign if incomplete
     * keep track of where the data is.
     *
     * get output from reducers, write to local file(s) (one file per reducer)
     */
    void run();
}
