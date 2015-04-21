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
		PrintWriter toServer;
		BufferedReader fromServer;
		try (Socket clientSocket = new Socket(hostname, Ports.SIGNAL);) {
			toServer = new PrintWriter(clientSocket.getOutputStream(), true);
			fromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			System.out.println("log: connected to host " + hostname
					+ " port number " + Ports.SIGNAL);
			// First ask for ID
			System.out.println("log: asking for ID");
			toServer.println("askID");
			// get the info back from the Server
			String id = fromServer.readLine();
			if (id.matches("\\d+")) { // one or more digits
				System.out.println("log: got back valid ID = '" + id + "'");
			} else {
				System.err.println("ERROR failed to get back valid ID,"
						+ " instead got '" + id + "'");
				return;
			}

		} catch (UnknownHostException ex) {
			System.err.println("ERROR UnknownHostException caught "
					+ "trying to create new socket on port " + Ports.SIGNAL
					+ "   details: " + ex.getMessage());
			return;
		} catch (IOException ex) {
			System.err
					.println("ERROR IOException caught "
							+ "trying to create new socket/reader/writer or read/write to port "
							+ Ports.SIGNAL + "   details: " + ex.getMessage());
			return;

		}

	}
}
