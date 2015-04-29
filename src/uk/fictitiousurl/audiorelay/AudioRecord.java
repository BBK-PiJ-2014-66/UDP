package uk.fictitiousurl.audiorelay;

import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileFormat;
import static uk.fictitiousurl.audiorelay.AudioUtils.audioFileToByteArray;
import static uk.fictitiousurl.audiorelay.AudioUtils.playBack;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Object to store an audio "record": a short audio recording read from a file,
 * or transmitted using UDP.
 *
 * @author Oliver Smart {@literal <osmart01@dcs.bbk.ac.uk>}
 */
public class AudioRecord {
	private byte[] bytes;
	private AudioFormat audioFormat;

	/**
	 * construct audio record from a file
	 * 
	 * @param filename
	 *            the filename including any path
	 * @throws RuntimeException
	 *             if there is a problem opening or reading from the file
	 */
	public AudioRecord(String fileName) {
		try {
			bytes = audioFileToByteArray(fileName);
			audioFormat = audioFileFormat(fileName);
		} catch (UnsupportedAudioFileException | IOException ex) {
			throw new RuntimeException("ERROR reading audio record from file "
					+ fileName + " details: " + ex.getMessage());
		}
	}
	
	/**
	 * construct an audio record from an audioFormat and byte array. 
	 * 
	 * @param audioFormat the audio format
	 * @param bytes the byte array
	 */
	public AudioRecord(  AudioFormat audioFormat, byte[] bytes) {
		this.audioFormat = audioFormat;
		this.bytes = bytes;
	}

	/**
	 * Getter for the byte array.
	 * 
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * Getter for the audio Format.
	 * 
	 * @return the audioFormat
	 */
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}

	/**
	 * play back the audio record via speakers.
	 */
	public void play() {
		try {
			playBack(audioFormat, bytes);
		} catch (LineUnavailableException ex) {
			throw new RuntimeException(
					"ERROR playing audio via speakers. Details: "
							+ ex.getMessage());
		}

	}
	
	/**
	 * return the duration of the clip in milliseconds
	 * @return the duration in milliseconds
	 */
	public int getDurationInMilliSecs() {
		double secs = bytes.length/(audioFormat.getFrameSize()*audioFormat.getFrameRate());
		return (int) (1000*secs);
	}


	/**
	 * hashCode uses just byte array and ignores format (a bit limited)
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(bytes);
		return result;
	}

	/**
	 * equals uses just byte array and ignores format (a bit limited)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof AudioRecord))
			return false;
		AudioRecord other = (AudioRecord) obj;
		if (!Arrays.equals(bytes, other.bytes))
			return false;
		return true;
	}
	
	

}
