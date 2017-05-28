package de.syscy.kagecloud.scheduler;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import de.syscy.kagecloud.plugin.Plugin;

public class CloudScheduler implements TaskScheduler {
	private final Object lock = new Object();
	private final AtomicInteger taskCounter = new AtomicInteger();
	private final Map<Integer, CloudTask> tasks = Collections.synchronizedMap(new HashMap<Integer, CloudTask>());
	private final Multimap<Plugin, CloudTask> tasksByPlugin = Multimaps.synchronizedMultimap(HashMultimap.<Plugin, CloudTask> create());

	private final Unsafe unsafe = new Unsafe() {
		@Override
		@SuppressWarnings("deprecation")
		public ExecutorService getExecutorService(Plugin plugin) {
			return plugin.getExecutorService();
		}
	};

	@Override
	public void cancel(int id) {
		CloudTask task = tasks.get(id);
		Preconditions.checkArgument(task != null, "No task with id %s", id);

		task.cancel();
	}

	void cancel0(CloudTask task) {
		synchronized(lock) {
			tasks.remove(task.getId());
			tasksByPlugin.values().remove(task);
		}
	}

	@Override
	public void cancel(ScheduledTask task) {
		task.cancel();
	}

	@Override
	public int cancel(Plugin plugin) {
		Set<ScheduledTask> toRemove = new HashSet<>();
		for(ScheduledTask task : tasksByPlugin.get(plugin)) {
			toRemove.add(task);
		}
		for(ScheduledTask task : toRemove) {
			cancel(task);
		}
		return toRemove.size();
	}

	@Override
	public ScheduledTask runAsync(Plugin owner, Runnable task) {
		return schedule(owner, task, 0, TimeUnit.MILLISECONDS);
	}

	@Override
	public ScheduledTask schedule(Plugin owner, Runnable task, long delay, TimeUnit unit) {
		return schedule(owner, task, delay, 0, unit);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ScheduledTask schedule(Plugin owner, Runnable task, long delay, long period, TimeUnit unit) {
		Preconditions.checkNotNull(owner, "owner");
		Preconditions.checkNotNull(task, "task");
		CloudTask prepared = new CloudTask(this, taskCounter.getAndIncrement(), owner, task, delay, period, unit);

		synchronized(lock) {
			tasks.put(prepared.getId(), prepared);
			tasksByPlugin.put(owner, prepared);
		}

		owner.getExecutorService().execute(prepared);
		return prepared;
	}

	@Override
	public Unsafe unsafe() {
		return unsafe;
	}
}
