package task;

// given a callback to report completion?


import java.io.Reader;
import java.io.Writer;

// given a data file and an operation, runs() the operation on the data file, saves the output to a local temporary file,
// reports the location of the local temporary file
public interface Task {

    boolean isComplete();
    void run();
    void setInputData(Reader reader);
    void setOutputData(Writer writer);

}
