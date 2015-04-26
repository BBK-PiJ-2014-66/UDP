package uk.fictitiousurl.development;

import uk.fictitiousurl.audiorelay.AudioRecord;

/**
 * Simple test of the AudioRecord Object. Open an audio file and play the sound.
 * Extended to play 9 seconds of Bach Cello Suite Number 1.
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class AudioRecordTest {

	public static void main(String[] args) {
		
		AudioRecord Bach[] = new AudioRecord[9];
		for (int ic = 0; ic < 9; ic++) {
			Bach[ic] = new AudioRecord("./audioFiles/Bach" + (ic + 1) + ".wav");
		}
		
		System.out.println("9 seconds of Bach loaded, format is " + Bach[0].getAudioFormat());

		// test playing back - looping 3 times
		for (int ic = 0; ic < 3; ic++) {
			for (AudioRecord arit : Bach) {
				arit.play();
			}
		}
	}

}
