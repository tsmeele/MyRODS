package nl.tsmeele.myrods.high;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


/**
 * ConfigReader reads configuration key-value pairs from a textfile, and optionally
 * requests the user to supply values for any required yet missing keys.
 * @author Ton Smeele
 *
 */
public class ConfigReader {
	private static final String CONFIG_FILE = "./irods.ini";
	private static final String[] DEFAULT_REQUIRED_KEYWORDS = {};
	
	public Map<String,String> readConfig()  {
		return readConfig(CONFIG_FILE, DEFAULT_REQUIRED_KEYWORDS);
	}
	
	public Map<String,String> readConfig(String path)  {
		return readConfig(path, DEFAULT_REQUIRED_KEYWORDS);
	}
	
	public Map<String,String> readConfig(String[] requiredKeywords)  {
		return readConfig(CONFIG_FILE, requiredKeywords);
	}
	
	public Map<String,String> readConfig(String path, String[] requiredKeywords) {
		Map<String,String> config =  readConfigFile(path);
		if (config == null) return null;
		return supplementInteractive(config, requiredKeywords);
	}
	

	private Map<String, String> supplementInteractive(Map<String,String> config, String[] keywords) {
		for (String key : keywords) {
			if (config.get(key) != null) {
				continue;
			}
			config.put(key, ask(key));
		}
		return config;
	}
	
	private String ask(String key) {
		@SuppressWarnings("resource")
		Scanner scan = new Scanner(System.in);
		System.out.print(key + "? ");
		System.out.flush();
		return scan.nextLine();
	}
	
	
	private Map<String,String> readConfigFile(String path)   {
		Map<String, String> config = new HashMap<String, String>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader (new FileReader(path));
		} catch (FileNotFoundException e) {
			return null;
		}
		try {
			String line = reader.readLine();
			while (line != null) {
				int i = line.indexOf("=");
				if (i >= 0) {
					String key = line.substring(0, i).trim();
					String value = line.substring(i).replaceFirst("=", "").trim();
					config.put(key, value);
				}
				line = reader.readLine();
			}
		} catch (IOException e) {}
		try {
			reader.close();
		} catch (IOException e) {}
		return config;
	}
	

	

}
