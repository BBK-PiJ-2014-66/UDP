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
	 * identity, initially set to no-meaningful value, Server will provide value
	 * greater than or equal to zero
	 */
	private int id = -1;

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
		Mode mode;
		try (Socket clientSocket = new Socket(hostname, Ports.SIGNAL);) {
			toServer = new PrintWriter(clientSocket.getOutputStream(), true);
			fromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			System.out.println("log: connected to host " + hostname
					+ " port number " + Ports.SIGNAL);
			// ask for ID
			System.out.println("log: asking for ID");
			toServer.println("askID");
			// get the info back from the Server
			String strID = fromServer.readLine();
			if (!strID.matches("\\d+")) { // one or more digits
				System.err.println("ERROR failed to get back valid ID,"
						+ " instead got '" + strID + "'");
				return;
			}
			id = Integer.parseInt(strID);
			System.out.println("log_id_" + id + ": got back valid ID = '"
					+ strID + "'");
			// ask for sender/receiver mode
			System.out.println("log_id_" + id + ": asking for mode");
			toServer.println("askMode");
			String reply = fromServer.readLine();
			if (Mode.SENDER.name().equals(reply)) {
				mode = Mode.SENDER;
			} else if (Mode.RECEIVER.name().equals(reply)) {
				mode = Mode.RECEIVER;
			} else {
				System.err
						.println("ERROR 'askMode' failed to get back valid mode,"
								+ " instead got '" + reply + "'");
				return;
			}
			System.out.println("log_id_" + id + ": mode is " + mode);
			if (mode == Mode.SENDER) {
				sender();
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

	private void sender() {
		// Load test audio data 9 seconds of Bach
		AudioRecord Sounds[] = new AudioRecord[9];
		for (int ic = 0; ic < 9; ic++) {
			Sounds[ic] = new AudioRecord("./audioFiles/Bach" + (ic + 1)
					+ ".wav");

		}
		System.out.println("log_id_" + id
				+ ": test audio 9 seconds of Bach loaded in 1 sec chunks");
		System.out.println("log_id_" + id + ": audio format is "
				+ Sounds[0].getAudioFormat());
	}
}