package com.github.ryankenney.web_dimmer.util;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.output.CloseShieldOutputStream;
import org.apache.commons.io.output.TeeOutputStream;

import jacle.common.lang.JavaUtil;

/**
 * <p>
 * Used to automatically copy the {@link OutputStream}s of a {@link Process} to
 * byte buffers, using background threads.
 * </p>
 */
public class ProcessStreamBuffers implements Closeable {

	private ExecutorService threadPool;
	private InputStream processInputStream;
	private InputStream processErrorStream;
	private ByteArrayOutputStream stdoutBuffer = new ByteArrayOutputStream();
	private ByteArrayOutputStream stderrBuffer = new ByteArrayOutputStream();
	private InputStream stdinStream;
	private boolean doEchoOutput;

	public ProcessStreamBuffers() {
	}

	/**
	 * If set to <code>true</code>, the process output will be copied to
	 * {@link System#out} and {@link System#err}, in addition to the internal
	 * buffers. Defaults to <code>false</code>.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessStreamBuffers setEchoOutput(boolean doEcho) {
		doEchoOutput = doEcho;
		return this;
	}

	/**
	 * If set, the provided stream is copied to the stdin stream of the process.
	 * Defaults to null.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessStreamBuffers setStdin(InputStream stdin) {
		stdinStream = stdin;
		return this;
	}

	/**
	 * Launches background threads that copy the stdout/stderr from the provided
	 * process to internal buffers. The buffers are available from
	 * {@link #getStdout()} and {@link #getStderr()} after {@link #waitFor()}.
	 * 
	 * @return this object (fluent setter)
	 */
	public ProcessStreamBuffers bind(Process process) {
		this.threadPool = Executors.newCachedThreadPool(new NamedThreadFactory(JavaUtil.I.getSimpleClassName()));
		this.processInputStream = process.getInputStream();
		this.processErrorStream = process.getErrorStream();

		OutputStream so = doEchoOutput ? teeToSystemOut(stdoutBuffer) : stdoutBuffer;
		OutputStream se = doEchoOutput ? teeToSystemErr(stderrBuffer) : stderrBuffer;

		threadPool.execute(new StreamCopierTask(process.getInputStream(), so));
		threadPool.execute(new StreamCopierTask(process.getErrorStream(), se));
		if (stdinStream != null) {
			threadPool.execute(new StreamCopierTask(stdinStream, process.getOutputStream()));
		}
		// Stop any future tasks and terminate when current tasks complete
		threadPool.shutdown();
		return this;
	}

	private static OutputStream teeToSystemOut(OutputStream stream) {
		return new TeeOutputStream(stream, new CloseShieldOutputStream(System.out));
	}

	private static OutputStream teeToSystemErr(OutputStream stream) {
		return new TeeOutputStream(stream, new CloseShieldOutputStream(System.err));
	}

	/**
	 * <p>
	 * Interrupts the stream readers/writers by closing the {@link InputStream} s,
	 * and then waits for the background threads to terminate. A side effect of this
	 * method is that the streams owned by the {@link Process} provided to
	 * {@link #bind(Process)} will be closed.
	 * </p>
	 * 
	 * <p>
	 * After calling this method, you should still call {@link #waitFor()}, which
	 * will ensure that the background threads terminate and the internal resources
	 * are released.
	 * </p>
	 * 
	 */
	public void interrupt() {
		CloseablesExt.close(processInputStream);
		CloseablesExt.close(processErrorStream);
	}

	/**
	 * Waits for all stream copying to complete (along with the related background
	 * threads). Calls {@link #close()} internally, so there's no need to call that
	 * explicitly.
	 */
	public void waitFor() throws InterruptedException {
		try {
			threadPool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} finally {
			// Close this even in case of InterruptedException
			this.close();
		}
	}

	/**
	 * Returns the content collected from {@link Process#getInputStream()}.
	 */
	public byte[] getStdout() {
		return stdoutBuffer.toByteArray();
	}

	/**
	 * Returns the content collected from {@link Process#getErrorStream()}.
	 */
	public byte[] getStderr() {
		return stderrBuffer.toByteArray();
	}

	/**
	 * Releases internal resources (stream handles) owned by this object. It is
	 * important to call this method in order to avoid leaked resources. You do not
	 * need to call this method explicitly if you call {@link #waitFor()}.
	 */
	@Override
	public void close() {
		CloseablesExt.close(stdoutBuffer);
		CloseablesExt.close(stderrBuffer);
	}
}
