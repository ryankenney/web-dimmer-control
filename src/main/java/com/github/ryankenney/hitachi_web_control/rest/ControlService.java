package com.github.ryankenney.hitachi_web_control.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.github.ryankenney.hitachi_web_control.rest.GenericResult.RESULT_STATUS;


@Path("control")
public class ControlService {

	@GET
	@Path("speed/{speed}")
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResult setSpeed(@QueryParam("speed") double speed) {

		// TODO [rkenney]: Implement

		return new GenericResult(RESULT_STATUS.OK);
	}
}
