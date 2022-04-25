package task;

// given a callback to report completion?


import com.healthmarketscience.rmiio.RemoteIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

// given a data file and an operation, runs() the operation on the data file, saves the output to a local temporary file,
// reports the location of the local temporary file
public interface Task extends Serializable {

    boolean isComplete();

    // run the task, reading from inputData, writing to outputstream
    // CLOSE THE STREAMS WHEN DONE!!!!
    void run();
    void setInputData(RemoteIterator<String> iterator);
    void setOutputData(OutputStream output);

    String getType();
    UUID getUID();

    void emit(String key, String value) throws IOException;
}
