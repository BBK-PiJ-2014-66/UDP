package uk.fictitiousurl.development;



import uk.fictitiousurl.audiorelay.AudioRecord;
/**
 * Simple test of the AudioRecord Object. Open an audio file and play the sound
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 *
 */
public class AudioRecordTest {

	public static void main(String[] args) {
		AudioRecord soundC = new AudioRecord("./audioFiles/C.aiff");
		// play the sound
		soundC.play();
	}

}
