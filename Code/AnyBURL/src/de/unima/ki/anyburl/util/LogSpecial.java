package de.unima.ki.anyburl.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * A log writer for special debugging purpose.
 * Can be used from every class without need to initialise first.
 * 
 *
 */
public class LogSpecial {
	
	
	static PrintWriter pw = null;
	static String path = "log_special.txt";
	
	
	static {
		try {
			pw = new PrintWriter(path);
			pw.println("special log writer activated!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	
	public static void write(String message) {
		long millis = System.currentTimeMillis();
		long m = millis % 1000000;
		pw.println(m + "\t" + message);

	}
	
	
	public static void flush() {
		pw.flush();

	}
	
	*/
	

	

}
