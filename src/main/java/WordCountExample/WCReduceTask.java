package WordCountExample;

import task.AbstractTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WCReduceTask extends AbstractTask {

    private final Map<String, Integer> counts;

    public WCReduceTask() {
        super();
        this.type = "reduce";
        this.counts = new HashMap<>();
    }

    @Override
    public void run() {
        try {
            while (this.iterator.hasNext()) {
                String[] data = getPair(iterator);
                String word = data[0];
                int num = Integer.parseInt(data[1]);
                counts.compute(word, (k, v) -> (v == null) ? num : v + num);
            }
            for (String word: counts.keySet()) {
                this.emit(word, counts.get(word).toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
