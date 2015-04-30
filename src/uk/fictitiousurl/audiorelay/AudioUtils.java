package uk.fictitiousurl.audiorelay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Static utilities to handle reading and playing audio files.
 *
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioUtils {
	
	
	/**
	 * AudioUtils is an uninstantiable class.
	 */
	private AudioUtils() {
		throw new UnsupportedOperationException("Uninstantiable class");
	}

	
	/**
	 * Reads the complete contents of a small audio file into a byte array
	 * 
	 * @param fileName
	 *            the audio file
	 * @return byte array with the contents read from the file
	 * 
	 * @throws UnsupportedAudioFileException
	 *             if there is a problem with audio format of the file not being
	 *             supported by Java
	 * @throws IOException
	 *             if there is a problem reading from the file
	 * @throws FileNotFoundException
	 *             if the file does not exist or cannot be opened
	 */
	public static byte[] audioFileToByteArray(String fileName)
			throws UnsupportedAudioFileException, IOException,
			FileNotFoundException {
		if (!new File(fileName).isFile()) {
			throw new FileNotFoundException("Error the audio file: " + fileName
					+ " does not exist");
		}

		File audioFile = new File(fileName);

		AudioInputStream audioStream = AudioSystem
				.getAudioInputStream(audioFile);

		int numbBytes = audioStream.available();
		byte[] bytesBuffer = new byte[numbBytes];
		audioStream.read(bytesBuffer);
		audioStream.close();
		return bytesBuffer;
	}

	/**
	 * Returns the AudioFormat of an audio file
	 * 
	 * @param fileName
	 *            the audio file
	 * @return its audio format
	 * @throws UnsupportedAudioFileException
	 *             if there is a problem with audio format of the file not being
	 *             supported by Java
	 * @throws IOException
	 *             if there is a problem reading from the file
	 * @throws FileNotFoundException
	 *             if the file does not exist or cannot be opened
	 */
	public static AudioFormat audioFileFormat(String fileName)
			throws UnsupportedAudioFileException, IOException,
			FileNotFoundException {
		if (!new File(fileName).isFile()) {
			throw new FileNotFoundException("Error the audio file: " + fileName
					+ " does not exist");
		}

		AudioInputStream audioStream = AudioSystem
				.getAudioInputStream(new File(fileName));

		AudioFormat format = audioStream.getFormat();
		audioStream.close();
		return format;
	}

	/**
	 * Play back stored audio
	 * 
	 * @param format
	 *            the format
	 * @param bytes
	 *            byte array with the contents
	 * @throws LineUnavailableException
	 *             if there is a problem opening the audio line for play back.
	 */
	public static void playBack(AudioFormat format, byte[] bytes)
			throws LineUnavailableException {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
		audioLine.open(format);
		audioLine.start();
		audioLine.write(bytes, 0, bytes.length);
		audioLine.drain();
		audioLine.close();

	}

	/**
	 * Send AudioFormat down a TCP type connection, so it can be decoded by
	 * {@link #receiveAudioFormatFromTCP(BufferedReader)
	 * receiveAudioFormatFromTCP}
	 *
	 * @param tcpTo
	 *            the connection a PrintWriter
	 * @param format
	 *            the Audio Format to send
	 */
	public static void sendAudioFormatDownTCP(PrintWriter tcpTo,
			AudioFormat format) {
		tcpTo.println(format.getEncoding());
		tcpTo.println(format.getSampleRate());
		tcpTo.println(format.getSampleSizeInBits());
		tcpTo.println(format.getChannels());
		tcpTo.println(format.getFrameSize());
		tcpTo.println(format.getFrameRate());
		tcpTo.println(format.isBigEndian());
	}

	/**
	 * Receive AudioFormat from a TCP type connection as sent by
	 * {@link #sendAudioFormatDownTCP(PrintWriter, AudioFormat)
	 * sendAudioFormatDownTCP}
	 * 
	 * @param tcpFrom
	 *            the TCP type connection
	 * @return the AudioFormat
	 * @throws IOException
	 *             if there is a problem
	 */
	public static AudioFormat receiveAudioFormatFromTCP(BufferedReader tcpFrom)
			throws IOException {
		String strEncode = tcpFrom.readLine();
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
		float sampleRate = Float.parseFloat(tcpFrom.readLine());
		int sampleSizeInBits = Integer.parseInt(tcpFrom.readLine());
		int channels = Integer.parseInt(tcpFrom.readLine());
		int frameSize = Integer.parseInt(tcpFrom.readLine());
		float frameRate = Float.parseFloat(tcpFrom.readLine());
		boolean bigEndian = Boolean.parseBoolean(tcpFrom.readLine());
		AudioFormat audioformat = new AudioFormat(encoding, sampleRate,
				sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
		return audioformat;
	}
}