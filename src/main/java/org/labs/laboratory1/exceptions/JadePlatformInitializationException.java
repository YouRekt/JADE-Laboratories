package org.labs.laboratory1.exceptions;

public class JadePlatformInitializationException extends RuntimeException {

	public JadePlatformInitializationException(final Throwable cause) {
		super("Could't initialize JADE platform.", cause);
	}
}
