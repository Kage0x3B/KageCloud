package de.syscy.kagecloud.util;

import java.io.IOException;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudBungee;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.scheduler.ScheduledTask;

@RequiredArgsConstructor
public class CloudCoreConnectRunnable implements Runnable {
	private final KageCloudBungee bungee;
	private @Setter ScheduledTask task;

	@Override
	@SuppressWarnings("deprecation")
	public void run() {
		try {
			bungee.getClient().connect(5000, bungee.getConfig().getString("coreIP", "localhost"), bungee.getConfig().getInt("port"));

			bungee.getProxy().getServers().clear();

			KageCloud.logger.info("Connected to " + bungee.getConfig().getString("coreIP", "localhost"));

			task.cancel();
		} catch(IOException ex) {
			KageCloud.logger.finer("Could not connect to the KageCloud core server, trying again... (" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ")");
		}
	}
}