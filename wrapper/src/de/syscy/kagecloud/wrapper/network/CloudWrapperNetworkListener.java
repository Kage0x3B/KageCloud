package de.syscy.kagecloud.wrapper.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ReflectionListener;

import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.packet.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.CreateServerPacket;
import de.syscy.kagecloud.network.packet.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.ShutdownPacket;
import de.syscy.kagecloud.wrapper.KageCloudWrapper;
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
}