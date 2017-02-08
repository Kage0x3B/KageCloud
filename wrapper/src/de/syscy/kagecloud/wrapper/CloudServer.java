package de.syscy.kagecloud.wrapper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class CloudServer {
	private static final int SERVER_SHUTDOWN_TIMEOUT = 120 * 1000;

	private final KageCloudWrapper wrapper;
	private final @Getter ServerTemplate template;

	private final @Getter UUID serverId;
	private final @Getter String serverName;
	private final @Getter String templateName;

	private @Getter Process process;

	private @Getter File serverFolder;

	public CloudServer(KageCloudWrapper wrapper, ServerTemplate template) {
		this.wrapper = wrapper;
		this.template = template;

		serverId = template.getServerId();
		serverName = template.getServerName();
		templateName = template.getTemplateName();
	}

	public void prepareServerFolder() {
		serverFolder = new File(wrapper.getServersDirectory(), serverId.toString());
		serverFolder.mkdirs();

		File pluginsFolder = new File(serverFolder, "plugins");
		pluginsFolder.mkdirs();

		List<String> pluginNames = template.getPlugins();

		for(File pluginFile : wrapper.getGlobalPluginDirectory().listFiles(new JARFileFilter())) {
			String pluginFileName = pluginFile.getName().substring(0, pluginFile.getName().length() - 4); //Removes the .jar (the 4 last characters)

			for(String pluginName : pluginNames) {
				if(pluginFileName.equalsIgnoreCase(pluginName)) {
					try {
						FileUtils.copyFileToDirectory(pluginFile, pluginsFolder);
					} catch(IOException ex) {
						ex.printStackTrace();
					}

					break;
				}
			}
		}

		try {
			FileUtils.copyDirectory(template.getTemplateDirectory(), serverFolder);

			FileUtils.copyFile(template.getServerJAR(), new File(serverFolder, "server.jar"));
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				List<String> commands = buildCommands(template);

				ProcessBuilder processBuilder = new ProcessBuilder(commands);

				processBuilder.environment().put("serverId", serverId.toString());
				processBuilder.environment().put("serverName", serverName);
				processBuilder.environment().put("templateName", templateName);
				processBuilder.environment().put("wrapperName", wrapper.getNodeName());
				processBuilder.environment().put("isLobbyServer", Boolean.toString(template.isLobby()));

				processBuilder.directory(serverFolder);

				//DEBUG Code
				processBuilder.redirectErrorStream(true);
				processBuilder.redirectOutput(Redirect.INHERIT);
				processBuilder.redirectError(Redirect.INHERIT);

				try {
					process = processBuilder.start();
					KageCloud.logger.info("Started server!");
					process.waitFor();

					KageCloud.logger.info("Server " + serverName + " went offline");
				} catch(IOException ex) {
					System.out.println("Error while starting the server (template: " + templateName + ")");

					ex.printStackTrace();
				} catch(InterruptedException ex) {
					ex.printStackTrace();
				} finally {
					cleanUp();
				}
			}
		}).start();
	}

	public void shutdown() {
		if(process != null) {
			try {
				process.getOutputStream().write("stop\n".getBytes());
			} catch(IOException ex) {
				ex.printStackTrace();
			}

			int exitValue = shutdownProcess(SERVER_SHUTDOWN_TIMEOUT);

			KageCloud.logger.info("Server " + serverName + " was shutdown (code: " + exitValue + ")");

			process = null;
		}

		cleanUp();
	}

	public void cleanUp() {
		if(process != null) {
			process.destroy();
		}

		try {
			FileUtils.deleteDirectory(serverFolder);
		} catch(IOException ex) {
			ex.printStackTrace();
		}

		wrapper.removeServer(this);
	}

	private int shutdownProcess(int timeout) {
		Worker worker = new Worker(process);
		worker.start();

		try {
			worker.join(timeout);

			if(worker.getExitValue() != null) {
				return worker.getExitValue();
			} else {
				return -1;
			}
		} catch(InterruptedException ex) {
			worker.interrupt();
			Thread.currentThread().interrupt();
		} finally {
			process.destroy();
		}

		return process.exitValue();
	}

	private List<String> buildCommands(ServerTemplate template) {
		List<String> commands = new ArrayList<>();
		int port = wrapper.getNextPort();

		commands.add(System.getProperty("java.home") + "/bin/java");
		commands.add("-Xmx" + template.getMemory() + "M");
		commands.add("-Xms" + template.getMemory() + "M");
		commands.add("-jar");
		commands.add(template.getServerJAR().getAbsolutePath());

		commands.add("--port");
		commands.add(Integer.toString(port));
		commands.add("--max-players");
		commands.add(Integer.toString(template.getSlots()));
		commands.add("--o");
		commands.add("false");
		commands.add("--nojline");

		return commands;
	}

	private static class JARFileFilter implements FileFilter {
		@Override
		public boolean accept(File pathname) {
			return pathname.getAbsolutePath().toLowerCase().endsWith(".jar");
		}
	}

	@RequiredArgsConstructor
	private static class Worker extends Thread {
		private final Process process;
		private @Getter Integer exitValue;

		@Override
		public void run() {
			try {
				exitValue = process.waitFor();
			} catch(InterruptedException ex) {

			}
		}
	}
}