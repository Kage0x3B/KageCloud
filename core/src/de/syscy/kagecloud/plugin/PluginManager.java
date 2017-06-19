package de.syscy.kagecloud.plugin;

import java.io.File;

public interface PluginManager {
	public void registerInterface(Class<? extends PluginLoader> var1) throws IllegalArgumentException;

	public Plugin getPlugin(String var1);

	public Plugin[] getPlugins();

	public boolean isPluginEnabled(String var1);

	public boolean isPluginEnabled(Plugin var1);

	public Plugin loadPlugin(File var1) throws InvalidPluginException, InvalidDescriptionException, UnknownDependencyException;

	public Plugin[] loadPlugins(File var1);

	public void disablePlugins();

	public void clearPlugins();

	public void callEvent(Event var1) throws IllegalStateException;

	public void registerEvents(Listener var1, Plugin var2);

	public void enablePlugin(Plugin var1);

	public void disablePlugin(Plugin var1);

	public boolean useTimings();
}