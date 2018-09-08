package com.github.ryankenney.hitachi_web_control.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;

public class HardwareController {

	private static final Logger LOGGER = Logger.getLogger(HardwareController.class.getName());

	public void setSpeed(int percent) {
		if (percent < 0 || percent > 100) {
			throw new IllegalStateException("Invalid precentage provided: "+percent);
		}
		LOGGER.log(Level.FINE, "Setting speed: "+percent);

		// TODO [rkenney]: Implement
	}
}
