package de.syscy.kagecloud.listener;

import java.util.UUID;

import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;
import de.syscy.kagecloud.util.ProtocolConstants;

import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.ServerPing.Protocol;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PingListener implements Listener {
	private final KageCloudBungee plugin;

	private final ServerPing maintenceModePing;

	private @Setter String supportedProtocolString = "Unsupported Minecraft Version";
	private @Setter int minProtocolVersion = 335;
	private @Setter int maxProtocolVersion = 340;
	private @Setter int maxPlayers = 1;
	private @Setter int onlinePlayers = 0;
	private @Setter String[] playerInfoHover = new String[0];
	private @Setter BaseComponent motd = new TextComponent(TextComponent.fromLegacyText("KageCloud Network"));
	private @Setter String faviconData = null;

	private Favicon favicon = null;
	private Players playerInfoData;

	public PingListener(KageCloudBungee plugin) {
		this.plugin = plugin;

		updateServerPing();

		PlayerInfo[] playerInfoHoverData = new PlayerInfo[2];
		playerInfoHoverData[0] = new PlayerInfo(ChatColor.GOLD + "The network is currently in " + ChatColor.RED + "maintenance mode" + ChatColor.GOLD + ",", UUID.randomUUID());
		playerInfoHoverData[1] = new PlayerInfo(ChatColor.GOLD + "please come back later...", UUID.randomUUID());

		Players maintenancePlayerData = new Players(0, 0, playerInfoHoverData);

		maintenceModePing = new ServerPing(new Protocol("Maintenance Mode", 0), maintenancePlayerData, ChatColor.RED + "Maintenance Mode", (Favicon) null);
	}

	public void updateFromPacket(UpdatePingDataPacket packet) {
		if(packet.getSupportedProtocolString() != null) {
			supportedProtocolString = packet.getSupportedProtocolString();
		}

		if(packet.getMinProtocolVersion() >= 0) {
			minProtocolVersion = packet.getMinProtocolVersion();
		}

		if(packet.getMaxProtocolVersion() >= 0) {
			maxProtocolVersion = packet.getMaxProtocolVersion();
		}

		if(packet.getMaxPlayers() >= 0) {
			maxPlayers = packet.getMaxPlayers();
		}

		if(packet.getOnlinePlayers() >= 0) {
			onlinePlayers = packet.getOnlinePlayers();
		}

		if(packet.getPlayerInfoHover() != null) {
			playerInfoHover = packet.getPlayerInfoHover();
		}

		if(packet.getMotd() != null) {
			motd = new TextComponent(TextComponent.fromLegacyText(packet.getMotd()));
		}

		if(packet.getFaviconData() != null) {
			faviconData = packet.getFaviconData();
		}

		updateServerPing();
	}

	@SuppressWarnings("deprecation")
	public void updateServerPing() {
		PlayerInfo[] playerInfoHoverData = new PlayerInfo[playerInfoHover.length];

		for(int i = 0; i < playerInfoHover.length; i++) {
			playerInfoHoverData[i] = new PlayerInfo(playerInfoHover[i], UUID.randomUUID());
		}

		playerInfoData = new Players(maxPlayers, onlinePlayers, playerInfoHoverData);

		if(faviconData != null) {
			favicon = Favicon.create(faviconData);
		}
	}

	@EventHandler
	public void onPing(ProxyPingEvent event) {
		if(plugin.isMaintenanceMode()) {
			event.setResponse(maintenceModePing);

			return;
		}

		int protocolVersion = event.getConnection().getVersion();

		Protocol protocolData;

		if(protocolVersion >= minProtocolVersion && protocolVersion <= maxProtocolVersion) {
			protocolData = new Protocol(ProtocolConstants.getVersionName(protocolVersion), protocolVersion);
		} else {
			protocolData = new Protocol(supportedProtocolString, minProtocolVersion);
		}

		ServerPing serverPing = new ServerPing(protocolData, playerInfoData, motd, favicon);

		event.setResponse(serverPing);
	}
}