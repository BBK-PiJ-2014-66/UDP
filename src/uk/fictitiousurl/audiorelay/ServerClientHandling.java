package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
	private Socket signalSocket = null;
	private int id;

	public ServerClientHandling(Socket signalSocket, int id) {
		super("ServerThread");
		this.signalSocket = signalSocket;
		this.id = id;
	}

	public void run() {
		System.out.println("log_connection_id_" + id + ": starting thread");

		BufferedReader fromClient; // to get text instructions from client
		try {
			fromClient = new BufferedReader(new InputStreamReader(
					signalSocket.getInputStream()));
			String clientSentence = fromClient.readLine();
			System.out.println("log_connection_id_" + id + " Received: "
					+ clientSentence);

		} catch (IOException ex) {
			System.err.println("ERROR connect_id_" + id
					+ " IOException caught on signalSocket details: "
					+ ex.getMessage());
			return; //
		}

		System.out.println("log_connection_id_" + id + ": ending thread");
	}

}
