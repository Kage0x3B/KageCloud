package de.syscy.kagecloud.configuration.file;

import java.util.LinkedHashMap;
import java.util.Map;

import de.syscy.kagecloud.configuration.ConfigurationSection;
import de.syscy.kagecloud.configuration.serialization.ConfigurationSerializable;
import de.syscy.kagecloud.configuration.serialization.ConfigurationSerialization;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

public class YamlRepresenter extends Representer {

	public YamlRepresenter() {
		multiRepresenters.put(ConfigurationSection.class, new RepresentConfigurationSection());
		multiRepresenters.put(ConfigurationSerializable.class, new RepresentConfigurationSerializable());
	}

	private class RepresentConfigurationSection extends RepresentMap {
		@Override
		public Node representData(Object data) {
			return super.representData(((ConfigurationSection) data).getValues(false));
		}
	}

	private class RepresentConfigurationSerializable extends RepresentMap {
		@Override
		public Node representData(Object data) {
			ConfigurationSerializable serializable = (ConfigurationSerializable) data;
			Map<String, Object> values = new LinkedHashMap<>();
			values.put(ConfigurationSerialization.SERIALIZED_TYPE_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
			values.putAll(serializable.serialize());

			return super.representData(values);
		}
	}
}
