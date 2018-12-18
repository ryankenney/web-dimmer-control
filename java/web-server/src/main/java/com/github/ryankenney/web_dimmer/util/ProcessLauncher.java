package com.github.ryankenney.web_dimmer.util;

import java.io.ByteArrayInputStream;

public class ProcessLauncher {

	public static class Result {
		private int exitCode;
		private byte[] stdout;
		private byte[] stderr;

		Result(int exitCode, byte[] stdout, byte[] stderr) {
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

	private boolean doEchoOutput;
	private boolean doThrowExceptionOnExit = true;
	private byte[] stdinBytes;

	public ProcessLauncher() {
	}

	/**
	 * If set to <code>true</code>, the process output will be copied to
	 * {@link System#out} and {@link System#err}, in addition to the internal
	 * buffers made available as {@link Result} or {@link ProcessLauncherException}.
	 * Defaults to <code>false</code>.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessLauncher setEchoOutput(boolean doEcho) {
		this.doEchoOutput = doEcho;
		return this;
	}

	/**
	 * If set to <code>false</code>, the process will simply return a {@link Result}
	 * with the exit code rather than throwing a {@link ProcessLauncherException}
	 * when the exit code is non-zero. Defaults to <code>true</code>.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessLauncher setThrowExceptionOnExit(boolean doThrow) {
		this.doThrowExceptionOnExit = doThrow;
		return this;
	}

	/**
	 * If set, the provided bytes are provided to the child process via stdin.
	 * Defaults to null.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessLauncher setStdin(byte[] stdin) {
		this.stdinBytes = stdin;
		return this;
	}

	/**
	 * Launches a process from the already-populated {@link ProcessBuilder} and
	 * blocks until that process terminates.
	 * 
	 * @param processBuilder
	 *            The process definition to execute
	 * 
	 * @throws ProcessLauncherException
	 *             Thrown if the process terminates with a non-zero exit code.
	 * @throws RuntimeInterruptedException
	 *             Thrown if {@link InterruptedException} thrown by any underlying
	 *             methods. Treat this identically to {@link InterruptedException}.
	 * @throws RuntimeIOException
	 *             Thrown if the action fails for any other, non-{@link Error}
	 *             reason.
	 */
	public Result runToCompletion(ProcessBuilder processBuilder)
			throws RuntimeInterruptedException, RuntimeIOException, ProcessLauncherException {
		ProcessStreamBuffers buffers;
		int exitCode;
		ByteArrayInputStream stdinStream = null;

		Process process = null;
		try {
			process = processBuilder.start();

			// Launch stream processing threads
			buffers = new ProcessStreamBuffers();
			buffers.setEchoOutput(this.doEchoOutput);
			if (stdinBytes != null) {
				stdinStream = new ByteArrayInputStream(stdinBytes);
				buffers.setStdin(stdinStream);
			}
			buffers.bind(process);

			try {
				exitCode = process.waitFor();
				buffers.waitFor();
			} catch (InterruptedException e) {
				buffers.interrupt();
				buffers.close();
				throw new RuntimeInterruptedException(e);
			} finally {
				// Just in case waitFor() wasn't executed
				buffers.close();
			}
			if (exitCode != 0 && doThrowExceptionOnExit) {
				throw new ProcessLauncherException(exitCode, buffers.getStdout(), buffers.getStderr());
			}
			return new Result(exitCode, buffers.getStdout(), buffers.getStderr());
		} catch (RuntimeInterruptedException e) {
			throw e;
		} catch (ProcessLauncherException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeIOException("Failed to launch process", e);
		} finally {
			CloseablesExt.close(stdinStream);
		}
	}
}