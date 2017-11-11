package de.syscy.kagecloud.spigot.gui;

import de.syscy.kagecloud.network.packet.info.gui.RequestServerListPacket;
import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.spigot.network.CloudPluginNetworkListener.IDPacketListener;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;

import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.esotericsoftware.kryonet.Connection;

public class MainCloudGUI extends KGUI {
	public MainCloudGUI(KageCloudSpigot plugin) {
		super();

		setTitle("KageCloud");
		setSize(HOPPER_INVENTORY_SIZE);

		KButton serverButton = new KButton(0, 0);
		serverButton.setTitle("Server");
		serverButton.setIcon(new ItemIcon(Material.COMMAND_MINECART));
		serverButton.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				plugin.getClient().sendIDPacket(new RequestServerListPacket(), new IDPacketListener<ServerListPacket>() {
					@Override
					public void received(Connection connection, ServerListPacket packet) {
						KageGUI.showGUI(new ServerListGUI(plugin, packet.getServer()), player);
					}
				});
			}
		}, "Open running server list");
		add(serverButton);

		ItemStack skullItemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());

		KButton findPlayer = new KButton(1, 0);
		findPlayer.setTitle("Find player");
		findPlayer.setIcon(new ItemIcon(skullItemStack));
		findPlayer.setClickListener(new ButtonClickListener() {
			@Override
			public void onClick(KButton button, Player player) {
				KageGUI.showGUI(new PlayerSearchGUI(plugin), player);
			}
		}, "Search for a player on the network");
		add(findPlayer);
	}
}