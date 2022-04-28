package jobManager;

import com.healthmarketscience.rmiio.RemoteIterator;

/**
 * An interface to represent an object that has the capability to create a remote iterator for a piece of data
 */
public interface DataGetter {

    /**
     * Return a remote iterate that iterates over the data associated with this DataGetter
     * @return a remote iterate that iterates over the data associated with this DataGetter
     */
    RemoteIterator<String> get();
}
