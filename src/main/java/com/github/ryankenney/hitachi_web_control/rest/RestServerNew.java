package com.github.ryankenney.hitachi_web_control.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServerNew {
	public static void main(String[] args) throws Exception {
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/");

		Server jettyServer = new Server(8080);
		jettyServer.setHandler(context);

		ServletHolder jerseyServlet = context.addServlet(com.sun.jersey.spi.container.servlet.ServletContainer.class, "/rest/*");
		jerseyServlet.setInitOrder(0);

		jerseyServlet.setInitParameter("com.sun.jersey.config.property.packages",
				GlobalExceptionMapper.class.getPackage().getName());

//		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
//				GlobalExceptionMapper.class.getCanonicalName()+";"+
//				ControlService.class.getCanonicalName()+";"+
//				StatusService.class.getCanonicalName());

		jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true"); 

		try {
			jettyServer.start();
			jettyServer.join();
		} finally {
			jettyServer.destroy();
		   }
    }
}
