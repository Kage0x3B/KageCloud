package de.syscy.kagecloud.network;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.syscy.kagecloud.CloudPlayer;
import de.syscy.kagecloud.CloudServer;
import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.event.ServerStatusChangeEvent;
import de.syscy.kagecloud.network.CloudConnection.ServerStatus;
import de.syscy.kagecloud.network.CloudConnection.Type;
import de.syscy.kagecloud.network.packet.PluginDataPacket;
import de.syscy.kagecloud.network.packet.RelayPacket;
import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket.Player;
import de.syscy.kagecloud.network.packet.info.gui.RequestPlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.RequestServerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket.Server;
import de.syscy.kagecloud.network.packet.node.ChangeStatusPacket;
import de.syscy.kagecloud.network.packet.node.RegisterProxyPacket;
import de.syscy.kagecloud.network.packet.node.RegisterServerPacket;
import de.syscy.kagecloud.network.packet.node.RegisterWrapperPacket;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerJoinServerPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveNetworkPacket;
import de.syscy.kagecloud.network.packet.player.PlayerLeaveServerPacket;
import de.syscy.kagecloud.network.packet.server.ReloadServerPacket;
import de.syscy.kagecloud.util.SearchQueryFilter;
import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryonet.Connection;

public class CloudNetworkListener extends CloudReflectionListener {
	private final KageCloudCore core;

	public CloudNetworkListener(KageCloudCore core) {
		super(CloudCoreConnection.class);

		this.core = core;
	}

	@Override
	public void disconnected(Connection kryoConnection) {
		CloudCoreConnection connection = (CloudCoreConnection) kryoConnection;

		core.getPluginManager().callEvent(new ServerStatusChangeEvent(connection, ServerStatus.OFFLINE));

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

		core.getPluginManager().callEvent(new ServerStatusChangeEvent(connection, packet.getStatus()));

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

	public void received(CloudCoreConnection connection, PluginDataPacket packet) {
		core.onPluginData(connection, packet);
	}

	public void received(CloudCoreConnection connection, RelayPacket packet) {
		List<CloudConnection> receiverList = new ArrayList<>();

		boolean byID = packet.getReceiverId() != null;
		UUID id = packet.getReceiverId();
		String name = packet.getReceiverName();

		if(packet.isToAllOfType()) {
			switch(packet.getReceiverType()) {
				case PROXY:
					receiverList.addAll(core.getBungeeCordProxies().values());
					break;
				case WRAPPER:
					receiverList.addAll(core.getWrappers().values());
					break;
				case SERVER:
					core.getServers().forEach((uuid, server) -> receiverList.add(server.getConnection()));
					break;
				default:
					break;
			}
		} else {
			switch(packet.getReceiverType()) {
				case PROXY:
					if(byID) {
						receiverList.add(core.getBungeeCordProxies().get(id));
					} else {
						core.getBungeeCordProxies().values().parallelStream().filter(p -> p.getName().equalsIgnoreCase(name)).forEach(p -> receiverList.add(p));
					}
					break;
				case WRAPPER:
					if(byID) {
						receiverList.add(core.getWrappers().get(id));
					} else {
						core.getBungeeCordProxies().values().parallelStream().filter(w -> w.getName().equalsIgnoreCase(name)).forEach(w -> receiverList.add(w));
					}
					break;
				case SERVER:
					if(byID) {
						CloudServer server = core.getServers().get(id);
						receiverList.add(server != null ? server.getConnection() : null);
					} else {
						core.getServers().values().parallelStream().filter(s -> s.getName().equalsIgnoreCase(name)).forEach(s -> receiverList.add(s.getConnection()));
					}
					break;
				default:
					break;
			}
		}

		receiverList.forEach(receiver -> {
			if(receiver != null) {
				receiver.sendTCP(packet.getPacket());
			}
		});
	}

	public void received(CloudCoreConnection connection, RequestServerListPacket packet) {
		List<Server> serverList = new ArrayList<>();

		core.getServers().values().stream().filter(new SearchQueryFilter(packet.getSearchQuery(), true)).forEach(server -> serverList.add(Server.fromCloudServer(server)));

		ServerListPacket response = new ServerListPacket(serverList);
		response.setId(packet.getId());
		connection.sendTCP(response);
	}

	public void received(CloudCoreConnection connection, RequestPlayerListPacket packet) {
		List<Player> playerList = new ArrayList<>();

		core.getPlayers().values().stream().filter(new SearchQueryFilter(packet.getSearchQuery(), false)).limit(10).forEach(player -> playerList.add(Player.fromCloudPlayer(player)));

		PlayerListPacket response = new PlayerListPacket(playerList);
		response.setId(packet.getId());
		connection.sendTCP(response);
	}

	public void received(CloudCoreConnection connection, ConnectPlayerPacket packet) {
		CloudPlayer player = core.getPlayers().get(UUID.fromString(packet.getPlayerId()));

		if(player != null) {
			player.getBungeeCordProxy().sendTCP(packet);
		}
	}

	public void received(CloudCoreConnection connection, ReloadServerPacket packet) {
		CloudServer server = core.getServers().values().parallelStream().filter(s -> s.getName().equalsIgnoreCase(packet.getServerName())).findAny().orElse(null);

		if(server != null) {
			if(packet.isKickPlayers()) {
				server.getPlayers().forEach((uuid, player) -> {
					CloudServer lobbyServer = core.getJoinableLobbyServers(player).stream().max(new Comparator<CloudServer>() {
						@Override
						public int compare(CloudServer o1, CloudServer o2) {
							return o1.getPlayers().size() - o2.getPlayers().size();
						}
					}).orElse(null);

					if(lobbyServer != null) {
						player.connect(lobbyServer);
					}
				});
			}

			core.getWrappers().forEach((uuid, wrapper) -> wrapper.sendTCP(packet));

			core.getScheduler().schedule(core.getPluginManager().getCorePlugin(), new Runnable() {
				@Override
				public void run() {
					server.getConnection().sendTCP(packet);
				}
			}, 2, TimeUnit.SECONDS);
		}
	}
}