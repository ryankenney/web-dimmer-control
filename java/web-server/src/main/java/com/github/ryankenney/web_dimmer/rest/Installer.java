package com.github.ryankenney.web_dimmer.rest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermissions;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.github.ryankenney.web_dimmer.util.ProcessLauncher;
import com.github.ryankenney.web_dimmer.util.ProcessLauncher.Result;

public class Installer {

	public void installService(File sourceJar) {
		File sourceJarFile = sourceJar;
		File installedDir = new File("/opt/web-dimmer-control");
		File installedJarFile = new File(installedDir, sourceJarFile.getName());
		File serviceFile = new File("/etc/systemd/system/web-dimmer-control.service");
		String serviceUser ="web_dimmer";

		setupDirectory(installedDir);
		setupJar(sourceJarFile, installedJarFile);
		setupServiceUser(serviceUser);
		setupServiceFile(serviceFile, serviceUser, installedJarFile);
	}

	private void setupDirectory(File installedDir) {
		if (!installedDir.isDirectory()) {
			if (!installedDir.mkdirs()) {
				throw new RuntimeException("Failed to create directory ["+installedDir+"]");
			}
		}
		setOwnerToRoot(installedDir);
		setPermissions(installedDir, "rwxr-xr-x");
		System.out.println("Setup install directory ["+installedDir+"]");
	}

	private void setupJar(File sourceJarFile, File installedJarFile) {
		try {
			FileUtils.copyFile(sourceJarFile, installedJarFile);
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy ["+sourceJarFile+"] to ["+installedJarFile+"]", e);
		}
		setOwnerToRoot(installedJarFile);
		setPermissions(installedJarFile, "rw-r--r--");
		System.out.println("Installed jar ["+installedJarFile+"]");
	}

	private void setupServiceUser(String serviceUser) {
		Result result = new ProcessLauncher().setThrowExceptionOnExit(false).runToCompletion(new ProcessBuilder("id", "-u", serviceUser));
		if (result.getExitCode() == 0) {
			System.out.println("Skipped creation of user ["+serviceUser+"] (already exists)");
			return;
		}
		if (result.getExitCode() != 1) {
			throw new RuntimeException("Unknown exit code from 'id -u': "+result.getExitCode());
		}
		new ProcessLauncher().runToCompletion(new ProcessBuilder("useradd", serviceUser));
		System.out.println("Created user ["+serviceUser+"]");
	}

	private void setupServiceFile(File serviceFile, String serviceUser, File installedJarFile) {
		try (InputStream in = Installer.class.getResourceAsStream("/"+serviceFile.getName());
				OutputStream out = new FileOutputStream(serviceFile))
		{
			String contents = IOUtils.toString(in, StandardCharsets.UTF_8);
			contents = contents.replace("<<JAR_PATH>>", installedJarFile.getAbsolutePath());
			contents = contents.replace("<<SERVICE_USER>>", serviceUser);
			System.out.println(contents);
			FileUtils.writeStringToFile(serviceFile, contents, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Failed to write to ["+serviceFile+"]", e);
		}
		setOwnerToRoot(serviceFile);
		setPermissions(serviceFile, "rwxr--r--");
		System.out.println("Wrote service definition ["+serviceFile+"]");

		new ProcessLauncher().runToCompletion(new ProcessBuilder("systemctl", "daemon-reload"));
		System.out.println("Refreshed service definitions");
	}

	private static void setOwnerToRoot(File file) {
		UserPrincipalLookupService lookupService = FileSystems.getDefault().getUserPrincipalLookupService();
		try {
			UserPrincipal user = lookupService.lookupPrincipalByName("root");
			GroupPrincipal group = lookupService.lookupPrincipalByGroupName("root");
			PosixFileAttributeView fileAttributes = Files.getFileAttributeView(file.toPath(), PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS);
			fileAttributes.setOwner(user);
			fileAttributes.setGroup(group);
		} catch (IOException e) {
			throw new RuntimeException("Failed to set ownership on ["+file+"]");
		}
	}

	private static void setPermissions(File file, String permissions) {
		try {
			Files.setPosixFilePermissions(file.toPath(), PosixFilePermissions.fromString(permissions));
		} catch (IOException e) {
			throw new RuntimeException("Failed to set permissions on ["+file+"]");
		}
	}
}
