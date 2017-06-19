package de.syscy.kagecloud.network.packet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;

public class PluginDataPacket extends Packet {
	private @Getter String channel;

	private transient ByteArrayDataOutput out;
	private transient ByteArrayDataInput in;

	public PluginDataPacket(String channel) {
		this.channel = channel;
		out = ByteStreams.newDataOutput();
	}

	public PluginDataPacket(String channel, ByteArrayDataOutput out) {
		this.channel = channel;
		this.out = out;
	}

	public ByteArrayDataOutput out() {
		if(in != null) {
			throw new IllegalStateException("You can't use this on a packet received over the network! Create a new ");
		}

		return out;
	}

	public ByteArrayDataInput in() {
		if(in == null) {
			throw new IllegalStateException("You can't use this on a self created instance, only when receiving this over the network!");
		}

		return in;
	}

	public static class PluginDataSerializer extends Serializer<PluginDataPacket> {
		@Override
		public PluginDataPacket read(Kryo kryo, Input input, Class<PluginDataPacket> type) {
			PluginDataPacket packet = new PluginDataPacket(input.readString());

			int length = input.readInt();
			packet.in = ByteStreams.newDataInput(input.readBytes(length));

			return packet;
		}

		@Override
		public void write(Kryo kryo, Output output, PluginDataPacket packet) {
			output.writeString(packet.channel);

			byte[] data = packet.out.toByteArray();
			output.writeInt(data.length);
			output.writeBytes(data);
		}
	}
}