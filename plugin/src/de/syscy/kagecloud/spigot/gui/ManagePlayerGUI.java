package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket.Player;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KLabel;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.LoreBuilder;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ManagePlayerGUI extends KGUI {
	public ManagePlayerGUI(KageCloudSpigot plugin, Player player) {
		super();

		setTitle("Manage " + player.getPlayerName());
		setHeight(1);

		KLabel infoLabel = new KLabel(0, 0);
		infoLabel.setTitle("Info");
		infoLabel.setIcon(new ItemIcon(Material.PAPER));
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, "Rank: " + player.getRankName());
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 1, "Current server: " + player.getCurrentServer().getName());
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 2, "BungeeCord Proxy: " + player.getProxyName());

		add(infoLabel);

		KButton teleportButton = new KButton(1, 0);
		teleportButton.setTitle("Teleport");
		teleportButton.setIcon(new ItemIcon(Material.ENDER_PEARL));
		teleportButton.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, org.bukkit.entity.Player player) {
				ConnectPlayerPacket packet = new ConnectPlayerPacket(player.getUniqueId().toString(), player.getServer().getName());
				plugin.getClient().sendTCP(packet);

				button.displayStatus(Integer.MAX_VALUE, ChatColor.GOLD + "Teleporting you...", new ItemIcon(Material.EYE_OF_ENDER));
			}
		}, "Teleport you to this server");
		add(teleportButton);

		KButton manageServerButton = new KButton(2, 0);
		manageServerButton.setTitle("Manage server");
		manageServerButton.setIcon(new ItemIcon(Material.COMMAND_MINECART));
		manageServerButton.setClickListener((b, p) -> KageGUI.showGUI(new ManageServerGUI(plugin, player.getCurrentServer()), p), "Manage the players current server");
		add(manageServerButton);
	}
}