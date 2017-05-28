package de.syscy.kagecloud.util;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.network.CloudConnection;
import de.syscy.kagecloud.network.packet.info.PlayerAmountPacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerAmountUpdater implements Runnable {
	private final KageCloudCore cloud;

	@Override
	public void run() {
		int currentAmount = cloud.getPlayers().size();
		PlayerAmountPacket packet = new PlayerAmountPacket(currentAmount);

		for(CloudConnection proxy : cloud.getBungeeCordProxies().values()) {
			proxy.sendTCP(packet);
		}
	}
}