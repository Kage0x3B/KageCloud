package de.syscy.kagecloud.plugin.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.regex.Pattern;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.configuration.serialization.ConfigurationSerializable;
import de.syscy.kagecloud.configuration.serialization.ConfigurationSerialization;
import de.syscy.kagecloud.plugin.InvalidDescriptionException;
import de.syscy.kagecloud.plugin.InvalidPluginException;
import de.syscy.kagecloud.plugin.Plugin;
import de.syscy.kagecloud.plugin.PluginDescriptionFile;
import de.syscy.kagecloud.plugin.PluginLoader;
import de.syscy.kagecloud.plugin.UnknownDependencyException;
import de.syscy.kagecloud.util.Validate;

import org.yaml.snakeyaml.error.YAMLException;

public final class JavaPluginLoader implements PluginLoader {
	final KageCloudCore cloud;
	private final Pattern[] fileFilters = new Pattern[] { Pattern.compile("\\.jar$") };
	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	private final List<PluginClassLoader> loaders = new CopyOnWriteArrayList<>();

	@Deprecated
	public JavaPluginLoader(KageCloudCore cloud) {
		this.cloud = cloud;
	}

	@Override
	public Plugin loadPlugin(File file) throws InvalidPluginException {
		PluginClassLoader loader;
		PluginDescriptionFile description;

		if(!file.exists()) {
			throw new InvalidPluginException(new FileNotFoundException(String.valueOf(file.getPath()) + " does not exist"));
		}
		try {
			description = getPluginDescription(file);
		} catch(InvalidDescriptionException ex) {
			throw new InvalidPluginException(ex);
		}
		File parentFile = file.getParentFile();
		File dataFolder = new File(parentFile, description.getName());
		@SuppressWarnings("deprecation")
		File oldDataFolder = new File(parentFile, description.getRawName());
		if(!dataFolder.equals(oldDataFolder)) {
			if(dataFolder.isDirectory() && oldDataFolder.isDirectory()) {
				KageCloud.logger.warning(String.format("While loading %s (%s) found old-data folder: `%s' next to the new one `%s'", description.getFullName(), file, oldDataFolder, dataFolder));
			} else if(oldDataFolder.isDirectory() && !dataFolder.exists()) {
				if(!oldDataFolder.renameTo(dataFolder)) {
					throw new InvalidPluginException("Unable to rename old data folder: `" + oldDataFolder + "' to: `" + dataFolder + "'");
				}
				KageCloud.logger.log(Level.INFO, String.format("While loading %s (%s) renamed data folder: `%s' to `%s'", description.getFullName(), file, oldDataFolder, dataFolder));
			}
		}
		if(dataFolder.exists() && !dataFolder.isDirectory()) {
			throw new InvalidPluginException(String.format("Projected datafolder: `%s' for %s (%s) exists and is not a directory", dataFolder, description.getFullName(), file));
		}
		for(String pluginName : description.getDepend()) {
			Plugin current = cloud.getPluginManager().getPlugin(pluginName);
			if(current != null) {
				continue;
			}
			throw new UnknownDependencyException(pluginName);
		}
		try {
			loader = new PluginClassLoader(this, this.getClass().getClassLoader(), description, dataFolder, file);
		} catch(InvalidPluginException ex) {
			throw ex;
		} catch(Throwable ex) {
			throw new InvalidPluginException(ex);
		}
		loaders.add(loader);
		return loader.plugin;
	}

	/*
	 * Loose catch block
	 * Enabled aggressive block sorting
	 * Enabled unnecessary exception pruning
	 * Enabled aggressive exception aggregation
	 * Lifted jumps to return sites
	 */
	@Override
	public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
		Validate.notNull(file, "File cannot be null");

		JarFile jar = null;
		InputStream stream = null;

		try {
			jar = new JarFile(file);
			JarEntry entry = jar.getJarEntry("cloud.yml");

			if(entry == null) {
				throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain cloud.yml"));
			}

			stream = jar.getInputStream(entry);

			return new PluginDescriptionFile(stream);

		} catch(IOException ex) {
			throw new InvalidDescriptionException(ex);
		} catch(YAMLException ex) {
			throw new InvalidDescriptionException(ex);
		} finally {
			if(jar != null) {
				try {
					jar.close();
				} catch(IOException e) {
				}
			}
			if(stream != null) {
				try {
					stream.close();
				} catch(IOException e) {
				}
			}
		}
	}

	@Override
	public Pattern[] getPluginFileFilters() {
		return (Pattern[]) fileFilters.clone();
	}

	Class<?> getClassByName(String name) {
		Class<?> cachedClass = classes.get(name);
		if(cachedClass != null) {
			return cachedClass;
		}
		for(PluginClassLoader loader : loaders) {
			try {
				cachedClass = loader.findClass(name, false);
			} catch(ClassNotFoundException v0) {
			}
			if(cachedClass == null) {
				continue;
			}
			return cachedClass;
		}
		return null;
	}

	void setClass(String name, Class<?> clazz) {
		if(!classes.containsKey(name)) {
			classes.put(name, clazz);
			if(ConfigurationSerializable.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<ConfigurationSerializable> serializable = (Class<ConfigurationSerializable>) clazz.asSubclass(ConfigurationSerializable.class);
				ConfigurationSerialization.registerClass(serializable);
			}
		}
	}

	private void removeClass(String name) {
		Class<?> clazz = classes.remove(name);
		try {
			if(clazz != null && ConfigurationSerializable.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<ConfigurationSerializable> serializable = (Class<ConfigurationSerializable>) clazz.asSubclass(ConfigurationSerializable.class);
				ConfigurationSerialization.unregisterClass(serializable);
			}
		} catch(NullPointerException v0) {
		}
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");
		if(!plugin.isEnabled()) {
			plugin.getLogger().info("Enabling " + plugin.getDescription().getFullName());
			JavaPlugin jPlugin = (JavaPlugin) plugin;
			PluginClassLoader pluginLoader = (PluginClassLoader) jPlugin.getClassLoader();
			if(!loaders.contains(pluginLoader)) {
				loaders.add(pluginLoader);
				KageCloud.logger.log(Level.WARNING, "Enabled plugin with unregistered PluginClassLoader " + plugin.getDescription().getFullName());
			}
			try {
				jPlugin.setEnabled(true);
			} catch(Throwable ex) {
				KageCloud.logger.log(Level.SEVERE, "Error occurred while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}
		}
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		Validate.isTrue(plugin instanceof JavaPlugin, "Plugin is not associated with this PluginLoader");
		if(plugin.isEnabled()) {
			String message = String.format("Disabling %s", plugin.getDescription().getFullName());
			plugin.getLogger().info(message);
			JavaPlugin jPlugin = (JavaPlugin) plugin;
			ClassLoader cloader = jPlugin.getClassLoader();
			try {
				jPlugin.setEnabled(false);
			} catch(Throwable ex) {
				KageCloud.logger.log(Level.SEVERE, "Error occurred while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}
			if(cloader instanceof PluginClassLoader) {
				PluginClassLoader loader = (PluginClassLoader) cloader;
				loaders.remove(loader);
				Set<String> names = loader.getClasses();
				for(String name : names) {
					removeClass(name);
				}
			}
		}
	}

}
