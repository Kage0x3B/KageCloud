package de.syscy.kagecloud.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.syscy.kagecloud.KageCloud;

public class Whitelist {
	private List<UUID> allowedUUIDs = null;

	public void load(File dataFolder) {
		File whitelistFile = new File(dataFolder, "whitelist.txt");
		allowedUUIDs = new ArrayList<>();

		try(BufferedReader reader = new BufferedReader(new FileReader(whitelistFile))) {
			reader.lines().forEach(line -> {
				try {
					if(!line.contains("-")) {
						line = fixUUID(line);
					}

					UUID uuid = UUID.fromString(line);
					allowedUUIDs.add(uuid);
				} catch(IllegalArgumentException ex) {
					KageCloud.logger.info("Invalid UUID: " + line);
				}
			});
		} catch(FileNotFoundException ex) {
			ex.printStackTrace();
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public boolean isAllowed(UUID playerId) {
		return allowedUUIDs == null || allowedUUIDs.isEmpty() || allowedUUIDs.contains(playerId);
	}

	private String fixUUID(String uuidString) {
		return uuidString.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5");
	}
}