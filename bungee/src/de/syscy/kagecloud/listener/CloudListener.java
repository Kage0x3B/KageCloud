package de.syscy.kagecloud.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.player.LoginResultPacket;
import de.syscy.kagecloud.network.packet.player.LoginResultPacket.Result;
import de.syscy.kagecloud.network.packet.player.PlayerEarlyJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.util.UUID;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class CloudListener implements Listener {
	private final KageCloudBungee bungee;

	@EventHandler
	public void onPlayerJoin(LoginEvent event) {
		if(!bungee.isConnected()) {
			return;
		}

		PendingConnection connection = event.getConnection();

		bungee.getClient().sendTCP(new PlayerJoinNetworkPacket(connection.getUniqueId().toString(), connection.getName(), connection.getVersion()));
	}

	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent event) {
		if(!bungee.isConnected()) {
			return;
		}

		bungee.getClient().sendTCP(new PlayerLeaveNetworkPacket(event.getPlayer().getUniqueId().toString()));
	}

	@EventHandler
	public void onPlayerJoinServer(ServerConnectedEvent event) {
		if(!bungee.isConnected()) {
			return;
		}

		CloudServerInfo serverInfo = (CloudServerInfo) event.getServer().getInfo();

		bungee.getClient().sendTCP(new PlayerEarlyJoinServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
	}

	@EventHandler
	public void onPlayerLeaveServer(ServerDisconnectEvent event) {
		if(!bungee.isConnected()) {
			return;
		}

		CloudServerInfo serverInfo = (CloudServerInfo) event.getTarget();

		bungee.getClient().sendTCP(new PlayerLeaveServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
	}
}