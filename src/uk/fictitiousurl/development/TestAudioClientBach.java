package uk.fictitiousurl.development;

import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileFormat;
import static uk.fictitiousurl.audiorelay.AudioUtils.playBack;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import uk.fictitiousurl.audiorelay.AudioRecord;

/**
 * Initial test client to receive and play sound over a UDP connection. 
 * 
 * @see uk.fictitiousurl.development.TestAudioServer TestAudioServer for the
 *      server
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class TestAudioClientBach {
	public static void main(String args[]) throws Exception {

		// For now cheat to get format of the sound files.
		// In real application will send/get via TCP?
		AudioRecord Bach1 = new AudioRecord("./audioFiles/Bach1.wav");
		AudioFormat format = Bach1.getAudioFormat();

		byte[] receiveData = new byte[100000]; // over dimensioned


		// try with resources
		try (DatagramSocket clientSocket = new DatagramSocket()) {
			// N.B. localhost hardcoded for now
			InetAddress IPAddress = InetAddress.getByName("localhost");

			while (true) { // infinite loop

				// always send "SEND"
				byte[] sendData = "SEND".getBytes();
				DatagramPacket sendPacket = new DatagramPacket(sendData,
						sendData.length, IPAddress, 2000);
				clientSocket.send(sendPacket);

				// get back the length of the array byte array
				DatagramPacket receivePacket = new DatagramPacket(receiveData,
						receiveData.length);
				clientSocket.receive(receivePacket);
				int bytesLength = receivePacket.getLength();
				System.out
						.println("FROM SERVER: got " + bytesLength + " bytes");
				byte[] audio = new byte[bytesLength];
				System.arraycopy(receivePacket.getData(), 0, audio, 0,
						bytesLength);

				try {
					playBack(format, audio);
				} catch (LineUnavailableException ex) {
					System.err
							.println("ERROR exception playing back audio. Details '"
									+ ex.getMessage());
					System.exit(1); // terminate with error
				}

			}
		}
	}
}
