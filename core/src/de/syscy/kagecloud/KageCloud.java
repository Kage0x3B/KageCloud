package de.syscy.kagecloud;

import java.io.File;
import java.util.logging.Logger;

import de.syscy.kagecloud.scheduler.TaskScheduler;

public class KageCloud {
	public static ICloudNode cloudNode;

	public static Logger logger;

	public static File dataFolder;

	public static TaskScheduler scheduler;
}