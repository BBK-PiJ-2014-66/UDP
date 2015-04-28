package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;

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
	 * to send replies to the client
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
	 * 
	 * @throws IOException
	 */
	private void sender() throws IOException {
		// must first get the audio format from sender
		String strEncode = fromClient.readLine();
		System.out.println("log_connection_id_" + id
				+ ": audio format encoding as string '" + strEncode + "'");
		// for the constructor need to convert string back to encoding 
		AudioFormat.Encoding encoding = null;
		if (strEncode.equalsIgnoreCase("ALAW")) {
			encoding = AudioFormat.Encoding.ALAW;
		} else if (strEncode.equalsIgnoreCase("PCM_FLOAT")) {
			encoding = AudioFormat.Encoding.PCM_FLOAT;
		} else if (strEncode.equalsIgnoreCase("PCM_SIGNED")) {
			encoding = AudioFormat.Encoding.PCM_SIGNED;
		} else if (strEncode.equalsIgnoreCase("PCM_UNSIGNED")) {
			encoding = AudioFormat.Encoding.PCM_UNSIGNED;
		} else if (strEncode.equalsIgnoreCase("ULAW")) {
			encoding = AudioFormat.Encoding.ULAW;
		}			
		float sampleRate = Float.parseFloat(fromClient.readLine());
		int sampleSizeInBits = Integer.parseInt(fromClient.readLine());
		int channels = Integer.parseInt(fromClient.readLine());
		int frameSize = Integer.parseInt(fromClient.readLine());
		float frameRate = Float.parseFloat(fromClient.readLine());
		boolean bigEndian = Boolean.parseBoolean(fromClient.readLine());
		AudioFormat audioformat = new AudioFormat(encoding, sampleRate,
				sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
		System.out.println("log_connection_id_" + id
				+ ": audio format received as " + audioformat);
		
		
		//while (true) { // ask for and receive audio
			toClient.println("send");
			
		//}
		
	}

	private void receiver() {
		System.out.println("receiver to be written"); // TODO
	}

}
