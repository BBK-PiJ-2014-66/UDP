package uk.fictitiousurl.audiorelay;

import static uk.fictitiousurl.audiorelay.AudioUtils.receiveAudioFormatFromTCP;
import static uk.fictitiousurl.audiorelay.AudioUtils.sendAudioFormatDownTCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;

/**
 * Server-Client handling class for client-application where clients send or
 * receive looping audio.
 * 
 * <br>
 * based on ideas from <a href=
 * "http://docs.oracle.com/javase/tutorial/networking/sockets/examples/KKMultiServerThread.java"
 * target="_blank">
 * http://docs.oracle.com/javase/tutorial/networking/sockets/examples
 * /KKMultiServerThread.java</a> .
 * 
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class ServerClientHandling extends Thread {

	/**
	 * TCP server socket for this connection. Supplied to the constructor when
	 * thread launched.
	 */
	private Socket signalSocket = null;
	/**
	 * A unique id number for this connection. Supplied to the constructor when
	 * thread launched.
	 */
	private int id;
	/**
	 * The audioStore provides a storage space so that the thread dealing with
	 * the sending client can store audio information to be sent out by threads
	 * dealing with receiving clients.
	 */
	private AudioStore audioStore;
	/**
	 * to get text instructions from the client
	 */
	private BufferedReader fromClient;
	/**
	 * to send replies to the client
	 */
	private PrintWriter toClient;

	/**
	 * Constructor
	 * 
	 * @param signalSocket
	 *            the TCP server socket.
	 * @param id
	 *            an ID number unique to this thread
	 * @param audioStore
	 *            the audio store
	 */
	public ServerClientHandling(Socket signalSocket, int id,
			AudioStore audioStore) {
		super("ServerThread");
		this.signalSocket = signalSocket;
		this.id = id;
		this.audioStore = audioStore;
	}

	/**
	 * starts the thread that deals with connection from a client.
	 */
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
	 * deals with communication when the client is a sender.
	 * 
	 * @throws IOException
	 *             if there was a problem receiving or sending information
	 */
	private void sender() throws IOException {
		// get the audio format from sender
		AudioFormat audioformat = receiveAudioFormatFromTCP(fromClient);
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
	 * deals with communication when the client is a receiver.
	 * 
	 * @throws IOException
	 *             if there was a problem receiving or sending information
	 */
	private void receiver() throws IOException {
		// make sure there is some audio to send.
		while (audioStore.getStoredAudio() == null) {
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
		sendAudioFormatDownTCP(toClient, audioformat);

		// will send audio data by UDP but first need to get a packet get the
		// return port
		// audio data will come in via UDP
		int serverUDPport = Ports.UDPSTART + id;
		try (DatagramSocket serverSocket = new DatagramSocket(serverUDPport);) {
			System.out.println("log_connection_id_" + id + ": UDP port set to "
					+ serverUDPport);
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			// get an instruction from the client. This will be "HELLO"
			String instruction = new String(receivePacket.getData());
			instruction = instruction.trim(); // trim of whitespace
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			System.out.println("log_connection_id_" + id
					+ ": UDP instruction: '" + instruction + "' from IP: "
					+ IPAddress + " port: " + port
					+ " so can send UDP to client.");

			while (true) { // infinite loop
				instruction = fromClient.readLine();
				if (instruction.equals("send")) {
					byte[] sendData = audioStore.getStoredAudio().getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, IPAddress, port);
					serverSocket.send(sendPacket);

					System.out.println("log_connection_id_" + id
							+ ": received \"send\", so sent " + sendData.length
							+ " bytes audio data.");
				} else {
					throw new RuntimeException("ERROR unrecognized"
							+ "TCP instruction from client = '" + instruction
							+ "'");

				}

			}

		}

	}

}
