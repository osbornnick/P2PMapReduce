package client;

import task.Task;

import java.io.InputStream;

/**
 * Represents an entity that can run MapReduce.
 */
public interface MapReducer {

    /**
     * Schedule a map and reduce task with given tasks, data, and requested number of reduce workers
     * @param map the map task
     * @param reduce the reduce task
     * @param data streams data files
     * @param reducers how many reducers the user would like
     * @return true once operation is complete.
     */
    boolean mapReduce(Task map, Task reduce, InputStream[] data, int reducers);
}
