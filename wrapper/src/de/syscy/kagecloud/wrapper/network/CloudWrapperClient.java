package de.syscy.kagecloud.wrapper.network;

import java.io.IOException;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.network.ChunkedPacketListener;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.wrapper.KageCloudWrapper;

import com.esotericsoftware.kryonet.Client;

public class CloudWrapperClient extends Client {
	private final KageCloudWrapper wrapper;

	public CloudWrapperClient(KageCloudWrapper wrapper) {
		super();

		this.wrapper = wrapper;
	}

	public void connect(String ip, int tcpPort) throws IOException {
		CloudWrapperNetworkListener listener = new CloudWrapperNetworkListener(wrapper);
		addListener(new ChunkedPacketListener(listener));
		addListener(listener);

		Packet.registerKryoClasses(getKryo());

		new Thread(this, "Client").start();

		connect(5000, ip, tcpPort);

		KageCloud.logger.info("Connected to " + ip);
	}
}