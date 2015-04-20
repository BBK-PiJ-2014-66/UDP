package uk.fictitiousurl.audiorelay;

import java.net.Socket;

/**
 * Server-Client handling class for client-application where clients send or
 * receive looping audio.
 * 
 * <br>
 * based on ideas from
 * http://docs.oracle.com/javase/tutorial/networking/sockets/
 * examples/KKMultiServerThread.java
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class ServerClientHandling extends Thread {
	private Socket socket = null;
	private int id;

	public ServerClientHandling(Socket socket, int id) {
		super("ServerThread");
		this.socket = socket;
		this.id = id;
	}

	public void run() {
		System.out.println("log_connection_id_" + id + ": starting thread");

		System.out.println("log_connection_id_" + id + ": ending thread");
	}

}
