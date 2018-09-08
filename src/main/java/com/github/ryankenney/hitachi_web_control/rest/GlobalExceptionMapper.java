package com.github.ryankenney.hitachi_web_control.rest;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.github.ryankenney.hitachi_web_control.rest.GenericResult.RESULT_STATUS;

// TODO [rkenney]: Remove if not properly registered with jetty
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

	static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());
	final ThreadLocalRandom RANDOM  = ThreadLocalRandom.current();

	public Response toResponse(Throwable e) {
		// We generate a random string that we echo to the client so they can find the actual error in the logs
		// (We don't want to leak stack traces out to the web.)
		String errorRefernce = ""+RANDOM.nextLong()+"-"+RANDOM.nextLong()+"-"+RANDOM.nextLong();
		LOGGER.log(Level.WARNING, "Request Failed (errorReference: "+errorRefernce+")", e);
		return Response.status(500).entity(
				new GenericResult(RESULT_STATUS.ERROR, errorRefernce)).type("text/plain").build();
	}
}
