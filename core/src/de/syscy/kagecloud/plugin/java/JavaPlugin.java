package de.syscy.kagecloud.plugin.java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.configuration.file.FileConfiguration;
import de.syscy.kagecloud.configuration.file.YamlConfiguration;
import de.syscy.kagecloud.plugin.PluginBase;
import de.syscy.kagecloud.plugin.PluginDescriptionFile;
import de.syscy.kagecloud.plugin.PluginLoader;
import de.syscy.kagecloud.plugin.PluginLogger;
import de.syscy.kagecloud.scheduler.GroupedThreadFactory;
import de.syscy.kagecloud.util.Validate;

public class JavaPlugin extends PluginBase {
	private boolean isEnabled = false;
	private PluginLoader loader = null;
	private KageCloudCore cloud = null;
	private File file = null;
	private PluginDescriptionFile description = null;
	private File dataFolder = null;
	private ClassLoader classLoader = null;
	private boolean naggable = true;
	private FileConfiguration newConfig = null;
	private File configFile = null;
	private PluginLogger logger = null;

	public JavaPlugin() {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if(!(classLoader instanceof PluginClassLoader)) {
			throw new IllegalStateException("JavaPlugin requires " + PluginClassLoader.class.getName());
		}
		((PluginClassLoader) classLoader).initialize(this);
	}

	public JavaPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		ClassLoader classLoader = this.getClass().getClassLoader();
		if(!description.getName().equals("Core") && classLoader instanceof PluginClassLoader) {
			throw new IllegalStateException("Cannot use initialization constructor at runtime");
		}
		init(loader, loader == null ? null : loader.cloud, description, dataFolder, file, classLoader);
	}

	@Override
	public final File getDataFolder() {
		return dataFolder;
	}

	@Override
	public final PluginLoader getPluginLoader() {
		return loader;
	}

	@Override
	public final KageCloudCore getCloud() {
		return cloud;
	}

	@Override
	public final boolean isEnabled() {
		return isEnabled;
	}

	protected File getFile() {
		return file;
	}

	@Override
	public final PluginDescriptionFile getDescription() {
		return description;
	}

	@Override
	public FileConfiguration getConfig() {
		if(newConfig == null) {
			reloadConfig();
		}
		return newConfig;
	}

	protected final Reader getTextResource(String file) {
		InputStream in = getResource(file);
		return in == null ? null : new InputStreamReader(in, Charsets.UTF_8);
	}

	@Override
	public void reloadConfig() {
		newConfig = YamlConfiguration.loadConfiguration(configFile);
		InputStream defConfigStream = getResource("config.yml");
		if(defConfigStream == null) {
			return;
		}
		newConfig.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
	}

	@Override
	public void saveConfig() {
		try {
			getConfig().save(configFile);
		} catch(IOException ex) {
			logger.log(Level.SEVERE, "Could not save config to " + configFile, ex);
		}
	}

	@Override
	public void saveDefaultConfig() {
		if(!configFile.exists()) {
			saveResource("config.yml", false);
		}
	}

	@Override
	public void saveResource(String resourcePath, boolean replace) {
		if(resourcePath == null || resourcePath.equals("")) {
			throw new IllegalArgumentException("ResourcePath cannot be null or empty");
		}
		InputStream in = getResource(resourcePath = resourcePath.replace('\\', '/'));
		if(in == null) {
			throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + file);
		}
		File outFile = new File(dataFolder, resourcePath);
		int lastIndex = resourcePath.lastIndexOf(47);
		File outDir = new File(dataFolder, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));
		if(!outDir.exists()) {
			outDir.mkdirs();
		}
		try {
			if(!outFile.exists() || replace) {
				int len;
				FileOutputStream out = new FileOutputStream(outFile);
				byte[] buf = new byte[1024];
				while((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				in.close();
			} else {
				logger.log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
			}
		} catch(IOException ex) {
			logger.log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
		}
	}

	@Override
	public InputStream getResource(String filename) {
		if(filename == null) {
			throw new IllegalArgumentException("Filename cannot be null");
		}
		try {
			URL url = getClassLoader().getResource(filename);

			if(url == null) {
				return null;
			}

			URLConnection connection = url.openConnection();
			connection.setUseCaches(false);
			return connection.getInputStream();
		} catch(IOException v0) {
			return null;
		}
	}

	protected final ClassLoader getClassLoader() {
		return classLoader;
	}

	protected final void setEnabled(boolean enabled) {
		if(isEnabled != enabled) {
			isEnabled = enabled;
			if(isEnabled) {
				onEnable();
			} else {
				onDisable();
			}
		}
	}

	final void init(PluginLoader loader, KageCloudCore cloud, PluginDescriptionFile description, File dataFolder, File file, ClassLoader classLoader) {
		this.loader = loader;
		this.cloud = cloud;
		this.file = file;
		this.description = description;
		this.dataFolder = dataFolder;
		this.classLoader = classLoader;

		if(dataFolder != null) {
			configFile = new File(dataFolder, "config.yml");
		}

		logger = new PluginLogger(this);
	}

	@Override
	public void onLoad() {
	}

	@Override
	public void onDisable() {
	}

	@Override
	public void onEnable() {
	}

	@Override
	public final boolean isNaggable() {
		return naggable;
	}

	@Override
	public final void setNaggable(boolean canNag) {
		naggable = canNag;
	}

	@Override
	public final Logger getLogger() {
		return logger;
	}

	@Override
	public String toString() {
		return description.getFullName();
	}

	@SuppressWarnings("unchecked")
	public static <T extends JavaPlugin> T getPlugin(Class<T> clazz) {
		Validate.notNull(clazz, "Null class cannot have a plugin");
		if(!JavaPlugin.class.isAssignableFrom(clazz)) {
			throw new IllegalArgumentException(clazz + " does not extend " + JavaPlugin.class);
		}
		ClassLoader cl = clazz.getClassLoader();
		if(!(cl instanceof PluginClassLoader)) {
			throw new IllegalArgumentException(clazz + " is not initialized by " + PluginClassLoader.class);
		}
		JavaPlugin plugin = ((PluginClassLoader) cl).plugin;
		if(plugin == null) {
			throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
		}
		return (T) ((JavaPlugin) clazz.cast(plugin));
	}

	public static JavaPlugin getProvidingPlugin(Class<?> clazz) {
		Validate.notNull(clazz, "Null class cannot have a plugin");
		ClassLoader cl = clazz.getClassLoader();
		if(!(cl instanceof PluginClassLoader)) {
			throw new IllegalArgumentException(clazz + " is not provided by " + PluginClassLoader.class);
		}
		JavaPlugin plugin = ((PluginClassLoader) cl).plugin;
		if(plugin == null) {
			throw new IllegalStateException("Cannot get plugin for " + clazz + " from a static initializer");
		}
		return plugin;
	}

	private ExecutorService service;

	@Override
	@Deprecated
	public ExecutorService getExecutorService() {
		if(service == null) {
			String name = (getDescription() == null) ? "unknown" : getDescription().getName();
			service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + " Pool Thread #%1$d").setThreadFactory(new GroupedThreadFactory(this, name)).build());
		}

		return service;
	}
}
