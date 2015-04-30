package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.sound.sampled.AudioFormat;

import static uk.fictitiousurl.audiorelay.AudioUtils.receiveAudioFormatFromTCP;
import static uk.fictitiousurl.audiorelay.AudioUtils.sendAudioFormatDownTCP;

/**
 * The Client program for client-server application where clients either send or
 * receive looping audio.
 * 
 * <p>
 * 
 * For details on how to run the program see <a
 * href="https://github.com/BBK-PiJ-2014-66/UDP"
 * target="_blank">https://github.com/BBK-PiJ-2014-66/UDP </a> .
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
	 * TCP connection to the server
	 */
	private PrintWriter toServer;

	/**
	 * TCP connection from server
	 */
	private BufferedReader fromServer;

	/**
	 * hostname for the server (default is localhost).
	 */
	private String hostName;

	/**
	 * Main program
	 * 
	 * The user can specify an optional host name as a command line argument. If
	 * they do not specify a value use the default value "localhost".
	 * 
	 * @param args
	 *            a single value the host name to connect to.
	 */
	public static void main(String[] args) {
		String hName = "localhost";
		if (args.length == 1) {
			hName = args[0];
		} else if (args.length != 0) {
			System.err.println("Usage: java Client <optional host name>");
			System.exit(1);
		}

		Client theClient = new Client();
		theClient.launch(hName);
	}

	/**
	 * does the actual work
	 * 
	 * @param hName
	 *            hostname either localhost or got as a user-specified command
	 *            line argument
	 */
	public void launch(String hName) {
		hostName = hName;
		// First setup the signal communication socket, writer and reader
		Mode mode;
		try (Socket clientSocket = new Socket(hostName, Ports.SIGNAL);) {
			toServer = new PrintWriter(clientSocket.getOutputStream(), true);
			fromServer = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));

			System.out.println("log: connected to host " + hostName
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
			} else {
				receiver();
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

	/**
	 * The client is a sender
	 */
	private void sender() {
		// Load test audio data 9 seconds of Bach
		AudioRecord sounds[] = new AudioRecord[9];
		for (int ic = 0; ic < 9; ic++) {
			sounds[ic] = new AudioRecord("./audioFiles/Bach" + (ic + 1)
					+ ".wav");
		}

		AudioFormat audioformat = sounds[0].getAudioFormat();

		System.out.println("log_id_" + id
				+ ": test audio 9 seconds of Bach loaded in 1 sec chunks");
		System.out.println("log_id_" + id + ": sending audio format "
				+ audioformat + " to server");
		sendAudioFormatDownTCP(toServer, audioformat);

		// setup UDP connection
		try (DatagramSocket clientSocket = new DatagramSocket()) {
			InetAddress IPAddress = InetAddress.getByName(hostName);
			int serverUDPport = Ports.UDPSTART + id;
			System.out.println("log_id_" + id + ": UDP connection to "
					+ IPAddress + " port number " + serverUDPport);
			while (true) { // infinite loop
				for (AudioRecord sound : sounds) { // loop thru the audio
					// wait for server to send a "send" instruction
					String instruction;
					try {
						instruction = fromServer.readLine();
					} catch (IOException ex) {
						throw new RuntimeException(
								"ERROR exception in getting "
										+ "TCP instruction from server. Details: "
										+ ex);
					}
					if (instruction.equals("send")) {
						byte[] sendData = sound.getBytes();
						DatagramPacket sendPacket = new DatagramPacket(
								sendData, sendData.length, IPAddress,
								serverUDPport);
						clientSocket.send(sendPacket);

						System.out.println("log_id_" + id + ": sent "
								+ sendData.length + " bytes audio data."
								+ " sequence "
								+ hashcode2sequence(sound.hashCode()));
					} else {
						throw new RuntimeException("ERROR unrecognized"
								+ "TCP instruction from server = '"
								+ instruction + "'");

					}
				}
				System.out.println("log_id_" + id + ": restarting audio loop");
			}
		} catch (IOException ex) {
			throw new RuntimeException("ERROR exception in UDP "
					+ "connection. Details: " + ex);
		}
	}

	/**
	 * the client is a receiver so will get and play audio.
	 * 
	 * @throws IOException
	 *             if there is a problem
	 */
	private void receiver() throws IOException {
		// get the audio format from sender
		AudioFormat audioformat = receiveAudioFormatFromTCP(fromServer);
		System.out.println("log_id_" + id + ": audio format received as "
				+ audioformat);
		// setup UDP connection
		try (DatagramSocket clientSocket = new DatagramSocket()) {
			InetAddress IPAddress = InetAddress.getByName(hostName);
			int serverUDPport = Ports.UDPSTART + id;
			System.out.println("log_id_" + id + ": UDP connection to "
					+ IPAddress + " port number " + serverUDPport);

			// send "HELLO" to let Server know the return IP and socket
			byte[] sendData = "HELLO".getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, serverUDPport);
			clientSocket.send(sendPacket);

			byte[] receiveData = new byte[100000]; // to receive audio byte
													// array

			while (true) {
				long startTime = System.currentTimeMillis();
				// tell Server to send next audio chunk via TCP
				toServer.println("send");
				// also send HELLO again to keep the UDP connection alive
				// preventing
				// UDP NAT timeouts
				clientSocket.send(sendPacket);

				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				// set a socket timeout of 200 millisecs
				clientSocket.setSoTimeout(200);
				try {
					clientSocket.receive(receivePacket);
					int bytesLength = receivePacket.getLength();
					byte[] audio = new byte[bytesLength];
					System.arraycopy(receivePacket.getData(), 0, audio, 0,
							bytesLength);
					AudioRecord receivedAR = new AudioRecord(audioformat, audio);
					long endTime = System.currentTimeMillis();
					System.out.println("log_id_" + id
							+ " received new audio chunk, sequence "
							+ hashcode2sequence(receivedAR.hashCode())
							+ " . Now play chunk.");
					receivedAR.play();
					long playTime = System.currentTimeMillis() - endTime;
					System.out.println("log_id_" + id + " transmission time "
							+ (endTime - startTime)
							+ " millisecs. playing time " + playTime
							+ " millisecs");

				} catch (SocketTimeoutException ex) {
					System.out.println("log_id_" + id
							+ " SocketTimeoutException: " + ex);
				}
			}

		}

	}

	/**
	 * Used for testing to convert hashcode of bytes to an int sequence number
	 * 
	 * @param hashcode
	 *            an AudioRecord hashcode
	 * @return a sequence number that is easier to be read by a human
	 */
	private static int hashcode2sequence(int hashcode) {
		HashMap<Integer, Integer> mapHash2Sequence = new HashMap<Integer, Integer>();
		// the hash codes for "9 seconds of Bach"
		mapHash2Sequence.put(-365997042, 1);
		mapHash2Sequence.put(182636277, 2);
		mapHash2Sequence.put(-1521414040, 3);
		mapHash2Sequence.put(1359779218, 4);
		mapHash2Sequence.put(1448440695, 5);
		mapHash2Sequence.put(-914633892, 6);
		mapHash2Sequence.put(1113035881, 7);
		mapHash2Sequence.put(-1877807058, 8);
		mapHash2Sequence.put(1578275581, 9);
		if (mapHash2Sequence.containsKey(hashcode)) {
			return mapHash2Sequence.get(hashcode);
		}
		return -1;
	}

}