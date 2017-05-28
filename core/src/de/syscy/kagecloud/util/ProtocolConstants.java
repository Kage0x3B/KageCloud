package de.syscy.kagecloud.util;

public class ProtocolConstants {
	public static final int MINECRAFT_1_8 = 47;
	public static final int MINECRAFT_1_9 = 107;
	public static final int MINECRAFT_1_9_1 = 108;
	public static final int MINECRAFT_1_9_2 = 109;
	public static final int MINECRAFT_1_9_4 = 110;
	public static final int MINECRAFT_1_10 = 210;
	public static final int MINECRAFT_1_11 = 315;
	public static final int MINECRAFT_1_11_1 = 316;
	public static final int MINECRAFT_1_12 = 332;

	public static String getVersionName(int version) {
		if(version < MINECRAFT_1_8) {
			return "Lower than 1.8";
		} else if(version < MINECRAFT_1_9) {
			return "1.8";
		} else if(version < MINECRAFT_1_9_1) {
			return "1.9";
		} else if(version < MINECRAFT_1_9_2) {
			return "1.9.1";
		} else if(version < MINECRAFT_1_9_4) {
			return "1.9.2";
		} else if(version < MINECRAFT_1_10) {
			return "1.9.4";
		} else if(version < MINECRAFT_1_11) {
			return "1.10";
		} else if(version < MINECRAFT_1_11_1) {
			return "1.11";
		} else if(version < MINECRAFT_1_12) {
			return "1.11.x";
		} else if(version == MINECRAFT_1_12) {
			return "1.12";
		} else {
			return "Higher than 1.12";
		}
	}
}