package uk.fictitiousurl.audiorelay;

/**
 * Store used by Server to enable chunk of audio data to be relayed. In the
 * future this could be extended to store a number of chunks to provide a
 * buffer.
 * 
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioStore {

	/**
	 * Currently the AudioStore stores a single "chunk" of audio.
	 */
	private AudioRecord storedAudio = null;

	/**
	 * Setter to store an audio chunk.
	 * 
	 * @param supplied
	 *            the audio chunk to store.
	 */
	public synchronized void setStoredAudio(AudioRecord supplied) {
		storedAudio = new AudioRecord(supplied.getAudioFormat(),
				supplied.getBytes());
	}

	/**
	 * Getter provides a clone of the stored audio.
	 * 
	 * @return the a clone of the stored audio or null if nothing is stored;
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
