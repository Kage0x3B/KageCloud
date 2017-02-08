package de.syscy.kagecloud.command;

import de.syscy.kagecloud.KageCloudBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class CreateServerCommand extends Command {
	private final KageCloudBungee core;

	public CreateServerCommand(KageCloudBungee core) {
		super("createServer", "kagecloud.server.create");

		this.core = core;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if(args.length < 1) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Invalid template name!"));

			return;
		}

		String templateName = args[0];

		if(core.createServer(templateName)) {
			sender.sendMessage(new TextComponent(ChatColor.GREEN + "Creating server with template " + templateName + "! This may take a while..."));
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Could not create a new server. Check if you have already started a wrapper"));
		}
	}
}