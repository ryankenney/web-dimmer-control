package com.github.ryankenney.web_dimmer.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Pumps an input stream to an output stream until nothing is available. Based
 * loosely upon the Apache Common Exec class, StreamPumper.
 */
public class StreamCopierTask implements Runnable {

	private static int BUFFER_SIZE = 1000;

	private final AtomicReference<InputStream> inputStream = new AtomicReference<>();
	private final AtomicReference<OutputStream> outputStream = new AtomicReference<>();

	/**
	 * Constructor. The provided streams will be closed if {@link #run()} is allowed
	 * to run to completion.
	 */
	public StreamCopierTask(InputStream input, OutputStream output) {
		this.inputStream.set(input);
		this.outputStream.set(output);
	}

	/**
	 * Copies all bytes available from the {@link InputStream} to the
	 * {@link OutputStream}. If the stream is delayed, due to network traffic, etc,
	 * this method will block until completion or a connection failure. The provided
	 * streams are closed upon termination of this method.
	 */
	@Override
	public void run() {
		final byte[] buf = new byte[BUFFER_SIZE];
		int length;
		InputStream is = inputStream.get();
		OutputStream os = outputStream.get();
		try {
			while ((length = is.read(buf)) > 0) {
				os.write(buf, 0, length);
			}
		} catch (Exception e) {
			throw new RuntimeIOException("Failed to pump stream", e);
		} finally {
			CloseablesExt.close(inputStream.get());
			CloseablesExt.close(outputStream.get());
		}
	}
}
