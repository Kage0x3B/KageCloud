package de.syscy.kagecloud.spigot.gui;

import java.util.List;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket.Player;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KList;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.LoreBuilder;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerListGUI extends KGUI {
	public PlayerListGUI(KageCloudSpigot plugin, List<Player> playerList) {
		super();

		setTitle("Players");
		setHeight(6);

		KList list = new KList(0, 0);
		list.setWidth(9);
		list.setHeight(6);

		playerList.forEach(player -> {
			ItemStack skullItemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
			SkullMeta skullMeta = (SkullMeta) skullItemStack.getItemMeta();
			skullMeta.setOwner(player.getPlayerName());
			skullItemStack.setItemMeta(skullMeta);

			KButton playerButton = new KButton(0, 0);
			playerButton.setTitle(player.getPlayerName());
			playerButton.setIcon(new ItemIcon(skullItemStack));
			playerButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, "Rank: " + player.getRankName());
			playerButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 1, "Current server: " + player.getCurrentServer().getName());

			playerButton.setClickListener(new ButtonClickListener() {
				@Override
				public void onClick(KButton button, org.bukkit.entity.Player bukkitPlayer) {
					KageGUI.showGUI(new ManagePlayerGUI(plugin, player), bukkitPlayer);
				}
			}, "Manage player");

			list.add(playerButton);
		});
		add(list);
	}
}