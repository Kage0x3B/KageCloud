package de.syscy.kagecloud.wrapper.network;

import java.io.IOException;
import java.util.Optional;

import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.server.CreateServerPacket;
import de.syscy.kagecloud.network.packet.server.ReloadServerPacket;
import de.syscy.kagecloud.wrapper.CloudServer;
import de.syscy.kagecloud.wrapper.KageCloudWrapper;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudWrapperNetworkListener extends ReflectionListener {
	private final KageCloudWrapper wrapper;

	@Override
	public void connected(Connection connection) {
		connection.sendTCP(new RegisterWrapperPacket(wrapper.getNodeId(), wrapper.getNodeName(), wrapper.getCredentials()));
		connection.sendTCP(new ChangeStatusPacket(ServerStatus.RUNNING));
	}

	@Override
	public void disconnected(Connection connection) {
		wrapper.shutdown();
	}

	public void received(Connection connection, CreateServerPacket packet) {
		wrapper.createServer(packet.getServerId(), packet.getServerName(), packet.getTemplateName());
	}

	public void received(Connection connection, ShutdownPacket packet) {
		wrapper.shutdown();
	}

	public void received(Connection connection, ReloadServerPacket packet) {
		Optional<CloudServer> serverOpt = wrapper.getServers().values().parallelStream().filter(s -> s.getServerName().equalsIgnoreCase(packet.getServerName())).findAny();

		if(serverOpt.isPresent()) {
			CloudServer server = serverOpt.get();

			try {
				server.copyPlugins();
				server.copyPluginData();
				server.copyTemplateData();
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}