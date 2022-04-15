package utility;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * A logger that enables statements to be printed to the standard out, as well as to a log file
 */
public class Logger {
    private File f;
    private PrintStream ps;
    private Utils u;

    /**
     * Construct a logger object. Writes a file to the logs directory
     * @param parentName
     * @param u
     * @throws IOException
     */
    public Logger(String parentName, Utils u) throws IOException {
        String nameWithTimestamp = parentName + "_" + u.timeToSecond();

        this.f = new File("logs/" + nameWithTimestamp + ".txt");
        if (! this.f.exists()) {
            this.f.createNewFile();
        }

        FileOutputStream fos = new FileOutputStream(this.f);
        this.ps = new PrintStream(fos);
        this.u = u;
        this.ps.println(u.timeToMillisecond() + " Begin logging");
    }

    /**
     * Helper to print the message both to the standard out, as well as to the corresponding log file
     *   with a millisecond timestamp
     * @param msg the message to be printed
     */
    public void printAndLog(String msg) {
        String msgWithTime = this.u.timeToMillisecond() + " " + msg;
        this.ps.println(msgWithTime);
        System.out.println(msgWithTime);
    }

}
