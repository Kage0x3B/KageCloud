package de.syscy.kagecloud.network;

import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryonet.Connection;

import lombok.Getter;
import lombok.Setter;

public class CloudConnection extends Connection {
	private @Getter @Setter Type type = Type.INVALID;

	private @Getter @Setter ServerStatus serverStatus = ServerStatus.UNKNOWN;

	private @Getter @Setter UUID nodeId = null;
	private @Getter @Setter String name = "";

	public void shutdown() {
		if(isConnected()) {
			sendTCP(new ShutdownPacket());
		}
	}

	public void shutdown(String reason) {
		if(isConnected()) {
			sendTCP(new ShutdownPacket(reason));
		}
	}

	public static enum Type {
		INVALID, PROXY, WRAPPER, SERVER;
	}

	public static enum ServerStatus {
		UNKNOWN, STARTING, RUNNING, OFFLINE;
	}
}