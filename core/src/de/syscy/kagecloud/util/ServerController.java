package de.syscy.kagecloud.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.scheduler.ScheduledTask;
import lombok.Getter;
import lombok.Setter;

public abstract class ServerController implements Runnable {
	private final @Getter String templateName;
	private @Setter KageCloudCore plugin;
	private @Getter @Setter ScheduledTask task;

	public ServerController(String templateName) {
		this.templateName = templateName.toLowerCase();
	}

	protected int getCurrentServerAmount() {
		return plugin.getCurrentServerAmount(templateName);
	}

	protected List<CloudServer> getAllCurrentServers() {
		List<CloudServer> serverInfoList = new ArrayList<>(plugin.getServers().size() + plugin.getStartingServers().size());

		for(CloudServer serverInfo : plugin.getServers().values()) {
			if(serverInfo.getTemplateName().equals(templateName)) {
				serverInfoList.add(serverInfo);
			}
		}

		for(StartingServerData startingServerInfo : plugin.getStartingServers().values()) {
			if(templateName.equals(startingServerInfo.getTemplateName())) {
				serverInfoList.add(startingServerInfo);
			}
		}

		return serverInfoList;
	}

	protected void createNewServer() {
		plugin.createServer(templateName);
	}
}