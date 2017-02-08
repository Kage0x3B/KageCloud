package de.syscy.kagecloud.wrapper;

import java.io.File;
import java.util.List;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.configuration.file.YamlConfiguration;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;

public class ServerTemplate {
	private final @Getter UUID serverId;
	private final @Getter String serverName;
	private final @Getter String templateName;

	private final @Getter File templateDirectory;

	private final @Getter File serverJAR;

	private final @Getter List<String> plugins;

	//In Megabyte
	private final @Getter int memory;

	private final @Getter int slots;

	private final @Getter boolean lobby;

	private ServerTemplate(KageCloudWrapper wrapper, UUID serverId, String serverName, String templateName, YamlConfiguration yaml) {
		this.serverId = serverId;
		this.serverName = serverName;
		this.templateName = templateName;

		templateDirectory = new File(wrapper.getTemplatesDirectory(), yaml.getString("name"));

		serverJAR = new File(wrapper.getDataFolder(), yaml.getString("serverJAR"));

		plugins = yaml.getStringList("plugins");
		plugins.addAll(wrapper.getGlobalPlugins());

		memory = yaml.getInt("memory");

		slots = yaml.getInt("slots");

		lobby = yaml.getBoolean("lobby", false);
	}

	public static ServerTemplate loadServerTemplate(KageCloudWrapper wrapper, UUID serverId, String serverName, String templateName) {
		File file = new File(wrapper.getTemplatesDirectory(), templateName + ".yml");

		if(!file.exists()) {
			KageCloud.logger.warning("Server template " + templateName + " does not exist");

			return null;
		}

		YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

		return new ServerTemplate(wrapper, serverId, serverName, templateName, yaml);
	}
}