package com.github.ryankenney.web_dimmer.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.github.ryankenney.web_dimmer.rest.GenericResult.RESULT_STATUS;


@Path("status")
public class StatusService {

	/**
	 * Simply used to verify that the system is online.
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public GenericResult setStatus() {
		return new GenericResult(RESULT_STATUS.OK);
	}
}
