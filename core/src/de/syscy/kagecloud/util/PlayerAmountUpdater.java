package de.syscy.kagecloud.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerAmountUpdater implements Runnable {
	private final KageCloudCore cloud;

	@Override
	public void run() {
		Map<UUID, Integer> playerAmounts = new HashMap<>();

		for(Entry<UUID, CloudServer> serverEntry : cloud.getServers().entrySet()) {
			playerAmounts.put(serverEntry.getKey(), serverEntry.getValue().getPlayers().size());
		}

		PlayerAmountPacket packet = new PlayerAmountPacket(playerAmounts);

		for(CloudConnection proxy : cloud.getBungeeCordProxies().values()) {
			proxy.sendTCP(packet);
		}
	}
}