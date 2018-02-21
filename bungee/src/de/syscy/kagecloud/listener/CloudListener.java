package de.syscy.kagecloud.listener;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class CloudListener implements Listener {
	private final KageCloudBungee bungee;

	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {
		ProxiedPlayer p = event.getPlayer();

		bungee.getClient().sendTCP(new PlayerJoinNetworkPacket(p.getUniqueId().toString(), p.getName(), p.getPendingConnection().getVersion()));
	}

	@EventHandler
	public void onPlayerLeave(PlayerDisconnectEvent event) {
		bungee.getClient().sendTCP(new PlayerLeaveNetworkPacket(event.getPlayer().getUniqueId().toString()));
	}

	@EventHandler
	public void onPlayerJoinServer(ServerConnectedEvent event) {
		CloudServerInfo serverInfo = (CloudServerInfo) event.getServer().getInfo();

		bungee.getClient().sendTCP(new PlayerJoinServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
	}

	@EventHandler
	public void onPlayerLeaveServer(ServerDisconnectEvent event) {
		CloudServerInfo serverInfo = (CloudServerInfo) event.getTarget();

		bungee.getClient().sendTCP(new PlayerLeaveServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
	}
}