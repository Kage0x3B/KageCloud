package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.RelayPacket;
import de.syscy.kagecloud.network.packet.node.ShutdownPacket;
import de.syscy.kagecloud.network.packet.server.KickAllPlayersPacket;
import de.syscy.kagegui.KageGUI;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.ItemStack;

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
		teleportButton.setClickListener((b, p) -> {
			plugin.getClient().sendTCP(new ConnectPlayerPacket(p.getUniqueId().toString(), server.getName()));

			b.displayStatus(Integer.MAX_VALUE, ChatColor.GOLD + "Teleporting you...", new ItemIcon(Material.ENDER_EYE));
		}, "Teleport you to this server");
		add(teleportButton);

		KButton reloadServerButton = new KButton(2, 0);
		reloadServerButton.setTitle("Reload");
		reloadServerButton.setIcon(new ItemIcon(Material.EMERALD));
		reloadServerButton.setClickListener(new ReloadButtonClickListener(false), "Reload the server");
		reloadServerButton.setClickListener(ClickType.SHIFT_CLICK, new ReloadButtonClickListener(true), "Kick players and reload the server");
		add(reloadServerButton);

		KButton kickPlayersButton = new KButton(3, 0);
		kickPlayersButton.setTitle("Kick All Players");
		kickPlayersButton.setIcon(new ItemIcon(Material.BARRIER));
		kickPlayersButton.setClickListener((b, p) -> {
			plugin.getClient().sendTCP(new KickAllPlayersPacket(server.getName()));

			b.displayStatus(5, ChatColor.GOLD + "Kicked all players", new ItemIcon(Material.LIME_WOOL));
		}, "Kick all players and send them to a lobby");
		add(kickPlayersButton);

		KButton shutdownButton = new KButton(4, 0);
		shutdownButton.setTitle("Shutdown server");
		shutdownButton.setIcon(new ItemIcon(Material.LAVA_BUCKET));
		shutdownButton.setClickListener((b, p) -> {
			plugin.getClient().sendTCP(RelayPacket.toServer(server.getName(), new ShutdownPacket()));

			b.displayStatus(5, ChatColor.GOLD + "Shutdown the server", new ItemIcon(Material.RED_GLAZED_TERRACOTTA));
		}, "Shutdown the server");
		add(shutdownButton);
	}

	@AllArgsConstructor
	private class ReloadButtonClickListener implements ButtonClickListener {
		private final boolean kickPlayers;

		@Override
		public void onClick(KButton button, Player player) {
			ReloadServerPacket packet = new ReloadServerPacket(server.getName(), kickPlayers);
			plugin.getClient().sendTCP(packet);

			button.displayStatus(20, ChatColor.YELLOW + "Reloading the server...", new ItemIcon(Material.CLOCK));
		}
	}
}