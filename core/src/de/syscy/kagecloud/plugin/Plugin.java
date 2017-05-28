package de.syscy.kagecloud.plugin;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.scheduler.GroupedThreadFactory;
import lombok.Getter;

/**
 * Represents any Plugin that may be loaded at runtime to enhance existing
 * functionality.
 */
@SuppressWarnings("deprecation")
public class Plugin {
	private @Getter PluginDescription description;
	private @Getter KageCloudCore cloud;
	private @Getter File file;
	private @Getter Logger logger;

	/**
	 * Called when the plugin has just been loaded. Most of the proxy will not
	 * be initialized, so only use it for registering
	 * {@link ConfigurationAdapter}'s and other predefined behavior.
	 */
	public void onLoad() {
	}

	/**
	 * Called when this plugin is enabled.
	 */
	public void onEnable() {
	}

	/**
	 * Called when this plugin is disabled.
	 */
	public void onDisable() {
	}

	/**
	 * Gets the data folder where this plugin may store arbitrary data. It will
	 * be a child of {@link ProxyServer#getPluginsFolder()}.
	 *
	 * @return the data folder of this plugin
	 */
	public final File getDataFolder() {
		return new File(getCloud().getPluginsFolder(), getDescription().getName());
	}

	/**
	 * Get a resource from within this plugins jar or container. Care must be
	 * taken to close the returned stream.
	 *
	 * @param name the full path name of this resource
	 * @return the stream for getting this resource, or null if it does not
	 * exist
	 */
	public final InputStream getResourceAsStream(String name) {
		return getClass().getClassLoader().getResourceAsStream(name);
	}

	/**
	 * Called by the loader to initialize the fields in this plugin.
	 *
	 * @param proxy current proxy instance
	 * @param description the description that describes this plugin
	 */
	final void init(KageCloudCore cloud, PluginDescription description) {
		this.cloud = cloud;
		this.description = description;
		file = description.getFile();
		logger = new PluginLogger(this);
	}

	private ExecutorService service;

	@Deprecated
	public ExecutorService getExecutorService() {
		if(service == null) {
			String name = (getDescription() == null) ? "unknown" : getDescription().getName();
			service = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setNameFormat(name + " Pool Thread #%1$d").setThreadFactory(new GroupedThreadFactory(this, name)).build());
		}

		return service;
	}
}
