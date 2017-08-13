package de.syscy.kagecloud.spigot.command;

import org.bukkit.entity.Player;

import de.syscy.kagecloud.spigot.KageCloudSpigot;
import de.syscy.kagecloud.spigot.gui.MainCloudGUI;
import de.syscy.kagecore.command.CommandManager;
import de.syscy.kagecore.command.PlayerCommandBase;
import de.syscy.kagegui.KageGUI;

public class CloudCommandManager extends CommandManager<KageCloudSpigot> {
	public CloudCommandManager(KageCloudSpigot plugin) {
		super(plugin, "cloud");

		addCommand(new PlayerCommandBase<KageCloudSpigot>(plugin, "manage") {
			@Override
			public void onPlayerCommand(Player player) {
				KageGUI.showGUI(new MainCloudGUI(plugin), player);
			}
		});
	}
}