package de.syscy.kagecloud.plugin;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudCore;
import de.syscy.kagecloud.event.EventManager;
import de.syscy.kagecloud.plugin.java.JavaPlugin;
import de.syscy.kagecloud.util.Validate;

import com.google.common.base.Preconditions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;

import lombok.Getter;

public final class SimplePluginManager implements PluginManager {
	private final KageCloudCore cloud;

	private final @Getter JavaPlugin corePlugin;

	private final EventManager eventManager;
	private final Map<Pattern, PluginLoader> fileAssociations = new HashMap<>();
	private final List<Plugin> plugins = new ArrayList<>();
	private final Map<String, Plugin> lookupNames = new HashMap<>();
	private final Multimap<Plugin, Listener> listenersByPlugin = ArrayListMultimap.create();
	private boolean useTimings = false;

	public SimplePluginManager(KageCloudCore cloud) {
		this.cloud = cloud;

		eventManager = new EventManager(KageCloud.logger);
		corePlugin = new JavaPlugin(null, new PluginDescriptionFile("Core", "", ""), null, null);
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	@Override
	public void registerInterface(Class<? extends PluginLoader> loader) throws IllegalArgumentException {
		PluginLoader instance;
		if(PluginLoader.class.isAssignableFrom(loader)) {
			try {
				Constructor<? extends PluginLoader> constructor = loader.getConstructor(KageCloudCore.class);
				instance = constructor.newInstance(cloud);
			} catch(NoSuchMethodException ex) {
				String className = loader.getName();
				throw new IllegalArgumentException(String.format("Class %s does not have a public %s(KageCloudCore) constructor", className, className), ex);
			} catch(Exception ex) {
				throw new IllegalArgumentException(String.format("Unexpected exception %s while attempting to construct a new instance of %s", ex.getClass().getName(), loader.getName()), ex);
			}
		} else {
			throw new IllegalArgumentException(String.format("Class %s does not implement interface PluginLoader", loader.getName()));
		}
		Pattern[] patterns = instance.getPluginFileFilters();
		SimplePluginManager ex = this;
		synchronized(ex) {
			Pattern[] arrpattern = patterns;
			int n = arrpattern.length;
			int n2 = 0;
			while(n2 < n) {
				Pattern pattern = arrpattern[n2];
				fileAssociations.put(pattern, instance);
				++n2;
			}
		}
	}

	@Override
	public Plugin[] loadPlugins(File directory) {
		Validate.notNull(directory, "Directory cannot be null");
		Validate.isTrue(directory.isDirectory(), "Directory must be a directory");
		ArrayList<Plugin> result = new ArrayList<>();
		Set<Pattern> filters = fileAssociations.keySet();
		HashMap<String, File> plugins = new HashMap<>();
		HashSet<String> loadedPlugins = new HashSet<>();
		HashMap<String, LinkedList<String>> dependencies = new HashMap<>();
		HashMap<String, List<String>> softDependencies = new HashMap<>();
		File[] arrfile = directory.listFiles();
		int n = arrfile.length;
		int n2 = 0;
		while(n2 < n) {
			block30: {
				File file = arrfile[n2];
				PluginLoader loader = null;
				for(Pattern filter : filters) {
					Matcher match = filter.matcher(file.getName());
					if(!match.find()) {
						continue;
					}
					loader = fileAssociations.get(filter);
				}
				if(loader != null) {
					List<String> dependencySet;
					List<String> loadBeforeSet;
					File replacedFile;
					PluginDescriptionFile description;
					List<String> softDependencySet;
					block31: {
						description = null;
						try {
							description = loader.getPluginDescription(file);
							String name = description.getName();
							if(name.equalsIgnoreCase("bukkit") || name.equalsIgnoreCase("minecraft") || name.equalsIgnoreCase("mojang")) {
								KageCloud.logger.log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': Restricted Name");
								break block30;
							}
							if(description.rawName.indexOf(32) == -1) {
								break block31;
							}
							KageCloud.logger.warning(String.format("Plugin `%s' uses the space-character (0x20) in its name `%s' - this is discouraged", description.getFullName(), description.rawName));
						} catch(InvalidDescriptionException ex) {
							KageCloud.logger.log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
							break block30;
						}
					}
					if((replacedFile = plugins.put(description.getName(), file)) != null) {
						KageCloud.logger.severe(String.format("Ambiguous plugin name `%s' for files `%s' and `%s' in `%s'", description.getName(), file.getPath(), replacedFile.getPath(), directory.getPath()));
					}
					if((softDependencySet = description.getSoftDepend()) != null && !softDependencySet.isEmpty()) {
						if(softDependencies.containsKey(description.getName())) {
							softDependencies.get(description.getName()).addAll(softDependencySet);
						} else {
							softDependencies.put(description.getName(), new LinkedList<>(softDependencySet));
						}
					}
					if((dependencySet = description.getDepend()) != null && !dependencySet.isEmpty()) {
						dependencies.put(description.getName(), new LinkedList<>(dependencySet));
					}
					if((loadBeforeSet = description.getLoadBefore()) != null && !loadBeforeSet.isEmpty()) {
						for(String loadBeforeTarget : loadBeforeSet) {
							if(softDependencies.containsKey(loadBeforeTarget)) {
								((Collection<String>) softDependencies.get(loadBeforeTarget)).add(description.getName());
								continue;
							}
							LinkedList<String> shortSoftDependency = new LinkedList<>();
							shortSoftDependency.add(description.getName());
							softDependencies.put(loadBeforeTarget, shortSoftDependency);
						}
					}
				}
			}
			++n2;
		}
		while(!plugins.isEmpty()) {
			String plugin;
			File file;
			boolean missingDependency = true;
			Iterator<String> pluginIterator = plugins.keySet().iterator();

			while(pluginIterator.hasNext()) {
				plugin = (String) pluginIterator.next();

				if(dependencies.containsKey(plugin)) {
					Iterator<String> dependencyIterator = dependencies.get(plugin).iterator();

					while(dependencyIterator.hasNext()) {
						String dependency = (String) dependencyIterator.next();

						if(loadedPlugins.contains(dependency)) {
							dependencyIterator.remove();
							continue;
						}

						if(plugins.containsKey(dependency)) {
							continue;
						}

						missingDependency = false;
						File file2 = plugins.get(plugin);
						pluginIterator.remove();
						softDependencies.remove(plugin);
						dependencies.remove(plugin);

						KageCloud.logger.log(Level.SEVERE, "Could not load '" + file2.getPath() + "' in folder '" + directory.getPath() + "'", new UnknownDependencyException(dependency));

						break;
					}

					if(dependencies.containsKey(plugin) && ((Collection<String>) dependencies.get(plugin)).isEmpty()) {
						dependencies.remove(plugin);
					}
				}

				if(softDependencies.containsKey(plugin)) {
					Iterator<String> softDependencyIterator = softDependencies.get(plugin).iterator();

					while(softDependencyIterator.hasNext()) {
						String softDependency = (String) softDependencyIterator.next();

						if(plugins.containsKey(softDependency)) {
							continue;
						}

						softDependencyIterator.remove();
					}

					if(softDependencies.get(plugin).isEmpty()) {
						softDependencies.remove(plugin);
					}
				}

				if(dependencies.containsKey(plugin) || softDependencies.containsKey(plugin) || !plugins.containsKey(plugin)) {
					continue;
				}

				file = (File) plugins.get(plugin);
				pluginIterator.remove();
				missingDependency = false;

				try {
					result.add(loadPlugin(file));
					loadedPlugins.add(plugin);

					continue;
				} catch(InvalidPluginException ex) {
					KageCloud.logger.log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
				}
			}

			if(!missingDependency) {
				continue;
			}

			pluginIterator = plugins.keySet().iterator();

			while(pluginIterator.hasNext()) {
				plugin = (String) pluginIterator.next();

				if(dependencies.containsKey(plugin)) {
					continue;
				}

				softDependencies.remove(plugin);
				missingDependency = false;
				file = (File) plugins.get(plugin);
				pluginIterator.remove();

				try {
					result.add(loadPlugin(file));
					loadedPlugins.add(plugin);

					break;
				} catch(InvalidPluginException ex) {
					KageCloud.logger.log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "'", ex);
				}
			}

			if(!missingDependency) {
				continue;
			}

			softDependencies.clear();
			dependencies.clear();
			Iterator<File> failedPluginIterator = plugins.values().iterator();

			while(failedPluginIterator.hasNext()) {
				file = (File) failedPluginIterator.next();
				failedPluginIterator.remove();

				KageCloud.logger.log(Level.SEVERE, "Could not load '" + file.getPath() + "' in folder '" + directory.getPath() + "': circular dependency detected");
			}
		}

		return result.toArray(new Plugin[result.size()]);
	}

	@Override
	public synchronized Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
		Validate.notNull(file, "File cannot be null");
		Set<Pattern> filters = fileAssociations.keySet();
		Plugin result = null;

		for(Pattern filter : filters) {
			Matcher match = filter.matcher(file.getName());

			if(!match.find()) {
				continue;
			}

			PluginLoader loader = fileAssociations.get(filter);
			result = loader.loadPlugin(file);
		}

		if(result != null) {
			plugins.add(result);
			lookupNames.put(result.getDescription().getName(), result);
		}

		return result;
	}

	@Override
	public synchronized Plugin getPlugin(String name) {
		return lookupNames.get(name.replace(' ', '_'));
	}

	@Override
	public synchronized Plugin[] getPlugins() {
		return plugins.toArray(new Plugin[0]);
	}

	@Override
	public boolean isPluginEnabled(String name) {
		Plugin plugin = getPlugin(name);

		return this.isPluginEnabled(plugin);
	}

	@Override
	public boolean isPluginEnabled(Plugin plugin) {
		if(plugin != null && plugins.contains(plugin)) {
			return plugin.isEnabled();
		}

		return false;
	}

	@Override
	public void enablePlugin(Plugin plugin) {
		if(!plugin.isEnabled()) {
			try {
				plugin.getPluginLoader().enablePlugin(plugin);
			} catch(Throwable ex) {
				KageCloud.logger.log(Level.SEVERE, "Error occurred (in the plugin loader) while enabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}
		}
	}

	@Override
	public void disablePlugins() {
		Plugin[] plugins = getPlugins();
		int i2 = plugins.length - 1;

		while(i2 >= 0) {
			disablePlugin(plugins[i2]);
			--i2;
		}
	}

	@Override
	public void disablePlugin(Plugin plugin) {
		if(plugin.isEnabled()) {
			try {
				plugin.getPluginLoader().disablePlugin(plugin);
			} catch(Throwable ex) {
				KageCloud.logger.log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}
			try {
				cloud.getScheduler().cancel(plugin);
			} catch(Throwable ex) {
				KageCloud.logger.log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
			}

			unregisterListeners(plugin);
		}
	}

	/*
	 * WARNING - Removed try catching itself - possible behaviour change.
	 */
	@Override
	public void clearPlugins() {
		SimplePluginManager simplePluginManager = this;
		synchronized(simplePluginManager) {
			disablePlugins();
			plugins.clear();
			lookupNames.clear();
			fileAssociations.clear();
		}
	}

	@Override
	public boolean useTimings() {
		return useTimings;
	}

	public void useTimings(boolean use) {
		useTimings = use;
	}

	@Override
	public void callEvent(Event event) throws IllegalStateException {
		Preconditions.checkNotNull(event, "event");

		long start = System.nanoTime();
		eventManager.fire(event);
		long elapsed = System.nanoTime() - start;

		if(elapsed > 250000000) {
			KageCloud.logger.log(Level.WARNING, "Event {0} took {1}ns to process!", new Object[] { event, elapsed });
		}
	}

	@Override
	public void registerEvents(Listener listener, Plugin plugin) {
		for(Method method : listener.getClass().getDeclaredMethods()) {
			Preconditions.checkArgument(!method.isAnnotationPresent(Subscribe.class), "Listener %s has registered using deprecated subscribe annotation! Please update to @EventHandler.", listener);
		}
		eventManager.register(listener);
		listenersByPlugin.put(plugin, listener);
	}

	/**
	 * Unregister a {@link Listener} so that the events do not reach it anymore.
	 *
	 * @param listener the listener to unregister
	 */
	public void unregisterListener(Listener listener) {
		eventManager.unregister(listener);
		listenersByPlugin.values().remove(listener);
	}

	/**
	 * Unregister all of a Plugin's listener.
	 */
	public void unregisterListeners(Plugin plugin) {
		for(Iterator<Listener> it = listenersByPlugin.get(plugin).iterator(); it.hasNext();) {
			eventManager.unregister(it.next());
			it.remove();
		}
	}
}