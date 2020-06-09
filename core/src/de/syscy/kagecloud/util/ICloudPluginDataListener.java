package de.syscy.kagecloud.util;

import com.esotericsoftware.kryonet.Connection;

import de.syscy.kagecloud.network.packet.PluginDataPacket;

public interface ICloudPluginDataListener {
	public void onPluginData(Connection sender, PluginDataPacket packet);
}
