package de.syscy.kagecloud.spigot.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket.Server;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.network.packet.server.ReloadServerPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KLabel;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.ClickType;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.AllArgsConstructor;

public class ManageServerGUI extends KGUI {
	private KageCloudSpigot plugin;
	private Server server;

	public ManageServerGUI(KageCloudSpigot plugin, Server server) {
		super();

		this.plugin = plugin;
		this.server = server;

		setTitle("Manage " + server.getName());
		setHeight(1);

		KLabel infoLabel = new KLabel(0, 0);
		infoLabel.setTitle("Info");
		infoLabel.setIcon(new ItemIcon(Material.PAPER));
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, "Template: " + server.getTemplateName());

		if(server.isLobby()) {
			infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 1, ChatColor.GREEN + "Lobby Server");
		}

		if(server.isRestricted()) {
			infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 2, ChatColor.RED + "Restricted");
		}

		add(infoLabel);

		KButton teleportButton = new KButton(1, 0);
		teleportButton.setTitle("Teleport");
		teleportButton.setIcon(new ItemIcon(Material.ENDER_PEARL));
		teleportButton.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				ConnectPlayerPacket packet = new ConnectPlayerPacket(player.getUniqueId().toString(), server.getName());
				plugin.getClient().sendTCP(packet);

				button.displayStatus(Integer.MAX_VALUE, ChatColor.GOLD + "Teleporting you...", new ItemIcon(Material.EYE_OF_ENDER));
			}
		}, "Teleport you to this server");
		add(teleportButton);

		KButton reloadServerButton = new KButton(2, 0);
		reloadServerButton.setTitle("Reload");
		reloadServerButton.setIcon(new ItemIcon(Material.EMERALD));
		reloadServerButton.setClickListener(new ReloadButtonClickListener(false), "Reload the server");
		reloadServerButton.setClickListener(ClickType.SHIFT_CLICK, new ReloadButtonClickListener(true), "Kick players and reload the server");
		add(reloadServerButton);
	}

	@AllArgsConstructor
	private class ReloadButtonClickListener implements ButtonClickListener {
		private final boolean kickPlayers;

		@Override
		public void onClick(KButton button, Player player) {
			ReloadServerPacket packet = new ReloadServerPacket(server.getName(), kickPlayers);
			plugin.getClient().sendTCP(packet);

			button.displayStatus(20, ChatColor.YELLOW + "Reloading the server...", new ItemIcon(Material.WATCH));
		}
	}
}