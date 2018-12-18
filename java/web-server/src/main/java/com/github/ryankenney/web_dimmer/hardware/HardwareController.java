package com.github.ryankenney.web_dimmer.hardware;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.ryankenney.web_dimmer.util.ProcessLauncher;

public class HardwareController {

	private static final Logger LOGGER = Logger.getLogger(HardwareController.class.getName());
	private static final int PWM_PIN = 18;
	private static final int MAX_PWM_VALUE = 1024;

	int lastSpeedSetPercent = -1;

	public void setSpeed(int percent) {
		if (percent < 0 || percent > 100) {
			throw new IllegalStateException("Invalid precentage provided: "+percent);
		}
		if (percent != lastSpeedSetPercent) {
			LOGGER.log(Level.INFO, "Setting speed: "+percent);
			int pwmValue = (percent * MAX_PWM_VALUE) / 100; 
			new ProcessLauncher().runToCompletion(new ProcessBuilder("gpio", "-g", "pwm", ""+PWM_PIN, ""+pwmValue));
		} else {
			LOGGER.log(Level.FINE, "Setting speed: "+percent);
		}
		lastSpeedSetPercent = percent;
	}
}
