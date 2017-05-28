package de.syscy.kagecloud.network;

import com.esotericsoftware.kryonet.Connection;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudConnection.Type;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.node.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.util.UUID;

public class CloudNetworkListener extends CloudReflectionListener {
	private final KageCloudCore core;

	public CloudNetworkListener(KageCloudCore core) {
		super(CloudCoreConnection.class);

		this.core = core;
	}

	@Override
	public void disconnected(Connection kryoConnection) {
		CloudCoreConnection connection = (CloudCoreConnection) kryoConnection;

		switch(connection.getType()) {
			case PROXY:
				core.removeProxy(connection.getNodeId());
				break;
			case SERVER:
				core.removeServer(connection.getNodeId());
				break;
			case WRAPPER:
				core.removeWrapper(connection.getNodeId());
				break;
			default:
				break;
		}
	}

	public void received(CloudCoreConnection connection, RegisterProxyPacket packet) {
		if(!KageCloud.cloudNode.getCredentials().equals(packet.getCredentials())) {
			KageCloud.logger.warning("A proxy tried to register with wrong credentials: " + connection + "(" + connection.getRemoteAddressTCP() + ")");

			connection.close();

			return;
		}

		connection.setNodeId(packet.getId());
		connection.setName(packet.getName());
		connection.setType(Type.PROXY);

		core.addProxy(packet.getId(), connection);
	}

	public void received(CloudCoreConnection connection, RegisterWrapperPacket packet) {
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

	public void received(CloudCoreConnection connection, RegisterServerPacket packet) {
		if(!KageCloud.cloudNode.getCredentials().equals(packet.getCredentials())) {
			KageCloud.logger.warning("A server tried to register with wrong credentials: " + connection + "(" + connection.getRemoteAddressTCP() + ")");

			connection.close();

			return;
		}

		connection.setNodeId(packet.getId());
		connection.setName(packet.getName());
		connection.setType(Type.SERVER);

		core.addServer(connection, packet);
	}

	public void received(CloudCoreConnection connection, ChangeStatusPacket packet) {
		connection.setServerStatus(packet.getStatus());

		if(connection.getType() == Type.PROXY) {
			if(packet.getStatus() == ServerStatus.OFFLINE) {
				core.removeProxy(connection.getNodeId());
			}
		} else if(connection.getType() == Type.WRAPPER) {
			if(packet.getStatus() == ServerStatus.OFFLINE) {
				core.removeWrapper(connection.getNodeId());
			}
		} else if(connection.getType() == Type.SERVER) {
			if(packet.getStatus() == ServerStatus.OFFLINE) {
				core.removeServer(connection.getNodeId());
			}
		}
	}

	public void received(CloudCoreConnection connection, PlayerJoinNetworkPacket packet) {
		core.onPlayerJoin(connection, packet);
	}

	public void received(CloudCoreConnection connection, PlayerJoinServerPacket packet) {
		UUID playerId = UUID.fromString(packet.getPlayerId());
		UUID serverId = UUID.fromString(packet.getServerId());
		CloudPlayer player = core.getPlayers().get(playerId);
		CloudServer server = core.getServers().get(serverId);

		if(player != null && server != null) {
			player.setCurrentServer(server);
			server.getPlayers().put(playerId, player);
		}
	}

	public void received(CloudCoreConnection connection, PlayerLeaveNetworkPacket packet) {
		core.onPlayerLeave(packet);
	}

	public void received(CloudCoreConnection connection, PlayerLeaveServerPacket packet) {
		UUID playerId = UUID.fromString(packet.getPlayerId());
		UUID serverId = UUID.fromString(packet.getServerId());
		CloudPlayer player = core.getPlayers().get(playerId);
		CloudServer server = core.getServers().get(serverId);

		if(player != null) {
			player.setCurrentServer(null);
		}

		if(server != null) {
			server.getPlayers().remove(playerId);
		}
	}
}