package uk.fictitiousurl.audiorelay;

/**
 * Store used by Server to enable chunk of audio data to be relayed
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioStore {
	private AudioRecord storedAudio = null;

	/**
	 * @param storedAudio
	 *            the storedAudio to set
	 */
	public synchronized void setStoredAudio(AudioRecord supplied) {
		storedAudio = new AudioRecord(supplied.getAudioFormat(),
				supplied.getBytes());
	}

	/**
	 * Getter Return a clone of the stored audio
	 * 
	 * @return the storedAudio or null if nothing stored;
	 */
	public synchronized AudioRecord getStoredAudio() {
		AudioRecord clone = null;
		if (storedAudio != null) {
			clone = new AudioRecord(storedAudio.getAudioFormat(),
					storedAudio.getBytes());
		}
		return clone;
	}

}
