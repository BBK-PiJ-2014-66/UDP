package uk.fictitiousurl.audiorelay;

import static uk.fictitiousurl.audiorelay.AudioUtils.sendAudioFormatDownTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
	private AudioStore audioStore;
	/**
	 * to get text instructions from the client
	 */
	private BufferedReader fromClient;
	/**
	 * to send replies to the client
	 */
	private PrintWriter toClient;

	public ServerClientHandling(Socket signalSocket, int id,
			AudioStore audioStore) {
		super("ServerThread");
		this.signalSocket = signalSocket;
		this.id = id;
		this.audioStore = audioStore;
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

		// audio data will come in via UDP
		int serverUDPport = Ports.UDPSTART + id;
		try (DatagramSocket serverSocket = new DatagramSocket(serverUDPport);) {
			System.out.println("log_connection_id_" + id + ": UDP port set to "
					+ serverUDPport);
			byte[] receiveData = new byte[100000]; // over dimensioned

			while (true) { // ask for and receive audio
				System.out.println("log_connection_id_" + id
						+ ": ask for new chunk of audio");
				toClient.println("send");
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				serverSocket.receive(receivePacket);
				int bytesLength = receivePacket.getLength();
				byte[] audio = new byte[bytesLength];
				System.arraycopy(receivePacket.getData(), 0, audio, 0,
						bytesLength);

				AudioRecord receivedAR = new AudioRecord(audioformat, audio);
				// store audio in audioStore
				audioStore.setStoredAudio(receivedAR);
				System.out.println("log_connection_id_" + id
						+ ": received and stored " + bytesLength
						+ " bytes audio chunk");
				// sleep for the length of the audio Recording
				int duration = receivedAR.getDurationInMilliSecs();
				System.out.println("log_connection_id_" + id
						+ ": sleep for duration of the audio chunk = "
						+ duration + " millisecs");
				try {
					sleep(duration);
				} catch (InterruptedException ex) {
					System.out.println("log_connection_id_" + id
							+ ": warning InterruptedException in sleep? " + ex);
				}
			}
		}

	}

	/**
	 * Client is a receiver
	 */
	private void receiver() {
		// make sure there is some audio to send.
		while (audioStore.getStoredAudio()==null) {
			System.out.println("log_connection_id_" + id
					+ ": no audio to transmit, sleep 2 seconds?");
			try {
				sleep(2000);
			} catch (InterruptedException ex) {
				System.out.println("log_connection_id_" + id
						+ ": warning InterruptedException in sleep? " + ex);
			}	
		}
		AudioFormat audioformat = audioStore.getStoredAudio().getAudioFormat();
		System.out.println("log_connection_id_" + id
				+ ": sending the audio format " + audioformat + " to client");
		sendAudioFormatDownTCP( toClient, audioformat);

		
		System.out.println("receiver to be finished"); // TODO
	}

}
