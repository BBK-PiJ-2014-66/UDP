package uk.fictitiousurl.audiorelay;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server for client-application where clients send or receive looping audio.
 * 
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class Server {


	public static void main(String[] args) {
		Server theServer = new Server();
		theServer.launch();
	}

	public void launch() {
		try {
			@SuppressWarnings("resource") // never close signalSocket
			ServerSocket signalSocket = new ServerSocket(Ports.SIGNAL);
			int connectionNumb = 0;
			while (true) {
				System.out.println("log: waiting for client " + connectionNumb
						+ " to connect");
				Socket clientSocket = signalSocket.accept();
				System.out.println("log: client " + connectionNumb
						+ " has connected");
				// launch thread to handle this connection
				new ServerClientHandling( clientSocket, connectionNumb).start();
				connectionNumb++;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
