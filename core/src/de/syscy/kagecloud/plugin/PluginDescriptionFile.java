package de.syscy.kagecloud.plugin;

import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

import com.google.common.collect.ImmutableList;

public final class PluginDescriptionFile {
	private static final ThreadLocal<Yaml> YAML = new ThreadLocal<Yaml>() {
		@Override
		protected Yaml initialValue() {
			return new Yaml(new SafeConstructor() {
			});
		}
	};
	String rawName = null;
	private String name = null;
	private String main = null;
	private String classLoaderOf = null;
	private List<String> depend = ImmutableList.of();
	private List<String> softDepend = ImmutableList.of();
	private List<String> loadBefore = ImmutableList.of();
	private String version = null;
	private String description = null;
	private List<String> authors = null;
	private String website = null;
	private String prefix = null;

	public PluginDescriptionFile(InputStream stream) throws InvalidDescriptionException {
		loadMap(asMap(YAML.get().load(stream)));
	}

	public PluginDescriptionFile(Reader reader) throws InvalidDescriptionException {
		loadMap(asMap(YAML.get().load(reader)));
	}

	public PluginDescriptionFile(String pluginName, String pluginVersion, String mainClass) {
		name = pluginName.replace(' ', '_');
		version = pluginVersion;
		main = mainClass;
	}

	public String getName() {
		return name;
	}

	public String getVersion() {
		return version;
	}

	public String getMain() {
		return main;
	}

	public String getDescription() {
		return description;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public String getWebsite() {
		return website;
	}

	public List<String> getDepend() {
		return depend;
	}

	public List<String> getSoftDepend() {
		return softDepend;
	}

	public List<String> getLoadBefore() {
		return loadBefore;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getFullName() {
		return String.valueOf(name) + " v" + version;
	}

	@Deprecated
	public String getClassLoaderOf() {
		return classLoaderOf;
	}

	public void save(Writer writer) {
		YAML.get().dump(saveMap(), writer);
	}

	private void loadMap(Map<?, ?> map) throws InvalidDescriptionException {
		try {
			name = rawName = map.get("name").toString();
			if(!name.matches("^[A-Za-z0-9 _.-]+$")) {
				throw new InvalidDescriptionException("name '" + name + "' contains invalid characters.");
			}
			name = name.replace(' ', '_');
		} catch(NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "name is not defined");
		} catch(ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "name is of wrong type");
		}

		try {
			version = map.get("version").toString();
		} catch(NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "version is not defined");
		} catch(ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "version is of wrong type");
		}

		try {
			main = map.get("main").toString();
			if(main.startsWith("org.bukkit.")) {
				throw new InvalidDescriptionException("main may not be within the org.bukkit namespace");
			}
		} catch(NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "main is not defined");
		} catch(ClassCastException ex) {
			throw new InvalidDescriptionException(ex, "main is of wrong type");
		}

		if(map.get("class-loader-of") != null) {
			classLoaderOf = map.get("class-loader-of").toString();
		}

		depend = PluginDescriptionFile.makePluginNameList(map, "depend");
		softDepend = PluginDescriptionFile.makePluginNameList(map, "softdepend");
		loadBefore = PluginDescriptionFile.makePluginNameList(map, "loadbefore");

		if(map.get("website") != null) {
			website = map.get("website").toString();
		}

		if(map.get("description") != null) {
			description = map.get("description").toString();
		}

		if(map.get("authors") != null) {
			ImmutableList.Builder<String> authorsBuilder = ImmutableList.builder();
			if(map.get("author") != null) {
				authorsBuilder.add(map.get("author").toString());
			}
			try {
				for(Object o : (Iterable<?>) map.get("authors")) {
					authorsBuilder.add(o.toString());
				}
			} catch(ClassCastException ex) {
				throw new InvalidDescriptionException(ex, "authors are of wrong type");
			} catch(NullPointerException ex) {
				throw new InvalidDescriptionException(ex, "authors are improperly defined");
			}
			authors = authorsBuilder.build();
		} else {
			authors = map.get("author") != null ? ImmutableList.of(map.get("author").toString()) : ImmutableList.of();
		}

		if(map.get("prefix") != null) {
			prefix = map.get("prefix").toString();
		}
	}

	private static List<String> makePluginNameList(Map<?, ?> map, String key) throws InvalidDescriptionException {
		Object value = map.get(key);

		if(value == null) {
			return ImmutableList.of();
		}

		ImmutableList.Builder<String> builder = ImmutableList.builder();

		try {
			for(Object entry : (Iterable<?>) value) {
				builder.add(entry.toString().replace(' ', '_'));
			}
		} catch(ClassCastException ex) {
			throw new InvalidDescriptionException(ex, String.valueOf(key) + " is of wrong type");
		} catch(NullPointerException ex) {
			throw new InvalidDescriptionException(ex, "invalid " + key + " format");
		}

		return builder.build();
	}

	private Map<String, Object> saveMap() {
		HashMap<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("main", main);
		map.put("version", version);

		if(depend != null) {
			map.put("depend", depend);
		}

		if(softDepend != null) {
			map.put("softdepend", softDepend);
		}

		if(website != null) {
			map.put("website", website);
		}

		if(description != null) {
			map.put("description", description);
		}

		if(authors.size() == 1) {
			map.put("author", authors.get(0));
		} else if(authors.size() > 1) {
			map.put("authors", authors);
		}

		if(classLoaderOf != null) {
			map.put("class-loader-of", classLoaderOf);
		}

		if(prefix != null) {
			map.put("prefix", prefix);
		}

		return map;
	}

	private Map<?, ?> asMap(Object object) throws InvalidDescriptionException {
		if(object instanceof Map) {
			return (Map<?, ?>) object;
		}

		throw new InvalidDescriptionException(object + " is not properly structured.");
	}

	@Deprecated
	public String getRawName() {
		return rawName;
	}
}