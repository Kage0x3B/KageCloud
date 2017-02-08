package de.syscy.kagecloud.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.Packet;

public class CloudCoreServer extends Server {
	public void start(KageCloudBungee core, int port) throws IOException {
		addListener(new CloudNetworkListener(core));

		Packet.registerClasses(getKryo());

		bind(port);

		start();

		KageCloud.logger.info("Started KryoServer on port " + port);
	}

	@Override
	protected Connection newConnection() {
		return new CloudConnection();
	}
}