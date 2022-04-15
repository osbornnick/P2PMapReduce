package utility;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * A utils class for any misc. functionality
 */
public class Utils {

    // default constructor suffices

    /**
     * Return a string with date and time to the second
     * @return a string with date and time to the second
     */
    public String timeToSecond() {
        String ts = LocalDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        ts = ts.concat(";");

        return ts;
    }



    /**
     * Return a string with date and time to the millisecond
     * @return a string with date and time to the millisecond
     */
    public String timeToMillisecond() {
        String tms = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MILLIS)
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
//        ts = ts.concat(";");

        return tms;
    }
}


