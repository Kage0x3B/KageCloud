package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket.Player;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KList;
import de.syscy.kagegui.util.LoreBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class PlayerListGUI extends KGUI {
	public PlayerListGUI(KageCloudSpigot plugin, List<Player> playerList) {
		super();

		setTitle("Players");
		setHeight(6);

		KList list = new KList(0, 0);
		list.setWidth(9);
		list.setHeight(6);

		playerList.forEach(player -> {
			ItemStack skullItemStack = new ItemStack(Material.PLAYER_HEAD);
			SkullMeta skullMeta = (SkullMeta) skullItemStack.getItemMeta();
			skullMeta.setOwner(player.getPlayerName());
			skullItemStack.setItemMeta(skullMeta);

			KButton playerButton = new KButton(0, 0);
			playerButton.setTitle(player.getPlayerName());
			playerButton.setIcon(new ItemIcon(skullItemStack));
			playerButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, ChatColor.BLUE + "Current server: " + player.getCurrentServer().getName());

			playerButton.setClickListener((b, p) -> KageGUI.showGUI(new ManagePlayerGUI(plugin, player), p), "Manage player");

			list.add(playerButton);
		});
		add(list);
	}
}