package uk.fictitiousurl.audiorelay;

/**
 * 
 * Hard-coded port numbers to be used for the communications
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class Ports {

	/**
	 * Ports is an uninstantiable class.
	 */
	private Ports() {
		throw new UnsupportedOperationException("Uninstantiable class");
	}

	/**
	 * the port number for the TCP connection used for signalling
	 */
	public static int SIGNAL = 7777;

	/**
	 * the starting port number for UDP connections. client id=0 will user this
	 * port, id=1 will use the next port etc.
	 */
	public static int UDPSTART = 7780;

}
