package uk.fictitiousurl.audiorelay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for client-application where clients send or receive looping audio.
 * 
 * <p>
 * 
 * For details on how to run the program see <a
 * href="https://github.com/BBK-PiJ-2014-66/UDP"
 * target="_blank">https://github.com/BBK-PiJ-2014-66/UDP </a> .
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class Server {

	/**
	 * store for the audio information to be relayed.
	 */
	private AudioStore audioStore;

	/**
	 * The main program
	 * 
	 * @param args
	 *            ignored as there are no command line arguments
	 * 
	 */
	public static void main(String[] args) {
		Server theServer = new Server();
		theServer.launch();
	}

	/**
	 * waits for clients to connect to the TCP set in Ports.SIGNAL and launches
	 * a new thread using {@link ServerClientHandling#run() ServerClientHandling#run()}
	 */
	public void launch() {
		audioStore = new AudioStore();
		try {
			@SuppressWarnings("resource")
			// never close signalSocket
			ServerSocket signalSocket = new ServerSocket(Ports.SIGNAL);
			int connectionNumb = 0;
			while (true) {
				System.out.println("log: waiting for client " + connectionNumb
						+ " to connect");
				Socket clientSocket = signalSocket.accept();
				System.out.println("log: client " + connectionNumb
						+ " has connected");
				// launch thread to handle this connection
				new ServerClientHandling(clientSocket, connectionNumb,
						audioStore).start();
				connectionNumb++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
