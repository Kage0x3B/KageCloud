package de.syscy.kagecloud;

import de.syscy.kagecloud.util.UUID;

public interface ICloudNode {
	public UUID getNodeId();

	public String getNodeName();

	public String getCredentials();
}