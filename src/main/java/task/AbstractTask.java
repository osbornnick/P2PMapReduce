package task;

import com.healthmarketscience.rmiio.RemoteIterator;

import java.io.InputStream;
import java.io.OutputStream;
public abstract class AbstractTask implements Task {
    protected RemoteIterator<String> iterator;
    protected OutputStream output;

    @Override
    public void setInputData(RemoteIterator<String> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void setOutputData(OutputStream writer) {
        this.output = writer;
    }
}
