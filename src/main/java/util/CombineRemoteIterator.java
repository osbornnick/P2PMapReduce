package util;

import com.healthmarketscience.rmiio.RemoteIterator;
import com.healthmarketscience.rmiio.RemoteRetry;

import java.io.IOException;

public class CombineRemoteIterator<T> implements RemoteIterator<T> {

    RemoteIterator<T> iterator1;
    RemoteIterator<T> iterator2;

    public CombineRemoteIterator(RemoteIterator<T> i1, RemoteIterator<T> i2) {
        super();
        this.iterator1 = i1;
        this.iterator2 = i2;
    }

    @Override
    public boolean hasNext() throws IOException {
        return iterator1.hasNext() || iterator2.hasNext();
    }

    @Override
    public T next() throws IOException {
        if (iterator1.hasNext()) return iterator1.next();
        return iterator2.next();
    }

    @Override
    public void close() throws IOException {
        iterator1.close();
        iterator2.close();
    }

    @Override
    public void setRemoteRetry(RemoteRetry remoteRetry) {
        iterator1.setRemoteRetry(remoteRetry);
        iterator2.setRemoteRetry(remoteRetry);
    }
}
