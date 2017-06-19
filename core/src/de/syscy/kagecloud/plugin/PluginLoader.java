package de.syscy.kagecloud.plugin;

import java.io.File;
import java.util.regex.Pattern;

public interface PluginLoader {
	public Plugin loadPlugin(File var1) throws InvalidPluginException, UnknownDependencyException;

	public PluginDescriptionFile getPluginDescription(File var1) throws InvalidDescriptionException;

	public Pattern[] getPluginFileFilters();

	public void enablePlugin(Plugin var1);

	public void disablePlugin(Plugin var1);
}
