package jobManager;

import com.healthmarketscience.rmiio.RemoteIterator;

/**
 * Functional object interface for getting RemoteIterators.
 */
public interface DataGetter {

    /**
     * Get the associated RemoteIterator
     * @return iterator of strings that is accessed remotely.
     */
    RemoteIterator<String> get();
}
