package de.syscy.kagecloud.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ProtocolConstants {
	//@-
	MINECRAFT_1_8("1.8", 47),
	MINECRAFT_1_9("1.9", 107),
	MINECRAFT_1_9_1("1.9.1", 108),
	MINECRAFT_1_9_2("1.9.2", 109),
	MINECRAFT_1_9_4("1.9.4", 110),
	MINECRAFT_1_10("1.10", 210),
	MINECRAFT_1_11("1.11", 315),
	MINECRAFT_1_11_1("1.11.1", 316),
	MINECRAFT_1_12("1.12", 335),
	MINECRAFT_1_12_1("1.12.1", 338),
	MINECRAFT_1_12_2("1.12.2", 340);
	//@+

	private final @Getter String name;
	private final @Getter int versionNumber;

	public static String getVersionName(int version) {
		if(version < values()[0].getVersionNumber()) {
			return "< " + values()[0].getName();
		}

		for(ProtocolConstants protocolVersion : values()) {
			if(protocolVersion.getVersionNumber() <= version) {
				return protocolVersion.getName();
			}
		}

		return "> " + values()[values().length - 1].getName();
	}
}