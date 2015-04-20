package uk.fictitiousurl.development;

/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.net.*;
import java.io.*;

/**
 *
 * Example TCP echo client adapted from
 * 
 * http://docs.oracle.com/javase/tutorial/networking/sockets/examples/EchoServer
 * .java
 * 
 * run by
 * 
 * java -cp bin uk.fictitiousurl.development.EchoClient localhost 7777
 * 
 * @author Oracle, adapted by Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class EchoServer {
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			System.out.println("# loginfo: before serverSocket.accept()");
			Socket clientSocket = serverSocket.accept();
			System.out.println("# loginfo: after serverSocket.accept()");

			PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(),
					true);
			BufferedReader fromClient = new BufferedReader(new InputStreamReader(
					clientSocket.getInputStream()));
			String inputLine;
			while ((inputLine = fromClient.readLine()) != null) {
				System.out.println("# loginfo: received '" + inputLine + "'");
				// convert to uppercase
				toClient.println(inputLine.toUpperCase());
			}
			serverSocket.close();
		} catch (IOException e) {
			System.out
					.println("Exception caught when trying to listen on port "
							+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}
	}
}
