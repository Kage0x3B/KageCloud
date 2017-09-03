package de.syscy.kagecloud.network;

import java.io.IOException;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.network.packet.Packet;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

public class KryoServer extends Server {
	public void start(KageCloudCore core, int port) throws IOException {
		Listener listener = new CloudNetworkListener(core);
		addListener(new ChunkedPacketListener(listener));
		addListener(listener);

		Packet.registerKryoClasses(getKryo());

		bind(port);

		start();

		KageCloud.logger.info("Started KryoServer on port " + port);
	}

	@Override
	protected Connection newConnection() {
		return new CloudCoreConnection();
	}
}