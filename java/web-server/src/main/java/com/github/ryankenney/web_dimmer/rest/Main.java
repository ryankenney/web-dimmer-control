package com.github.ryankenney.web_dimmer.rest;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;

public class Main {
	public static void main(String[] args) throws Exception {
		LinkedList<String> argList = new LinkedList<>(Arrays.asList(args));
		if (argList.size() < 1) {
			printUsageAndExit();
		}
		switch (argList.poll()) {
		case "web-server":
			new RestServer().run();
			break;
		case "install-service":
			new Installer().installService(getJar());
			break;
		case "uninstall-service":
			break;
		default:
			printUsageAndExit();
		}
	}

	private static void printUsageAndExit() {
		System.out.println("");
		System.out.println("Usage: java -jar '"+getJarPath()+"' <web-server|install-service>");
		System.out.println("");
		System.out.println("    web-server:        Launches the web server in the foreground.");
		System.out.println("    install-service:   Installs/enables the service to systemd.");
		System.out.println("    uninstall-service: Uninstalls the service from systemd.");
		System.out.println("");
		System.exit(1);
	}

	private static File getJar() {
		try {
			return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	private static String getJarPath() {
		try {
			return getJar().getCanonicalPath();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
