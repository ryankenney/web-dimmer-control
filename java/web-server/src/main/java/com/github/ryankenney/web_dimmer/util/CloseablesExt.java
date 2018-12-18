package com.github.ryankenney.web_dimmer.util;

import java.io.Closeable;
import java.io.IOException;

public class CloseablesExt {

	public static void close(Closeable closeable) {
		try {
			closeable.close();
		} catch (IOException e) {
			throw new RuntimeIOException("Failed to close", e);
		}
	}
}
