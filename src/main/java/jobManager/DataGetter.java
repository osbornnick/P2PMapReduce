package jobManager;

import com.healthmarketscience.rmiio.RemoteIterator;

public interface DataGetter {

    RemoteIterator<String> get();
}
