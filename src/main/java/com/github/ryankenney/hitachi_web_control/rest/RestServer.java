package com.github.ryankenney.hitachi_web_control.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServer {
	public static void main(String[] args) throws Exception {

		ResourceHandler staticContentHandler = new ResourceHandler();
		staticContentHandler.setDirectoriesListed(true);
		staticContentHandler.setWelcomeFiles(new String[]{ "index.html" });
		staticContentHandler.setResourceBase("src/main/webapp/");

		ServletContextHandler restHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
		restHandler.setContextPath("/rest/");
		ServletHolder jerseyServlet = restHandler.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		jerseyServlet.setInitParameter("javax.ws.rs.Application",
				RestApplication.class.getName());
		jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true"); 

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new org.eclipse.jetty.server.Handler[] {staticContentHandler, restHandler});
		Server jettyServer = new Server(8080);
		jettyServer.setHandler(handlers);

		HardwareManagerSingleton.INSTANCE.start();

		try {
			jettyServer.start();
			jettyServer.join();
			HardwareManagerSingleton.INSTANCE.stop();
		} finally {
			jettyServer.destroy();
		}
	}
}
