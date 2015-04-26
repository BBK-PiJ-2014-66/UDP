package uk.fictitiousurl.development;

import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileToByteArray;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.UnsupportedAudioFileException;

import uk.fictitiousurl.audiorelay.AudioRecord;

/**
 * Server for more ambitious attempt to send audio over UDP - send 9 seconds of
 * Bach Cello.
 * 
 * @see uk.fictitiousurl.development.TestAudioClientBach TestAudioClientBach for
 *      the corresponding client.
 * 
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class TestAudioServerBach {
	public static void main(String args[]) throws Exception {

		AudioRecord Bach[] = new AudioRecord[9];
		for (int ic = 0; ic < 9; ic++) {
			Bach[ic] = new AudioRecord("./audioFiles/Bach" + (ic + 1) + ".wav");
		}

		System.out.println("9 seconds of Bach loaded, format is "
				+ Bach[0].getAudioFormat());

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
			} else if ("SEND".equals(instruction)) {
				/*
				 * send Bach in sequence
				 */
				byte[] tByte = Bach[(count % 9)].getBytes();
				DatagramPacket sendPacket = new DatagramPacket(tByte,
						tByte.length, IPAddress, port);
				serverSocket.send(sendPacket);

			} else {
				System.err.println("ERROR unrecognized instruction '"
						+ instruction + "' will ignore it");
			}
			count++;
		}

		// serverSocket.close();
	}
}