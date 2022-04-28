package task;

import com.healthmarketscience.rmiio.RemoteIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.UUID;

/**
 * Task interface represents data and its associated operation that Workers are assigned and complete.
 */
public interface Task extends Serializable {

    /**
     * Get completion status of task
     * @return true if complete, false otherwise
     */
    boolean isComplete();

    /**
     * Run the task.
     */
    void run();

    /**
     * Set the iterator that run uses to retrieve data.
     * @param iterator to read input data from.
     */
    void setInputData(RemoteIterator<String> iterator);

    /**
     * Set the output stream that run uses to write data too.
     * @param output stream to write data too.
     */
    void setOutputData(OutputStream output);

    /**
     * Get this tasks type, either "map" or "reduce" (not enforced)
     * @return this tasks type
     */
    String getType();

    /**
     * Get the unique ID of this task.
     * @return unique id
     */
    UUID getUID();

    /**
     * Emit a key value pair that is a result of task computation
     * @param key of data pair
     * @param value of data pair
     * @throws IOException if fails to write out computation result.
     */
    void emit(String key, String value) throws IOException;
}
