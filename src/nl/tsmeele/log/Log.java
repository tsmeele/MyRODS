package nl.tsmeele.log;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * The Log class prints status output, primarily useful for debug and benchmark purposes.
 * @author Ton Smeele
 *
 */
public class Log {
	private static PrintStream out = System.out;
	@SuppressWarnings("rawtypes")
	private static Enum logLevel = LogLevel.ERROR;	// default loglevel
	private static Long lastTime = -1L;
	
	public static void setPrintStream(PrintStream stream) {
		out = stream;
	}
	
	@SuppressWarnings("rawtypes")
	public static Enum getLogLevel() {
		return logLevel;
	}
	
	@SuppressWarnings("rawtypes")
	public static void setLoglevel(Enum level) {
		logLevel = level;
	}
	
	public static void error(String message) {
		println(LogLevel.ERROR, message);
	}
	
	public static void info(String message) {
		println(LogLevel.INFO, message);
	}
	
	public static void debug(String message) {
		println(LogLevel.DEBUG, message);
	}
	
	public static void timerStart() {
		lastTime = (new Date()).getTime();
	}
	
	public static void timerRead(String message) {
		if (message == null) message = "<unspecified>";
		Long t = (new Date()).getTime();
		if (lastTime == -1L) {
			lastTime = t;
		}
		out.println("MEASURED mSEC: " + (t-lastTime) + " at " + message);
		lastTime = t;
	}
	
	/**
	 * log can be used by other classes to write log messages.
	 * The current log level of the system will determine if a message is included in console output
	 * 
	 * @param level  classification of the message
	 * @param message  content
	 */
	@SuppressWarnings("rawtypes")
	public static void println(Enum level, String message) {
		if (level.ordinal() <= logLevel.ordinal() ) {
			out.println(stamp() + "  " + level.toString() + ": " + message);
		}
	}
	
	public static String stamp() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYY-MM-dd HH:mm"));
	}

	

}
