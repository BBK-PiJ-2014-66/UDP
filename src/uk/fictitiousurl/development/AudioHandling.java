package uk.fictitiousurl.development;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 *
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioHandling {

	public static void main(String args[]) {

		String fileName = "./audioFiles/A.aiff";
		byte[] aByte = null;
		AudioFormat format = null;
		try {
			aByte = audioFileToByteArray(fileName);
			format = audioFileFormat(fileName);
		} catch (UnsupportedAudioFileException | IOException ex) {
			System.err.println("ERROR exception reading audio file. Details '"
					+ ex.getMessage());
			System.exit(1); // terminate with error
		}
		System.out.println(" read audio file " + fileName + " contains "
				+ aByte.length + " bytes,  hashcode " + Arrays.hashCode(aByte));
		System.out.println("   format " + format);

		try {
			playBack(format,aByte);
		} catch (LineUnavailableException ex) {
			System.err.println("ERROR exception playing back audio. Details '"
					+ ex.getMessage());
			System.exit(1); // terminate with error
		}
	}

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
	 * @param format the format 
	 * @param bytes  byte array with the contents
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
}