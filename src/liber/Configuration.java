package liber;

import liber.enumeration.Config;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class Configuration {
	static private final String filename = "config.tsv";
	private EnumMap<Config, String> configuration;
	private File file;
	public boolean loaded;
	public Configuration(File directory) throws Exception {
		loaded = false;
		file = new File(directory, filename);
		configuration = new EnumMap<Config, String>(Config.class);
		load();
	}
	private void load() throws Exception {
		if(file.exists()) {
			if(!file.isFile()) {
				throw new Exception("Le chemin \"" + file.getAbsolutePath() +
						"\" ne mène pas vers un fichier de configuration.");
			}
			BufferedReader reader = new BufferedReader(new FileReader(file.getAbsolutePath()));
			String line;
			while((line = reader.readLine()) != null) {
				line = line.trim();
				if(!line.isEmpty()) {
					String[] pieces = line.split("\t", 2);
					String key = pieces[0];
					String value = pieces.length == 2 ? pieces[1] : null;
					try {
						configuration.put(Config.valueOf(key), value);
					} catch(IllegalArgumentException ignored) {
						throw new Exception("Le paramètre \"" + key + "\" n'est pas un paramètre " +
								"de configuration de Libersaurus dans le fichier de configuration \"" +
								file.getAbsolutePath() + "\".");
					}
				}
			}
			reader.close();
			loaded = true;
			// debug
			debug();
		}
	}
	public boolean isLoaded() {
		return loaded;
	}
	public void save() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
		for(Map.Entry<Config, String> entry: configuration.entrySet()) {
			Config key = entry.getKey();
			String value = entry.getValue();
			if(value == null) value = "";
			String output = key + "\t" + value;
			writer.write(output);
			writer.newLine();
		}
		writer.close();
	}
	public void put(Config key, String value) {
		configuration.put(key, value);
	}
	public boolean has(Config key) {
		return configuration.containsKey(key);
	}
	public String get(Config key) {
		return configuration.get(key);
	}
	public void setPorts(int privatePort, int publicPort) {
		configuration.put(Config.privatePort, String.valueOf(privatePort));
		configuration.put(Config.publicPort, String.valueOf(publicPort));
	}
	public int getPrivatePort() {
		int port = -1;
		if(configuration.containsKey(Config.privatePort)) try {
			port = Integer.parseInt(configuration.get(Config.privatePort));
		} catch (NumberFormatException ignored) {}
		return port;
	}
	public int getPublicPort() {
		int port = -1;
		if(configuration.containsKey(Config.publicPort)) try {
			port = Integer.parseInt(configuration.get(Config.publicPort));
		} catch(NumberFormatException ignored) {}
		return port;
	}
	public void debug() {
		for(Map.Entry<Config,String> entry: configuration.entrySet()) {
			Config key = entry.getKey();
			String value = entry.getValue();
			if(value != null) value = '"' + value + '"';
			System.err.println("configuration [" + key + " = " + value + "]");
		}
	}
}
