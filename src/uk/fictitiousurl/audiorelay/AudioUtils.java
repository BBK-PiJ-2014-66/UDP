package uk.fictitiousurl.audiorelay;

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
 * Utilities to handle reading and playing audio files.
 *
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioUtils {
	/**
	 * Reads the complete contents of a small audio file into a byte array
	 * 
	 * @param fileName
	 *            the audio file
	 * @return byte array with the contents read from the file
	 * 
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws FileNotFoundException
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

		/*
		 * double secondsLength = audioStream.available()
		 * /(format.getFrameSize() * format.getFrameRate());
		 * 
		 * System.out .println("log length of clip = " + secondsLength +
		 * " seconds");
		 */

	}

	/**
	 * Returns the AudioFormat of an audio file
	 * 
	 * @param fileName
	 *            the audio file
	 * @return its audio format
	 * @throws UnsupportedAudioFileException
	 * @throws IOException
	 * @throws FileNotFoundException
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
	 * Send AudioFormat down a TCP type connection
     *
	 * @param tcpTo the connection a PrintWriter
	 * @param format the Audio Format to send
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
	
}