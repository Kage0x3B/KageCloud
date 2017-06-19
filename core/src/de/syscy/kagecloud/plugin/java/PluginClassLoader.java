package de.syscy.kagecloud.plugin.java;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.plugin.InvalidPluginException;
import de.syscy.kagecloud.plugin.PluginDescriptionFile;
import de.syscy.kagecloud.util.Validate;

final class PluginClassLoader extends URLClassLoader {
	private final JavaPluginLoader loader;
	private final Map<String, Class<?>> classes = new ConcurrentHashMap<>();
	private final PluginDescriptionFile description;
	private final File dataFolder;
	private final File file;
	final JavaPlugin plugin;
	private JavaPlugin pluginInit;
	private IllegalStateException pluginState;

	static {
		try {
			Method method = ClassLoader.class.getDeclaredMethod("registerAsParallelCapable", new Class[0]);
			if(method != null) {
				boolean oldAccessible = method.isAccessible();
				method.setAccessible(true);
				method.invoke(null, new Object[0]);
				method.setAccessible(oldAccessible);
				KageCloud.logger.log(Level.INFO, "Set PluginClassLoader as parallel capable");
			}
		} catch(NoSuchMethodException v0) {
		} catch(Exception ex) {
			KageCloud.logger.log(Level.WARNING, "Error setting PluginClassLoader as parallel capable", ex);
		}
	}

	@SuppressWarnings("unchecked")
	PluginClassLoader(JavaPluginLoader loader, ClassLoader parent, PluginDescriptionFile description, File dataFolder, File file) throws InvalidPluginException, MalformedURLException {
		super(new URL[] { file.toURI().toURL() }, parent);
		Validate.notNull(loader, "Loader cannot be null");
		this.loader = loader;
		this.description = description;
		this.dataFolder = dataFolder;
		this.file = file;
		try {
			Class<?> jarClass;
			Class<JavaPlugin> pluginClass;
			try {
				jarClass = Class.forName(description.getMain(), true, this);
			} catch(ClassNotFoundException ex) {
				throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
			}
			try {
				pluginClass = (Class<JavaPlugin>) jarClass.asSubclass(JavaPlugin.class);
			} catch(ClassCastException ex) {
				throw new InvalidPluginException("main class `" + description.getMain() + "' does not extend JavaPlugin", ex);
			}
			plugin = pluginClass.newInstance();
		} catch(IllegalAccessException ex) {
			throw new InvalidPluginException("No public constructor", ex);
		} catch(InstantiationException ex) {
			throw new InvalidPluginException("Abnormal plugin type", ex);
		}
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return this.findClass(name, true);
	}

	Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
		if(name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
			throw new ClassNotFoundException(name);
		}
		Class<?> result = classes.get(name);
		if(result == null) {
			if(checkGlobal) {
				result = loader.getClassByName(name);
			}
			if(result == null && (result = super.findClass(name)) != null) {
				loader.setClass(name, result);
			}
			classes.put(name, result);
		}
		return result;
	}

	Set<String> getClasses() {
		return classes.keySet();
	}

	synchronized void initialize(JavaPlugin javaPlugin) {
		Validate.notNull(javaPlugin, "Initializing plugin cannot be null");
		Validate.isTrue(javaPlugin.getClass().getClassLoader() == this, "Cannot initialize plugin outside of this class loader");
		if(plugin != null || pluginInit != null) {
			throw new IllegalArgumentException("Plugin already initialized!", pluginState);
		}
		pluginState = new IllegalStateException("Initial initialization");
		pluginInit = javaPlugin;
		javaPlugin.init(loader, loader.cloud, description, dataFolder, file, this);
	}
}
