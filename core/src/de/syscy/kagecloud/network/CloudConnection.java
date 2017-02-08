package de.syscy.kagecloud.network;

import com.esotericsoftware.kryonet.Connection;

import de.syscy.kagecloud.CloudServerInfo;
import de.syscy.kagecloud.network.packet.ShutdownPacket;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import lombok.Setter;

public class CloudConnection extends Connection {
	private @Getter @Setter Type type = Type.INVALID;

	private @Getter @Setter ServerStatus serverStatus = ServerStatus.UNKNOWN;

	private @Getter @Setter CloudServerInfo serverInfo;

	private @Getter @Setter UUID nodeId = null;
	private @Getter @Setter String name = "";

	public void shutdown() {
		sendTCP(new ShutdownPacket());
	}

	public static enum Type {
		INVALID, WRAPPER, SERVER;
	}

	public static enum ServerStatus {
		UNKNOWN, STARTING, RUNNING, OFFLINE;
	}
}