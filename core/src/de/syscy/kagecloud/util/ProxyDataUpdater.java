package de.syscy.kagecloud.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import de.syscy.kagecloud.network.packet.info.UpdatePingDataPacket;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProxyDataUpdater implements Runnable {
	private final KageCloudCore cloud;

	@Override
	public void run() {
		int networkPlayerAmount = 0;
		Map<UUID, Integer> playerAmounts = new HashMap<>();

		for(Entry<UUID, CloudServer> serverEntry : cloud.getServers().entrySet()) {
			int playerAmount = serverEntry.getValue().getPlayers().size();
			playerAmounts.put(serverEntry.getKey(), playerAmount);
			networkPlayerAmount += playerAmount;
		}

		PlayerAmountPacket playerAmountPacket = new PlayerAmountPacket(playerAmounts);
		UpdatePingDataPacket pingDataPacket = new UpdatePingDataPacket();
		pingDataPacket.setOnlinePlayers(networkPlayerAmount);

		for(CloudConnection proxy : cloud.getBungeeCordProxies().values()) {
			proxy.sendTCP(playerAmountPacket);
			proxy.sendTCP(pingDataPacket);
		}
	}
}