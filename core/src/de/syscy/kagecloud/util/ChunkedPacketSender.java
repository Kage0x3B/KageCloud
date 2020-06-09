package de.syscy.kagecloud.util;

import de.syscy.kagecloud.network.packet.ChunkedPacket;
import de.syscy.kagecloud.network.packet.Packet;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ChunkedPacketSender {
	private static Kryo chunkSerializer = null;

	private static void init() {
		chunkSerializer = new Kryo();
		Packet.registerKryoClasses(chunkSerializer);
	}

	public static void sendPacket(Packet packet, int bufferSize, Connection receiver) {
		if(receiver == null) {
			throw new IllegalArgumentException("receiver cannot be null.");
		}

		sendPacket(packet, bufferSize, p -> receiver.sendTCP(p));
	}

	public static void sendPacket(Packet packet, int bufferSize, IPacketSender packetSender) {
		if(packetSender == null) {
			throw new IllegalArgumentException("packetSender cannot be null.");
		}

		if(packet == null) {
			throw new IllegalArgumentException("packet cannot be null.");
		}

		if(chunkSerializer == null) {
			init();
		}

		UUID packetId = UUID.randomUUID();
		byte[] data = new byte[bufferSize];

		Output output = new Output(data);
		chunkSerializer.writeClassAndObject(output, packet);

		for(int i = 0; i < output.position(); i += ChunkedPacket.MAX_CHUNK_LENGTH) {
			int chunkSize = Math.min(ChunkedPacket.MAX_CHUNK_LENGTH, output.position() - i);

			byte[] chunk = new byte[chunkSize];
			System.arraycopy(data, i, chunk, 0, chunkSize);

			packetSender.sendPacket(new ChunkedPacket(packetId, false, chunk));
		}

		packetSender.sendPacket(new ChunkedPacket(packetId, true, null));
	}

	public static interface IPacketSender {
		public void sendPacket(Packet packet);
	}
}