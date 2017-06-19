package de.syscy.kagecloud.scheduler;

import java.util.concurrent.ThreadFactory;

import de.syscy.kagecloud.plugin.Plugin;
import lombok.Data;

@Data
@Deprecated
public class GroupedThreadFactory implements ThreadFactory {
	private final ThreadGroup group;

	public static class CloudGroup extends ThreadGroup {
		private CloudGroup(String name) {
			super(name);
		}
	}

	public GroupedThreadFactory(Plugin plugin, String name) {
		group = new CloudGroup(name);
	}

	@Override
	public Thread newThread(Runnable r) {
		return new Thread(group, r);
	}
}
