package com.github.ryankenney.web_dimmer.rest;

import com.github.ryankenney.web_dimmer.hardware.HardwareManager;

public class HardwareManagerSingleton {

	public static final HardwareManager INSTANCE = new HardwareManager();
}
