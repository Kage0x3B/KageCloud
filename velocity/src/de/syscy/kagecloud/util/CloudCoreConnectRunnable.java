package de.syscy.kagecloud.util;

import com.velocitypowered.api.scheduler.ScheduledTask;
import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.KageCloudVelocity;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;

@RequiredArgsConstructor
public class CloudCoreConnectRunnable implements Runnable {
	private final KageCloudVelocity plugin;
	private @Setter ScheduledTask task;

	@Override
	public void run() {
		try {
			plugin.getClient().connect(5000, plugin.getConfig().getString("coreIP", "localhost"), plugin.getConfig().getInt("port"));

			plugin.getProxy().getAllServers().stream().filter(s -> !s.getServerInfo().getName().equalsIgnoreCase("dummy")).forEach(s -> plugin.getProxy().unregisterServer(s.getServerInfo()));

			KageCloud.logger.info("Connected to " + plugin.getConfig().getString("coreIP", "localhost"));

			task.cancel();
		} catch(IOException ex) {
			KageCloud.logger.info("Could not connect to the KageCloud core server, trying again... (" + ex.getClass().getSimpleName() + ": " + ex.getMessage() + ")");
		}
	}
}