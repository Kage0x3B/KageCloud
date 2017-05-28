package de.syscy.kagecloud.plugin;

import de.syscy.kagecloud.CommandSender;

public interface TabExecutor {
	public Iterable<String> onTabComplete(CommandSender sender, String[] args);
}