package util;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

public class Logger {

  private PrintStream out;

  public Logger(String name) {
    File outFile = new File(String.format("./%s.log", name));
    this.out = System.out; // just in case
    try {
      outFile.createNewFile();
      this.out = new PrintStream(outFile);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  /**
   * Log a message
   *
   * @param message to log
   */
  public void log(String message) {
    String formatted = formatWithTime(message);
    out.println(formatWithTime(formatted));
    System.out.println(formatted);
  }

  /**
   * Log a string formatted with args
   *
   * @param format string
   * @param args to format string with
   */
  public void log(String format, Object... args) {
    args = Arrays.stream(args).map(o -> o != null ? o : "null").toArray();
    this.log(String.format(format, args));
  }

  /**
   * Add current milliseconds to log message
   *
   * @param message to add milliseconds too
   * @return message with milliseconds added
   */
  private String formatWithTime(String message) {
    return String.format("%d> %s", System.currentTimeMillis(), message);
  }
}
