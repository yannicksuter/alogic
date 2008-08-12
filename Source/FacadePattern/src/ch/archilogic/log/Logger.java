package ch.archilogic.log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	private static boolean debugVerbose = false;
	private static final SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
	
	public static boolean isDebugVerbose() {
		return debugVerbose;
	}

	public static void setDebugVerbose(boolean debug) {
		Logger.debugVerbose = debug;
	}
	
	public static void info(String msg) {	
		System.out.println(String.format("%s - %s", df.format(new Date()), msg));
	}

	public static void err(String msg) {
		System.err.println(String.format("%s - %s", df.format(new Date()), msg));
	}

	public static void debug(String msg) {	
		if (debugVerbose) {
			System.out.println(String.format("%s - [debug] %s", df.format(new Date()), msg));
		}
	}
}
