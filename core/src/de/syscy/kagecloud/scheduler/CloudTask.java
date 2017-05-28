package de.syscy.kagecloud.scheduler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

import de.syscy.kagecloud.KageCloud;
import de.syscy.kagecloud.plugin.Plugin;
import lombok.Data;

@Data
public class CloudTask implements Runnable, ScheduledTask {
	private final CloudScheduler sched;
	private final int id;
	private final Plugin owner;
	private final Runnable task;

	private final long delay;
	private final long period;
	private final AtomicBoolean running = new AtomicBoolean(true);

	public CloudTask(CloudScheduler sched, int id, Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
		this.sched = sched;
		this.id = id;
		this.owner = owner;
		this.task = task;
		this.delay = unit.toMillis(delay);
		this.period = unit.toMillis(period);
	}

	@Override
	public void cancel() {
		boolean wasRunning = running.getAndSet(false);

		if(wasRunning) {
			sched.cancel0(this);
		}
	}

	@Override
	public void run() {
		if(delay > 0) {
			try {
				Thread.sleep(delay);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		while(running.get()) {
			try {
				task.run();
			} catch(Throwable t) {
				KageCloud.logger.log(Level.SEVERE, String.format("Task %s encountered an exception", this), t);
			}

			// If we have a period of 0 or less, only run once
			if(period <= 0) {
				break;
			}

			try {
				Thread.sleep(period);
			} catch(InterruptedException ex) {
				Thread.currentThread().interrupt();
			}
		}

		cancel();
	}
}
