package uk.fictitiousurl.development;

import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileToByteArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * First attempt to send audio data by UDP, based on Keith's echo server sending
 * a byte array with the sound information rather than text.
 * 
 * @see uk.fictitiousurl.development.TestAudioClient TestAudioClient for the
 *      corresponding client.

 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class TestAudioServer {
	public static void main(String args[]) throws Exception {

		// read in three short audio files containing A, B an C
		String aFile = "./audioFiles/A.aiff";
		String bFile = "./audioFiles/B.aiff";
		String cFile = "./audioFiles/C.aiff";
		byte[] aByte = null, bByte = null, cByte = null;
		try {
			aByte = audioFileToByteArray(aFile);
			bByte = audioFileToByteArray(bFile);
			cByte = audioFileToByteArray(cFile);
		} catch (UnsupportedAudioFileException | IOException ex) {
			System.err.println("ERROR exception reading audio file. Details '"
					+ ex.getMessage());
			System.exit(1); // terminate with error
		}

		DatagramSocket serverSocket = new DatagramSocket(2000);

		int count = 0;
		while (true) {
			byte[] receiveData = new byte[1024];
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);

			// get an instruction from the client. In practice
			// this is always "send" but code up "STOP" support
			String instruction = new String(receivePacket.getData());
			instruction = instruction.trim(); // trim of whitespace
			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();
			System.out.println("Instruction: '" + instruction + "' from IP: "
					+ IPAddress + " port: " + port);

			if ("STOP".equals(instruction)) {
				break;
			} else	if ("SEND".equals(instruction)) {
				/*
				 * instead of sending back a string converted to bytes (as in
				 * the echo server), send back the "A" audio data, then "B"
				 * audio data, the "C" audio data
				 */
				byte[] tByte;
				if (count % 3 == 0)
					tByte = aByte;
				else if (count % 3 == 1)
					tByte = bByte;
				else
					tByte = cByte;
				DatagramPacket sendPacket = new DatagramPacket(tByte,
						tByte.length, IPAddress, port);
				serverSocket.send(sendPacket);
			
			} else {
				System.err.println("ERROR unrecognized instruction '"
						+ instruction + "' will ignore it");
			}
			count++;
		}

		//serverSocket.close();
	}
}