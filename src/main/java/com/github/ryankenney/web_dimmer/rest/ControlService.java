package com.github.ryankenney.web_dimmer.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.ryankenney.web_dimmer.rest.GenericResult.RESULT_STATUS;


@Path("control")
public class ControlService {

	@GET
	@Path("speed/{speed}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResult setSpeed(@PathParam("speed") int speed) {
		HardwareManagerSingleton.INSTANCE.setSpeed(speed);
		return new GenericResult(RESULT_STATUS.OK);
	}
}
