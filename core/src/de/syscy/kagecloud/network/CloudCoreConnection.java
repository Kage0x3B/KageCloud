package de.syscy.kagecloud.network;

import de.syscy.kagecloud.CloudServer;
import lombok.Setter;

public class CloudCoreConnection extends CloudConnection {
	private @Setter IConnectionRepresentation connectionRepresentation;

	public CloudServer getAsCloudServer() {
		if(getType() != Type.SERVER || connectionRepresentation == null) {
			return null;
		}

		return (CloudServer) connectionRepresentation;
	}
}