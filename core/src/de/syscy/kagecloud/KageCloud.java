package de.syscy.kagecloud;

import java.io.File;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import de.syscy.kagecloud.chat.BaseComponent;
import de.syscy.kagecloud.chat.ComponentSerializer;
import de.syscy.kagecloud.chat.TextComponent;
import de.syscy.kagecloud.chat.TextComponentSerializer;
import de.syscy.kagecloud.chat.TranslatableComponent;
import de.syscy.kagecloud.chat.TranslatableComponentSerializer;
import de.syscy.kagecloud.event.EventManager;

public class KageCloud {
	public static ICloudNode cloudNode;

	public static Logger logger;

	public static File dataFolder;

	public static EventManager eventManager;

	public static final Gson gson = new GsonBuilder().registerTypeAdapter(BaseComponent.class, new ComponentSerializer()).registerTypeAdapter(TextComponent.class, new TextComponentSerializer()).registerTypeAdapter(TranslatableComponent.class, new TranslatableComponentSerializer()).create();
}