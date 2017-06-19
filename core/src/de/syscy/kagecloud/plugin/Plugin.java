package de.syscy.kagecloud.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.configuration.file.FileConfiguration;

public interface Plugin {
	public File getDataFolder();

	public PluginDescriptionFile getDescription();

	public FileConfiguration getConfig();

	public InputStream getResource(String fileName);

	public void saveConfig();

	public void saveDefaultConfig();

	public void saveResource(String fileName, boolean var2);

	public void reloadConfig();

	public PluginLoader getPluginLoader();

	public KageCloudCore getCloud();

	public boolean isEnabled();

	public void onDisable();

	public void onLoad();

	public void onEnable();

	public boolean isNaggable();

	public void setNaggable(boolean canNag);

	public Logger getLogger();

	public String getName();

	public ExecutorService getExecutorService();
}
