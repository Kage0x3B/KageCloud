package de.syscy.kagecloud.network;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.event.ServerStatusChangeEvent;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudConnection.Type;
import de.syscy.kagecloud.network.packet.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.RegisterWrapperPacket;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CloudNetworkListener extends CloudReflectionListener {
	private final KageCloudBungee core;

	public void received(CloudConnection connection, RegisterWrapperPacket packet) {
		if(!KageCloud.cloudNode.getCredentials().equals(packet.getCredentials())) {
			KageCloud.logger.warning("A wrapper tried to register with wrong credentials: " + connection + "(" + connection.getRemoteAddressTCP() + ")");

			connection.close();

			return;
		}

		connection.setNodeId(packet.getId());
		connection.setName(packet.getName());
		connection.setType(Type.WRAPPER);

		core.addWrapper(packet.getId(), connection);
	}

	public void received(CloudConnection connection, RegisterServerPacket packet) {
		if(!KageCloud.cloudNode.getCredentials().equals(packet.getCredentials())) {
			KageCloud.logger.warning("A server tried to register with wrong credentials: " + connection + "(" + connection.getRemoteAddressTCP() + ")");

			connection.close();

			return;
		}

		connection.setNodeId(packet.getId());
		connection.setName(packet.getName());
		connection.setType(Type.SERVER);

		core.addServerInfo(connection, packet);
	}

	public void received(CloudConnection connection, ChangeStatusPacket packet) {
		connection.setServerStatus(packet.getStatus());

		core.getProxy().getPluginManager().callEvent(new ServerStatusChangeEvent(connection, packet.getStatus()));

		if(connection.getType() == Type.WRAPPER) {
			if(packet.getStatus() == ServerStatus.OFFLINE) {
				core.removeWrapper(connection.getNodeId());
			}
		} else if(connection.getType() == Type.SERVER) {
			if(packet.getStatus() == ServerStatus.OFFLINE) {
				core.removeServer(connection.getNodeId());
			}
		}
	}
}