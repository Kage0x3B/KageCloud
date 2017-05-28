package de.syscy.kagecloud.spigot.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.spigot.KageCloudSpigot;

public class CloudPluginClient extends Client {
	private final KageCloudSpigot plugin;

	public CloudPluginClient(KageCloudSpigot plugin) {
		super();

		this.plugin = plugin;
	}

	public void connect(String ip, int tcpPort) throws IOException {
		addListener(new CloudPluginNetworkListener(plugin));

		Packet.registerKryoClasses(getKryo());

		start();

		connect(5000, ip, tcpPort);

		KageCloud.logger.info("Connected to " + ip);
	}
}