package com.github.ryankenney.hitachi_web_control.hardware;

import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class manages a {@link HardwareController}, making sure to set the
 * controller to "0" if no updates have been seen for 5 seconds. This ensures
 * that disconnectivity disables the device.
 */
public class HardwareManager {

	private static final long TIMEOUT_SECS = 10;
	private static final Logger LOGGER = Logger.getLogger(HardwareManager.class.getName());

	private final HardwareController controller = new HardwareController();
	private ScheduledExecutorService scheduler;
	private volatile Date lastUpdate = new Date(0);
	private volatile boolean isTimedOut = false;
	private final Object hardwareMutex = new Object();

	public void start() {
		if (scheduler != null) {
			return;
		}
		scheduler =  Executors.newScheduledThreadPool(1);
		scheduler.scheduleWithFixedDelay(() -> disableIfUpdateTimedOut(), TIMEOUT_SECS, TIMEOUT_SECS, TimeUnit.SECONDS);
	}

	public void stop() {
		try {
			scheduler.shutdown();
			try {
				scheduler.awaitTermination(20, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				LOGGER.log(Level.WARNING, "Failed to terminate scheduler", e);
			}
		} finally {
			scheduler = null;
		}
	}

	public void setSpeed(int percent) {
		synchronized (hardwareMutex) {
			if (/* newly timed out */ isTimedOut) {
				LOGGER.log(Level.INFO, "Updates resumed after timeout. Enabling.");
				isTimedOut = false;
			}
			this.controller.setSpeed(percent);
			lastUpdate = new Date();
		}
	}

	private void disableIfUpdateTimedOut() {
		synchronized (hardwareMutex) {
			if (System.currentTimeMillis() - 5000 < lastUpdate.getTime()) {
				return;
			}
			if (/* newly timed out */ !isTimedOut) {
				LOGGER.log(Level.INFO, "Updates timed out. Disabling.");
				isTimedOut = true;
			}
			this.controller.setSpeed(0);
		}
	}
}
