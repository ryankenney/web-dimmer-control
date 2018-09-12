package com.github.ryankenney.web_dimmer.util;

@SuppressWarnings("serial")
public class ProcessLauncherException extends RuntimeException {

	private int exitCode;
	private byte[] stdout;
	private byte[] stderr;

	public ProcessLauncherException(int exitCode, byte[] stdout, byte[] stderr) {
		super("Process terminated with [" + exitCode + "]");
		this.exitCode = exitCode;
		this.stdout = stdout;
		this.stderr = stderr;
	}

	public int getExitCode() {
		return exitCode;
	}

	public byte[] getStdout() {
		return stdout;
	}

	public byte[] getStderr() {
		return stderr;
	}
}