package uk.fictitiousurl.development;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
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
		try {
			aByte = audioFileToByteArray(fileName);
		} catch (UnsupportedAudioFileException | IOException ex) {
			// TODO Auto-generated catch block
			System.err.println("ERROR exception reading audio file. Details '"
					+ ex.getMessage());
			System.exit(1); // terminate with error
		}
		System.out.println(" read audio file " + fileName + " contains "
				+ aByte.length + " bytes,  hashcode " + Arrays.hashCode(aByte));

	}

	/**
	 * Reads the complete contents of a small audio file into a byte array
	 * 
	 * @param fileName the audio file
	 * @return byte array with the complete contents.
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

		AudioFormat format = audioStream.getFormat();
		int numbBytes = audioStream.available();
		System.out.println("log format= " + format);
		System.out.println("log available bytes= " + numbBytes);
		System.out.println("log frames per sec= " + format.getFrameRate());
		System.out.println("log number of bytes in frame= "
				+ format.getFrameSize());
		double secondsLength = audioStream.available()
				/ (format.getFrameSize() * format.getFrameRate());
		System.out
				.println("log length of clip = " + secondsLength + " seconds");
		byte[] bytesBuffer = new byte[numbBytes];
		audioStream.read(bytesBuffer);

		return bytesBuffer;
	}
	
	
}