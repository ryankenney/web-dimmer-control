package com.github.ryankenney.web_dimmer.rest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;


@Path("/html")
public class HtmlService {

	@GET
	@Path("{file}")
	@Produces(MediaType.TEXT_HTML)
	public String setSpeed(@PathParam("file") String file) {
		try (InputStream in = HtmlService.class.getResourceAsStream("/static-html/"+file)) {
			String content = IOUtils.toString(in, StandardCharsets.UTF_8);
			return content;
		} catch (IOException e) {
			throw new RuntimeException("Failed to load resource [/static-html/"+file+"]", e);
		}
	}
}
