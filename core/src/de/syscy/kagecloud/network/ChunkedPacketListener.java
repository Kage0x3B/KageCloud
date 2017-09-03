package de.syscy.kagecloud.network;

import java.util.HashMap;
import java.util.Map;

import de.syscy.kagecloud.network.packet.ChunkedPacket;
import de.syscy.kagecloud.network.packet.Packet;
import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public class ChunkedPacketListener extends Listener {
	private final Listener listener;
	private final Kryo kryo;

	private Map<UUID, ByteArrayDataOutput> currentReceivingPackets = new HashMap<>();

	public ChunkedPacketListener(Listener listener) {
		this.listener = listener;

		kryo = new Kryo();
		Packet.registerKryoClasses(kryo);
	}

	@Override
	public void received(Connection connection, Object object) {
		if(object instanceof ChunkedPacket) {
			ChunkedPacket chunkedPacket = (ChunkedPacket) object;

			if(chunkedPacket.isEnd()) {
				ByteArrayDataOutput out = currentReceivingPackets.remove(chunkedPacket.getId());

				if(out != null) {
					Packet packet = (Packet) kryo.readClassAndObject(new Input(out.toByteArray()));

					listener.received(connection, packet);
				}
			} else {
				if(!currentReceivingPackets.containsKey(chunkedPacket.getId())) {
					currentReceivingPackets.put(chunkedPacket.getId(), ByteStreams.newDataOutput());
				}

				currentReceivingPackets.get(chunkedPacket.getId()).write(chunkedPacket.getData());
			}
		}
	}
}