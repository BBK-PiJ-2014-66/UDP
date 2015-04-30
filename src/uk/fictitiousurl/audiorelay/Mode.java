package uk.fictitiousurl.audiorelay;

/**
 * Client's mode.
 */
public enum Mode {
	/**
	 * Client is a sender so will send audio to the server.
	 */
	SENDER,
	/**
	 * Client is a receiver so will receive audio from the server and play it
	 * back.
	 */
	RECEIVER
}
