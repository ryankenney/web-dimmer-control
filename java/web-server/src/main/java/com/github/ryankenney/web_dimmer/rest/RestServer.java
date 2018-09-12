package com.github.ryankenney.web_dimmer.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServer {

	public void run() {

		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		restHandler.setContextPath("/");
		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter("javax.ws.rs.Application",
				RestApplication.class.getName());
		jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true"); 

		Server jettyServer = new Server(8080);
		jettyServer.setHandler(restHandler);

		HardwareManagerSingleton.INSTANCE.start();

		try {
			jettyServer.start();
			jettyServer.join();
			HardwareManagerSingleton.INSTANCE.stop();
		}
		// This broad catch comes form the Jetty methods
		catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			jettyServer.destroy();
		}
	}
}
