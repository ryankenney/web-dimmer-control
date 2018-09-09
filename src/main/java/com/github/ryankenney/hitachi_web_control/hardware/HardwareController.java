package com.github.ryankenney.hitachi_web_control.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HardwareController {

	private static final Logger LOGGER = Logger.getLogger(HardwareController.class.getName());

	int lastSpeedSetPercent = -1;

	public void setSpeed(int percent) {
		if (percent < 0 || percent > 100) {
			throw new IllegalStateException("Invalid precentage provided: "+percent);
		}
		if (percent != lastSpeedSetPercent) {
			LOGGER.log(Level.INFO, "Setting speed: "+percent);
		} else {
			LOGGER.log(Level.FINE, "Setting speed: "+percent);
		}
		lastSpeedSetPercent = percent;

		// TODO [rkenney]: Implement
	}
}
