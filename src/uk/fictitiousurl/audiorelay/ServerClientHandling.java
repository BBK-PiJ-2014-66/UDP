package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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

		BufferedReader fromClient; // to get text instructions from the client
		PrintWriter toClient; // to send replies to the client
		try {
			fromClient = new BufferedReader(new InputStreamReader(
					signalSocket.getInputStream()));
			toClient = new PrintWriter(signalSocket.getOutputStream(), true);
			String clientAsk = fromClient.readLine();
			System.out.println("log_connection_id_" + id + ": received: "
					+ clientAsk);
			// client must first ask for its ID
			if ("askID".equals(clientAsk)) {
				toClient.println(id);
				System.out
						.println("log_connection_id_" + id + ": sent back id");
			} else {
				System.err.println("ERROR for connection_id_" + id
						+ " failed to send 'askID'" + " instead sent '"
						+ clientAsk + "'");
				return;
			}
			// then the mode
			clientAsk = fromClient.readLine();
			System.out.println("log_connection_id_" + id + ": received: "
					+ clientAsk);
			if ("askMode".equals(clientAsk)) {
				String reply;
				if (id == 0) { // first client is the sender TODO (for now)
					reply = Mode.SENDER.name();
				} else {
					reply = Mode.RECEIVER.name();
				}
				toClient.println(reply);
				System.out
						.println("log_connection_id_" + id + ": sent back " + reply);
			} else {
				System.err.println("ERROR for connection_id_" + id
						+ " failed to send 'askID'" + " instead sent '"
						+ clientAsk + "'");
				return;
			}
			
			// TODO implement UDP audio handling

		} catch (IOException ex) {
			System.err.println("ERROR connect_id_" + id
					+ " IOException caught on signalSocket details: "
					+ ex.getMessage());
			return; //
		}

		System.out.println("log_connection_id_" + id + ": ending thread");
	}

}
