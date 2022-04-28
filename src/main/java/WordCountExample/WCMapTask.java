package WordCountExample;

import task.AbstractTask;

import java.io.IOException;


/**
 * A sample implementation of a map operation for use in the word count example.
 *   The implemented run (map) task takes each word and emits a 1.
 */
public class WCMapTask extends AbstractTask {

    public WCMapTask() {
        super();
        this.type = "map";
    }

    @Override
    public void run() {
        try {
            while (this.iterator.hasNext()) {
                String line = this.iterator.next();
                for (String s : line.split("\\W+")) {
                    this.emit(s.toLowerCase(), "1");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.isComplete = true;
    }
}
