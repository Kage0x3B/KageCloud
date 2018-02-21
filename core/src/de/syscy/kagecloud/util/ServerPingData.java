package de.syscy.kagecloud.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.chat.TextComponent;
import de.syscy.kagecloud.configuration.ConfigurationSection;

import com.google.common.io.BaseEncoding;

import lombok.Getter;
import lombok.Setter;

public class ServerPingData {
	private final KageCloudCore core;

	private @Getter @Setter String supportedProtocolString = null;
	private @Getter @Setter int minProtocolVersion = 0;
	private @Getter @Setter int maxProtocolVersion = 0;

	private @Getter @Setter int maxPlayers = 0;
	private @Getter @Setter String[] playerInfoHover = null;

	private @Getter @Setter String motd = null;

	private @Getter @Setter String faviconName = null;

	private @Getter String faviconData = null;

	public ServerPingData(KageCloudCore core) {
		this.core = core;

		ConfigurationSection pingDataSection = core.getConfig().getConfigurationSection("pingData");
		supportedProtocolString = pingDataSection.getString("supportedProtocolString", "Unsupported Minecraft Version");
		minProtocolVersion = pingDataSection.getInt("minProtocolVersion", 335);
		maxProtocolVersion = pingDataSection.getInt("maxProtocolVersion", 340);
		maxPlayers = pingDataSection.getInt("maxPlayers", 1000);

		List<String> playerInfoHoverList = pingDataSection.getStringList("playerInfoHover");
		playerInfoHover = playerInfoHoverList.toArray(new String[playerInfoHoverList.size()]);

		for(int i = 0; i < playerInfoHover.length; i++) {
			playerInfoHover[i] = ChatColor.translateAlternateColorCodes('&', playerInfoHover[i]);
		}

		List<String> motdList = pingDataSection.getStringList("motd");
		TextComponent[] motdComponentArray = new TextComponent[motdList.size()];

		for(int i = 0; i < motdComponentArray.length; i++) {
			String motdLine = motdList.get(i);

			if(motdLine != null && !motdLine.isEmpty()) {
				motdComponentArray[i] = new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', motdLine) + "\n"));
			} else {
				motdComponentArray[i] = new TextComponent();
			}
		}

		motd = TextComponent.toLegacyText(motdComponentArray);

		faviconName = pingDataSection.getString("favicon", "default");
	}

	public void updateData() {
		File faviconFile = new File(core.getDataFolder(), faviconName + ".png");

		try {
			BufferedImage faviconImage = ImageIO.read(faviconFile);
			faviconData = createFaviconData(faviconImage);
		} catch(IllegalArgumentException | IOException ex) {
			ex.printStackTrace();
		}
	}

	private String createFaviconData(final BufferedImage image) {
		if(image.getWidth() != 64 || image.getHeight() != 64) {
			throw new IllegalArgumentException("Server icon must be exactly 64x64 pixels");
		}

		byte[] imageBytes;

		try {
			final ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(image, "PNG", stream);
			imageBytes = stream.toByteArray();
		} catch(IOException e) {
			throw new AssertionError((Object) e);
		}

		final String encoded = "data:image/png;base64," + BaseEncoding.base64().encode(imageBytes);

		if(encoded.length() > 32767) {
			throw new IllegalArgumentException("Favicon file too large for server to process");
		}

		return encoded;
	}
}