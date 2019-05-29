package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.RequestPlayerListPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.spigot.network.CloudPluginNetworkListener.IDPacketListener;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KTextInput;
import org.bukkit.Material;

public class PlayerSearchGUI extends KGUI {
	public PlayerSearchGUI(KageCloudSpigot plugin) {
		super();

		setTitle("Player Search");
		setSize(HOPPER_INVENTORY_SIZE);

		KTextInput searchTextInput = new KTextInput(2, 0);
		searchTextInput.setWidth(1);
		searchTextInput.setIcon(new ItemIcon(Material.PAPER));
		searchTextInput.setTitle("Search");
		searchTextInput.setTextInputListener((player, searchQuery) -> {
			if(searchQuery == null || searchQuery.isEmpty()) {
				return;
			}

			plugin.getClient().sendIDPacket(new RequestPlayerListPacket(searchQuery), (IDPacketListener<PlayerListPacket>) (connection, packet) -> {
				KageGUI.showGUI(new PlayerListGUI(plugin, packet.getPlayers()), player);
			});
		});
		add(searchTextInput);
	}
}