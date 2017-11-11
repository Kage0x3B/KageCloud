package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.PlayerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.RequestPlayerListPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.spigot.network.CloudPluginNetworkListener.IDPacketListener;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KTextInput;
import de.syscy.kagegui.inventory.listener.TextInputListener;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.esotericsoftware.kryonet.Connection;

public class PlayerSearchGUI extends KGUI {
	public PlayerSearchGUI(KageCloudSpigot plugin) {
		super();

		setTitle("Player Search");
		setSize(HOPPER_INVENTORY_SIZE);

		KTextInput searchTextInput = new KTextInput(0, 0);
		searchTextInput.setWidth(5);
		searchTextInput.setIcon(new ItemIcon(Material.PAPER));
		searchTextInput.setTitle("Search");
		searchTextInput.setTextInputListener(new TextInputListener() {
			@Override
			public void onTextInput(Player player, String searchQuery) {
				if(searchQuery == null || searchQuery.trim().isEmpty()) {
					return;
				}

				plugin.getClient().sendIDPacket(new RequestPlayerListPacket(searchQuery), new IDPacketListener<PlayerListPacket>() {
					@Override
					public void received(Connection connection, PlayerListPacket packet) {
						KageGUI.showGUI(new PlayerListGUI(plugin, packet.getPlayers()), player);
					}
				});
			}
		});
		add(searchTextInput);
	}
}