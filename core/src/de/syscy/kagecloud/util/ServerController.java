package de.syscy.kagecloud.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public abstract class ServerController implements Runnable, ScheduledTask {
	private final @Getter String templateName;
	private @Setter KageCloudBungee plugin;
	private @Setter ScheduledTask scheduledTask;

	public ServerController(String templateName) {
		this.templateName = templateName.toLowerCase();
	}

	protected int getCurrentServerAmount() {
		return plugin.getCurrentServerAmount(templateName);
	}

	protected List<CloudServerInfo> getAllCurrentServers() {
		List<CloudServerInfo> serverInfoList = new ArrayList<>(plugin.getServers().size() + plugin.getStartingServerTemplates().size());

		for(CloudServerInfo serverInfo : plugin.getServers().values()) {
			if(serverInfo.getTemplateName().equals(templateName)) {
				serverInfoList.add(serverInfo);
			}
		}

		for(String startingServerInfo : Collections.unmodifiableCollection(plugin.getStartingServerTemplates())) {
			if(templateName.equals(startingServerInfo)) {
				serverInfoList.add(new StartingServerInfo());
			}
		}

		return serverInfoList;
	}

	protected void createNewServer() {
		plugin.createServer(templateName);
	}

	@Override
	public int getId() {
		return scheduledTask.getId();
	}

	@Override
	public Plugin getOwner() {
		return scheduledTask.getOwner();
	}

	@Override
	public Runnable getTask() {
		return this;
	}

	@Override
	public void cancel() {
		scheduledTask.cancel();
	}

	private class StartingServerInfo extends CloudServerInfo {
		protected StartingServerInfo() {
			super(null, templateName, null, templateName, false, templateName, templateName == "lobby");
		}

		@Override
		public Collection<ProxiedPlayer> getPlayers() {
			return new ArrayList<>(0);
		}

		@Override
		public int hashCode() {
			return templateName.hashCode();
		}
	}
}