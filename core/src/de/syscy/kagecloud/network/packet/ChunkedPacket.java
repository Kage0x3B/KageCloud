package de.syscy.kagecloud.network.packet;

import de.syscy.kagecloud.util.UUID;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ChunkedPacket extends Packet {
	public static final int MAX_CHUNK_LENGTH = 1024;

	private @Getter UUID id;
	private @Getter boolean end = false;
	private @Getter byte[] data;

	public static class ChunkedPacketSerializer extends Serializer<ChunkedPacket> {
		@Override
		public ChunkedPacket read(Kryo kryo, Input input, Class<ChunkedPacket> type) {
			long mostSigBits = input.readLong();
			long leastSigBits = input.readLong();
			UUID id = new UUID(mostSigBits, leastSigBits);

			boolean end = input.readBoolean();

			byte[] data = null;

			if(!end) {
				int dataLength = input.readInt();

				data = input.readBytes(dataLength);
			}

			return new ChunkedPacket(id, end, data);
		}

		@Override
		public void write(Kryo kryo, Output output, ChunkedPacket packet) {
			output.writeLong(packet.id.getMostSignificantBits());
			output.writeLong(packet.id.getLeastSignificantBits());

			output.writeBoolean(packet.end);

			if(!packet.end) {
				output.writeInt(packet.data.length);
				output.write(packet.data);
			}
		}
	}
}