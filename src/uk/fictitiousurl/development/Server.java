package uk.fictitiousurl.development;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Example UDP echo server
 * 
 * downloaded from https://bitbucket.org/keithmannock/pij14-15/raw/657d73
 * cc0adf1fb9bec1a5712b4966274fb9926a/network/src/udp/Server.java
 * 
 * @author Keith Mannock (modified a bit by Oliver).
 *
 */
public class Server {
	public static void main(String args[]) throws Exception {
		DatagramSocket serverSocket = new DatagramSocket(2000);

		while (true) {
			byte[] receiveData = new byte[1024];
			byte[] sendData;
			DatagramPacket receivePacket = new DatagramPacket(receiveData,
					receiveData.length);
			serverSocket.receive(receivePacket);
			String sentence = new String(receivePacket.getData());

			InetAddress IPAddress = receivePacket.getAddress();
			int port = receivePacket.getPort();

			System.out.println("RECEIVED: '" + sentence + "' from IP: "
					+ IPAddress + " port: " + port);
			String capitalizedSentence = sentence.toUpperCase();
			sendData = capitalizedSentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, IPAddress, port);
			serverSocket.send(sendPacket);
		}
	}
}