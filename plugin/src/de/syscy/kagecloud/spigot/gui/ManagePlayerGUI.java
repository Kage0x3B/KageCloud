package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket.Player;
import de.syscy.kagecloud.network.packet.player.ConnectPlayerPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KLabel;
import de.syscy.kagegui.util.LoreBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class ManagePlayerGUI extends KGUI {
	public ManagePlayerGUI(KageCloudSpigot plugin, Player player) {
		super();

		setTitle("Manage " + player.getPlayerName());
		setHeight(1);

		KLabel infoLabel = new KLabel(0, 0);
		infoLabel.setTitle("Info");
		infoLabel.setIcon(new ItemIcon(Material.PAPER));
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, ChatColor.BLUE + "Current server: " + player.getCurrentServer().getName());
		infoLabel.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 1, ChatColor.YELLOW + "BungeeCord Proxy: " + player.getProxyName());

		add(infoLabel);

		KButton teleportButton = new KButton(1, 0);
		teleportButton.setTitle("Teleport");
		teleportButton.setIcon(new ItemIcon(Material.ENDER_PEARL));
		teleportButton.setClickListener((button, player1) -> {
			ConnectPlayerPacket packet = new ConnectPlayerPacket(player1.getUniqueId().toString(), player.getCurrentServer().getName());
			plugin.getClient().sendTCP(packet);

			button.displayStatus(Integer.MAX_VALUE, ChatColor.GOLD + "Teleporting you...", new ItemIcon(Material.ENDER_EYE));
		}, "Teleport you to this server");
		add(teleportButton);

		KButton moveButton = new KButton(2, 0);
		moveButton.setTitle("Move here");
		moveButton.setIcon(new ItemIcon(addGlowEffect(new ItemStack(Material.ENDER_PEARL))));
		moveButton.setClickListener((b, p) -> {
			ConnectPlayerPacket packet = new ConnectPlayerPacket(player.getPlayerId(), p.getServer().getName());
			plugin.getClient().sendTCP(packet);

			new TeleportRunnable(UUID.fromString(player.getPlayerId()), p).start(plugin);

			b.displayStatus(10, ChatColor.GOLD + "Teleporting...", new ItemIcon(Material.ENDER_EYE));
		}, "Teleport the player to your server");
		add(moveButton);

		KButton manageServerButton = new KButton(3, 0);
		manageServerButton.setTitle("Manage server");
		manageServerButton.setIcon(new ItemIcon(Material.COMMAND_BLOCK_MINECART));
		manageServerButton.setClickListener((b, p) -> KageGUI.showGUI(new ManageServerGUI(plugin, player.getCurrentServer()), p), "Manage the players current server");
		add(manageServerButton);
	}

	private ItemStack addGlowEffect(ItemStack itemStack) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addEnchant(Enchantment.LUCK, 1, true);
		itemMeta.addItemFlags(ItemFlag.values());
		itemStack.setItemMeta(itemMeta);

		return itemStack;
	}

	@RequiredArgsConstructor
	private static class TeleportRunnable extends BukkitRunnable {
		private final UUID playerId;
		private final org.bukkit.entity.Player destinationPlayer;
		private int tries = 0;

		@Override
		public void run() {
			org.bukkit.entity.Player bukkitPlayer = Bukkit.getPlayer(playerId);

			if(bukkitPlayer != null) {
				bukkitPlayer.teleport(destinationPlayer);

				cancel();
			} else {
				tries++;

				if(tries > 10) {
					cancel();
				}
			}
		}

		public void start(KageCloudSpigot plugin) {
			runTaskTimer(plugin, 20, 20);
		}
	}
}