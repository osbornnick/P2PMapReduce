package task;

import java.io.Reader;
import java.io.Writer;

public abstract class AbstractTask implements Task {
    protected Reader input;
    protected Writer output;

    @Override
    public void setInputData(Reader reader) {
        this.input = reader;
    }

    @Override
    public void setOutputData(Writer writer) {
        this.output = writer;
    }
}
