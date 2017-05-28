package de.syscy.kagecloud.command;

import de.syscy.kagecloud.KageCloudBungee;
import de.syscy.kagecloud.network.packet.webinterface.CreateAuthTokenPacket;
import de.syscy.kagecloud.util.UUID;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class WebInterfaceCommand extends Command {
	private final KageCloudBungee bungee;

	public WebInterfaceCommand(KageCloudBungee bungee) {
		super("webInterface", "kagecloud.webInterface");

		this.bungee = bungee;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		UUID authToken = UUID.randomUUID();

		bungee.getClient().sendTCP(new CreateAuthTokenPacket(sender.getName(), authToken));

		String webInterfaceAddress = bungee.getConfig().getString("webInterfaceAddress", "localhost");

		String url = webInterfaceAddress + "/backend/auth?at=" + authToken.toString();
		TextComponent textComponent = new TextComponent(ChatColor.GREEN + "Webinterface: " + url);
		textComponent.setClickEvent(new ClickEvent(Action.OPEN_URL, url));

		sender.sendMessage(textComponent);
	}
}