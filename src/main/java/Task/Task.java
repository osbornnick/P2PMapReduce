package Task;

// given a callback to report completion?


// given a data file and an operation, runs() the operation on the data file, saves the output to a local temporary file,
// reports the location of the local temporary file
public interface Task extends Runnable {

    public boolean isComplete();
    public void run();
    public Result getResult();
}
