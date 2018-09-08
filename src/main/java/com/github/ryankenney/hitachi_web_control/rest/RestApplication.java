package com.github.ryankenney.hitachi_web_control.rest;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

public class RestApplication extends Application {
	
	@Override
	public Set<Class<?>> getClasses() {
		Set<Class<?>> resources = new HashSet<Class<?>>();

		// register REST modules
		resources.add(StatusService.class);
		resources.add(ControlService.class);
		resources.add(GlobalExceptionMapper.class);

		// Manually adding MOXyJSONFeature
		resources.add(org.glassfish.jersey.moxy.json.MoxyJsonFeature.class);

		return resources;
	}

}
