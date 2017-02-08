package de.syscy.kagecloud.util;

import java.util.Collections;
import java.util.List;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.AbstractReconnectHandler;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@RequiredArgsConstructor
public class CloudReconnectHandler extends AbstractReconnectHandler {
	private final KageCloudBungee plugin;

	@Override
	protected ServerInfo getStoredServer(ProxiedPlayer player) {
		List<CloudServerInfo> availableLobbyServers = plugin.getJoinableLobbyServers(player);

		if(!availableLobbyServers.isEmpty()) {
			return Collections.min(availableLobbyServers);
		} else {
			player.disconnect(new TextComponent(ChatColor.RED + "No lobby server available."));

			return null;
		}
	}

	@Override
	public void close() {

	}

	@Override
	public void save() {

	}

	@Override
	public void setServer(ProxiedPlayer player) {

	}
}