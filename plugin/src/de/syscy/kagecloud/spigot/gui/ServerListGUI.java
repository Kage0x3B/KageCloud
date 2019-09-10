package de.syscy.kagecloud.spigot.gui;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import de.syscy.kagecloud.network.packet.info.gui.ServerListPacket.Server;
import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagegui.KageGUI;
import de.syscy.kagegui.icon.ItemIcon;
import de.syscy.kagegui.inventory.KGUI;
import de.syscy.kagegui.inventory.component.KButton;
import de.syscy.kagegui.inventory.component.KList;
import de.syscy.kagegui.inventory.listener.ButtonClickListener;
import de.syscy.kagegui.util.LoreBuilder;

public class ServerListGUI extends KGUI {
	public ServerListGUI(KageCloudSpigot plugin, List<Server> serverList) {
		super();

		setTitle("KageCloud");
		setHeight(6);

		KList list = new KList(0, 0);
		list.setWidth(9);
		list.setHeight(6);

		serverList.forEach(server -> {
			KButton serverButton = new KButton(0, 0);
			serverButton.setTitle(server.getName());
			serverButton.setIcon(new ItemIcon(Material.REPEATING_COMMAND_BLOCK));
			serverButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION, "Template: " + server.getTemplateName());

			if(server.isLobby()) {
				serverButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 1, ChatColor.GREEN + "Lobby Server");
			}

			if(server.isRestricted()) {
				serverButton.getLoreBuilder().set(LoreBuilder.DEFAULT_DESCRIPTION + 2, ChatColor.RED + "Restricted");
			}

			serverButton.setClickListener((button, player) -> KageGUI.showGUI(new ManageServerGUI(plugin, server), player), "Manage server");
			list.add(serverButton);
		});
		add(list);
	}
}