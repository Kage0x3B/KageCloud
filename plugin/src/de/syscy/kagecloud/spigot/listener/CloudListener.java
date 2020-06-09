package de.syscy.kagecloud.spigot.listener;

import de.syscy.kagecloud.network.packet.player.PlayerLateJoinServerPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudListener implements Listener {
	private final KageCloudSpigot plugin;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.getClient().sendTCP(new PlayerLateJoinServerPacket(event.getPlayer().getUniqueId().toString(), plugin.getNodeId().toString()));
	}
}