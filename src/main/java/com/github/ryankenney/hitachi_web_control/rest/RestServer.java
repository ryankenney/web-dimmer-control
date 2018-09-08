package com.github.ryankenney.hitachi_web_control.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class RestServer {
//	public static void main(String[] args) throws Exception {
//		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		context.setContextPath("/rest/");
//
//		Server jettyServer = new Server(8080);
//		jettyServer.setHandler(context);
//
//		ServletHolder jerseyServlet = context.addServlet(org.glassfish.jersey.servlet.ServletContainer.class, "/*");
//		jerseyServlet.setInitOrder(0);
//
//		jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",
//				GlobalExceptionMapper.class.getCanonicalName()+";"+
//				ControlService.class.getCanonicalName()+";"+
//				StatusService.class.getCanonicalName());
//
//		jerseyServlet.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true"); 
//
//		try {
//			jettyServer.start();
//			jettyServer.join();
//		} finally {
//			jettyServer.destroy();
//		   }
//    }
}
