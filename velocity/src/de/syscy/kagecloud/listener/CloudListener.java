package de.syscy.kagecloud.listener;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudVelocity;
import de.syscy.kagecloud.network.packet.player.PlayerEarlyJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import lombok.RequiredArgsConstructor;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CloudListener {
	private final KageCloudVelocity plugin;

	@Subscribe
	public void onPlayerJoin(LoginEvent event) {
		if(!plugin.isConnected() || plugin.isMaintenanceMode()) {
			return;
		}

		Player player = event.getPlayer();

		plugin.getClient().sendTCP(new PlayerJoinNetworkPacket(player.getUniqueId().toString(), player.getUsername(), player.getProtocolVersion().getProtocol()));
	}

	@Subscribe
	public void onPlayerLeave(DisconnectEvent event) {
		if(!plugin.isConnected()) {
			return;
		}

		plugin.getClient().sendTCP(new PlayerLeaveNetworkPacket(event.getPlayer().getUniqueId().toString()));
	}

	@Subscribe(order = PostOrder.LAST)
	public void onPlayerJoinServer(ServerConnectedEvent event) {
		if(!plugin.isConnected()) {
			return;
		}

		CloudServerInfo serverInfo = plugin.getServerLookup().get(event.getServer().getServerInfo());
		plugin.getClient().sendTCP(new PlayerEarlyJoinServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
	}

	@Subscribe(order = PostOrder.LAST)
	public void onPlayerLeaveServer(ServerPreConnectEvent event) {
		if(!plugin.isConnected()) {
			return;
		}

		Optional<ServerConnection> currentServer = event.getPlayer().getCurrentServer();

		if(currentServer.isPresent()) {
			CloudServerInfo currentServerInfo = plugin.getServerLookup().get(currentServer.get().getServerInfo());
			plugin.getClient().sendTCP(new PlayerLeaveServerPacket(event.getPlayer().getUniqueId().toString(), currentServerInfo.getId().toString()));
		}
	}

	@Subscribe
	public void onPlayerPreConnect(ServerPreConnectEvent event) {
		//Equivalent to BungeeCords ReconnectHandler
		if(event.getOriginalServer().getServerInfo().getName().equalsIgnoreCase("dummy")) {
			if(plugin.isMaintenanceMode()) {
				event.getPlayer().disconnect(TextComponent.builder("The network is currently in ", TextColor.GOLD).append("maintenance mode", TextColor.RED).append(", please come back later...", TextColor.GOLD).build());

				return;
			}

			List<CloudServerInfo> availableLobbyServers = plugin.getJoinableLobbyServers(event.getPlayer());

			if(!availableLobbyServers.isEmpty()) {
				Optional<RegisteredServer> server = plugin.getProxy().getServer(Collections.max(availableLobbyServers).getName());

				if(server.isPresent()) {
					event.setResult(ServerPreConnectEvent.ServerResult.allowed(server.get()));

					return;
				}
			}

			event.getPlayer().disconnect(TextComponent.of("No lobby server available.", TextColor.RED));
		}
	}
}