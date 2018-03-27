package de.syscy.kagecloud.listener;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.player.LoginResultPacket;
import de.syscy.kagecloud.network.packet.player.LoginResultPacket.Result;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.util.UUID;

import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

@RequiredArgsConstructor
public class CloudListener implements Listener {
	private final KageCloudBungee bungee;
	private final Map<UUID, Consumer<LoginResultPacket>> loginResultConsumers = new HashMap<>();

	@EventHandler
	public void onPlayerJoin(PreLoginEvent event) {
		PendingConnection connection = event.getConnection();
		UUID loginId = UUID.randomUUID();

		event.registerIntent(bungee);

		loginResultConsumers.put(loginId, new Consumer<LoginResultPacket>() {
			@Override
			public void accept(LoginResultPacket packet) {
				event.setCancelReason(TextComponent.fromLegacyText(packet.getMessage()));
				event.setCancelled(packet.getResult() == Result.DISALLOWED);

				event.completeIntent(bungee);
			}
		});

		bungee.getClient().sendTCP(new PlayerJoinNetworkPacket(connection.getUniqueId().toString(), connection.getName(), connection.getVersion(), loginId.toString()));

	}

	public void completeLogin(LoginResultPacket packet) {
		UUID loginId = UUID.fromString(packet.getLoginId());

		if(loginResultConsumers.containsKey(loginId)) {
			loginResultConsumers.remove(loginId).accept(packet);
		}
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

		bungee.getClient().sendTCP(new PlayerJoinServerPacket(event.getPlayer().getUniqueId().toString(), serverInfo.getId().toString()));
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