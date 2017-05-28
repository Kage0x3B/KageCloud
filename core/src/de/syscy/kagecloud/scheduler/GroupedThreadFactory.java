package de.syscy.kagecloud.scheduler;

import java.util.concurrent.ThreadFactory;

import de.syscy.kagecloud.plugin.Plugin;
import lombok.Data;

@Data
@Deprecated
public class GroupedThreadFactory implements ThreadFactory {

	private final ThreadGroup group;

	public static class BungeeGroup extends ThreadGroup {

		private BungeeGroup(String name) {
			super(name);
		}

	}

	public GroupedThreadFactory(Plugin plugin, String name) {
		group = new BungeeGroup(name);
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(group, r);
	}
}
