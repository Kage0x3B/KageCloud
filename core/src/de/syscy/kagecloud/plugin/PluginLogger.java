package de.syscy.kagecloud.plugin;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import de.syscy.kagecloud.KageCloud;

public class PluginLogger extends Logger {
	private String pluginName;

	public PluginLogger(Plugin context) {
		super(context.getClass().getCanonicalName(), null);
		String prefix = context.getDescription().getPrefix();
		pluginName = prefix != null ? "[" + prefix + "] " : "[" + context.getDescription().getName() + "] ";
		setParent(KageCloud.logger);
		setLevel(Level.ALL);
	}

	@Override
	public void log(LogRecord logRecord) {
		logRecord.setMessage(String.valueOf(pluginName) + logRecord.getMessage());
		super.log(logRecord);
	}
}
