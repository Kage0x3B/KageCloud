package de.syscy.kagecloud.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.server.ServerPing;
import com.velocitypowered.api.util.Favicon;
import de.syscy.kagecloud.KageCloudVelocity;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;
import de.syscy.kagecloud.util.ChatColor;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.UUID;

public class PingListener {
	private final KageCloudVelocity plugin;

	private final ServerPing maintenanceModePing;

	private @Setter String supportedProtocolString = "Unsupported Minecraft Version";
	private @Setter int minProtocolVersion = ProtocolVersion.MINIMUM_VERSION.getProtocol();
	private @Setter int maxProtocolVersion = ProtocolVersion.MAXIMUM_VERSION.getProtocol();
	private @Setter int maxPlayers = 1;
	private @Setter int onlinePlayers = 0;
	private @Setter String[] playerInfoHover = new String[0];
	private @Setter Component description = Component.text("KageCloud Network");
	private @Setter String faviconData = null;

	private Favicon favicon = null;
	private ServerPing.SamplePlayer[] playerHoverData;

	public PingListener(KageCloudVelocity plugin) {
		this.plugin = plugin;

		updateServerPing();

		ServerPing.SamplePlayer[] playerInfoHoverData = new ServerPing.SamplePlayer[2];
		playerInfoHoverData[0] = new ServerPing.SamplePlayer(ChatColor.GOLD + "The network is currently in " + ChatColor.RED + "maintenance mode" + ChatColor.GOLD + ",", UUID.randomUUID());
		playerInfoHoverData[1] = new ServerPing.SamplePlayer(ChatColor.GOLD + "please come back later...", UUID.randomUUID());

		ServerPing.Builder maintenancePingBuilder = ServerPing.builder();
		maintenancePingBuilder.version(new ServerPing.Version(0, "Maintenance Mode"));
		maintenancePingBuilder.samplePlayers(playerInfoHoverData);
		maintenancePingBuilder.description(Component.text("Maintenance Mode", TextColor.color(255, 0, 0)));
		maintenanceModePing = maintenancePingBuilder.build();
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
			description = LegacyComponentSerializer.legacyAmpersand().deserialize(packet.getMotd());
		}

		if(packet.getFaviconData() != null) {
			faviconData = packet.getFaviconData();
		}

		updateServerPing();
	}

	public void updateServerPing() {
		playerHoverData = new ServerPing.SamplePlayer[playerInfoHover.length];

		for(int i = 0; i < playerInfoHover.length; i++) {
			playerHoverData[i] = new ServerPing.SamplePlayer(playerInfoHover[i], UUID.randomUUID());
		}

		if(faviconData != null) {
			favicon = new Favicon(faviconData);
		}
	}

	@Subscribe
	public void onPing(ProxyPingEvent event) {
		if(plugin.isMaintenanceMode()) {
			event.setPing(maintenanceModePing);

			return;
		}

		ProtocolVersion protocol = event.getConnection().getProtocolVersion();
		int protocolVersion = event.getConnection().getProtocolVersion().getProtocol();

		ServerPing.Version pingProtocolVersion;

		if(protocolVersion >= minProtocolVersion && protocolVersion <= maxProtocolVersion) {
			pingProtocolVersion = new ServerPing.Version(protocol.getProtocol(), protocol.getName());
		} else {
			pingProtocolVersion = new ServerPing.Version(minProtocolVersion, supportedProtocolString);
		}

		ServerPing.Builder pingBuilder = ServerPing.builder();
		pingBuilder.favicon(favicon);
		pingBuilder.description(description);
		pingBuilder.maximumPlayers(maxPlayers);
		pingBuilder.onlinePlayers(onlinePlayers);
		pingBuilder.samplePlayers(playerHoverData);
		pingBuilder.version(pingProtocolVersion);

		event.setPing(pingBuilder.build());
	}
}