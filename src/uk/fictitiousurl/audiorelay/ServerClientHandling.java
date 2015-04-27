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
	/**
	 * to get text instructions from the client
	 */
	private BufferedReader fromClient;
	/**
	 *  to send replies to the client 
	 */
	private PrintWriter toClient;

	public ServerClientHandling(Socket signalSocket, int id) {
		super("ServerThread");
		this.signalSocket = signalSocket;
		this.id = id;
	}

	public void run() {
		System.out.println("log_connection_id_" + id + ": starting thread");

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
			Mode serverMode = Mode.RECEIVER;
			if (id == 0)
				serverMode = Mode.SENDER;
			clientAsk = fromClient.readLine();
			System.out.println("log_connection_id_" + id + ": received: "
					+ clientAsk);
			if ("askMode".equals(clientAsk)) {
				String reply = serverMode.name();
				toClient.println(reply);
				System.out.println("log_connection_id_" + id + ": sent back "
						+ reply);
			} else {
				System.err.println("ERROR for connection_id_" + id
						+ " failed to send 'askID'" + " instead sent '"
						+ clientAsk + "'");
				return;
			}

			if (serverMode == Mode.SENDER) {
				sender();
			} else {
				receiver();
			}

		} catch (IOException ex) {
			System.err.println("ERROR connect_id_" + id
					+ " IOException caught on signalSocket details: "
					+ ex.getMessage());
			return; //
		}

		System.out.println("log_connection_id_" + id + ": ending thread");
	}

	/**
	 * the client is a sender.
	 * @throws IOException 
	 */
	public void sender() throws IOException {
		// must first get the audio format back
		String tempAF = fromClient.readLine();
		System.out.println("debug string audioformat" + tempAF);
		System.out.println("sender to be written"); // TODO
	}

	public void receiver() {
		System.out.println("receiver to be written"); // TODO
	}

}
