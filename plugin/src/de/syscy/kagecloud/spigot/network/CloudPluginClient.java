package de.syscy.kagecloud.spigot.network;

import java.io.IOException;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.ChunkedPacketListener;
import de.syscy.kagecloud.network.packet.IDPacket;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.spigot.network.CloudPluginNetworkListener.IDPacketListener;

import com.esotericsoftware.kryonet.Client;

public class CloudPluginClient extends Client {
	private final KageCloudSpigot plugin;
	private CloudPluginNetworkListener mainListener;

	public CloudPluginClient(KageCloudSpigot plugin) {
		super();

		this.plugin = plugin;
	}

	public void connect(String ip, int tcpPort) throws IOException {
		mainListener = new CloudPluginNetworkListener(plugin);
		addListener(new ChunkedPacketListener(mainListener));
		addListener(mainListener);

		Packet.registerKryoClasses(getKryo());

		start();

		connect(5000, ip, tcpPort);

		KageCloud.logger.info("Connected to " + ip);
	}

	public void sendIDPacket(IDPacket packet, IDPacketListener<?> listener) {
		mainListener.addIDPacketListener(packet, listener);
		sendTCP(packet);
	}
}