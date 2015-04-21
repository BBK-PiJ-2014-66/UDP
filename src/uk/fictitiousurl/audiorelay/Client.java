package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The Client for client-application where clients send or receive looping
 * audio.
 * 
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class Client {

	/**
	 * The user can specify an optional hostname as a command line argument. If
	 * they do not specify a value use the default value "localhost".
	 * 
	 * @param args
	 *            a single value the hostname to connect to.
	 */
	public static void main(String[] args) {
		String hostname = "localhost";
		if (args.length == 1) {
			hostname = args[0];
		} else if (args.length != 0) {
			System.err.println("Usage: java Client <optional host name>");
			System.exit(1);
		}

		Client theClient = new Client();
		theClient.launch(hostname);
	}

	public void launch(String hostname) {
        // First setup the signal communication socket, writer and reader
		Socket clientSocket;
		PrintWriter toServer;
		BufferedReader fromServer;
		try {
			clientSocket = new Socket(hostname, Ports.SIGNAL);
			toServer = new PrintWriter(clientSocket.getOutputStream(),
					true);
			fromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
		} catch (UnknownHostException ex) {
			System.err.println("ERROR UnknownHostException caught "
					+ "trying to create new socket on port " + Ports.SIGNAL
					+ "   details: " + ex.getMessage());
			return;
		} catch (IOException ex) {
			System.err.println("ERROR IOException caught "
					+ "trying to create new socket/reader/writer on port " + Ports.SIGNAL
					+ "   details: " + ex.getMessage());
			return;

		}
		System.out.println("log: connected to host " + hostname
				+ " port number " + Ports.SIGNAL);

		try {
	    	// First ask for ID
			System.out.println("log: asking for ID");
	        toServer.println("askID");
	        // get the info back from the Server
	        String back = fromServer.readLine();
			System.out.println("log: got back " + back);
		
		} catch (IOException ex) {
			System.err.println("ERROR IOException caught "
					+ "reading/writing to port " + Ports.SIGNAL
					+ "   details: " + ex.getMessage());
			return;

		}


		
	}

}
