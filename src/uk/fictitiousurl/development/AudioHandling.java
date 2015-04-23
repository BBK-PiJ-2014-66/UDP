package uk.fictitiousurl.development;

import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileFormat;
import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileToByteArray;
import static uk.fictitiousurl.audiorelay.AudioUtils.playBack;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * test AudioUtils
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
}
