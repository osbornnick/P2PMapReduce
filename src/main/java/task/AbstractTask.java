package task;

import com.healthmarketscience.rmiio.RemoteIterator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public abstract class AbstractTask implements Task {
    protected RemoteIterator<String> iterator;
    protected OutputStream output;
    protected String type;
    protected UUID uid;
    protected boolean isComplete;

    public AbstractTask() {
        this.uid = UUID.randomUUID();
        this.isComplete = false;
    }

    @Override
    public void setInputData(RemoteIterator<String> iterator) {
        this.iterator = iterator;
    }

    @Override
    public void setOutputData(OutputStream writer) {
        this.output = writer;
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Task)) return false;
        Task t = (Task) obj;
        return t.getUID().equals(this.getUID());
    }

    @Override
    public boolean isComplete() {
        return this.isComplete;
    }

    @Override
    public UUID getUID() {
        return uid;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void emit(String key, String value) throws IOException {
        this.output.write(String.format("%s, %s%n", key, value).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String toString() {
        return String.format("[%s - %s]", this.getType(), this.getUID());
    }

    protected String[] getPair(RemoteIterator<String> iter) throws IOException {
        return iter.next().split(", ");
    }
}


