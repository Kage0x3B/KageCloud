package de.syscy.kagecloud.util;

import java.util.List;

import de.syscy.kagecloud.CloudServer;

import lombok.Getter;
import lombok.Setter;

public class BasicServerController extends ServerController {
	private @Getter @Setter int maxServers = 10;
	private @Getter @Setter int minAvailableServers = 2;
	private @Getter @Setter int serverPlayerMaximum = 50;

	public BasicServerController(String templateName) {
		super(templateName);
	}

	public BasicServerController(String templateName, int minAvailableServers, int maxServers, int serverPlayerMaximum) {
		super(templateName);

		this.minAvailableServers = minAvailableServers;
		this.maxServers = maxServers;
		this.serverPlayerMaximum = serverPlayerMaximum;
	}

	@Override
	public void run() {
		int availableServers = 0;
		List<CloudServer> currentServers = getAllCurrentServers();
		int currentServerAmount = currentServers.size();

		for(CloudServer serverInfo : currentServers) {
			if(isAvailable(serverInfo) && !isAlmostFull(serverInfo)) {
				availableServers++;
			}
		}

		while(availableServers < this.minAvailableServers && currentServerAmount < this.maxServers) {
			this.createNewServer();

			availableServers++;
			currentServerAmount++;
		}
	}

	protected boolean isAlmostFull(CloudServer serverInfo) {
		return serverInfo.getPlayers().size() > serverPlayerMaximum * 0.75;
	}

	protected boolean isAvailable(CloudServer serverInfo) {
		return !serverInfo.isRestricted() && serverInfo.getPlayers().size() < serverPlayerMaximum;
	}
}