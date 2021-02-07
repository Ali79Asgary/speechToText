package library.assistant.ui.main;

import sun.audio.AudioStream;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class WaveData {

    private byte[] audioBytes;
    private float[] audioData;
    private AudioFormat format;
    private double durationSec;

    public WaveData() {}

    public byte[] getAudioBytes() { return audioBytes; }

    public float[] getAudioData() { return audioData; }

    public AudioFormat getFormat() { return format; }

    public double getDurationSec() { return durationSec; }

    public float[] extractAmplitudeFromFile(File wavFile) throws Exception{
        FileInputStream fileInputStream = new FileInputStream(wavFile);
        byte[] arrFile = new byte[(int) wavFile.length()];
        fileInputStream.read(arrFile);
        return extractAmplitudeFromFileByteArray(arrFile);
    }

    public float[] extractAmplitudeFromFileByteArray(byte[] arrFile) throws Exception{
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(arrFile);
        return extractAmplitudeFromFileByteArrayInputStream(byteArrayInputStream);
    }

    public float[] extractAmplitudeFromFileByteArrayInputStream(ByteArrayInputStream bis) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bis);
        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
        durationSec = milliseconds / 1000.0;
        return extractFloatDataFromAudioInputStream(audioInputStream);
    }

    public float[] extractFloatDataFromAudioInputStream(AudioInputStream audioInputStream) throws Exception {
        format = audioInputStream.getFormat();
        audioBytes = new byte[(int) (audioInputStream.getFrameLength() * format.getFrameSize())];
        // calculate durationSec
        float milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / audioInputStream.getFormat().getFrameRate());
        durationSec = milliseconds / 1000.0;
        // System.out.println("The current signal has duration "+durationSec+" Sec");
        audioInputStream.read(audioBytes);
        return extractFloatDataFromAmplitudeByteArray(format, audioBytes);
    }

    public float[] extractFloatDataFromAmplitudeByteArray(AudioFormat format, byte[] audioBytes) {
        // convert
        audioData = null;
        if (format.getSampleSizeInBits() == 16) {
            int nlengthInSamples = audioBytes.length / 2;
            audioData = new float[nlengthInSamples];
            if (format.isBigEndian()) {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is MSB (high order) */
                    int MSB = audioBytes[2 * i];
                    /* Second byte is LSB (low order) */
                    int LSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            } else {
                for (int i = 0; i < nlengthInSamples; i++) {
                    /* First byte is LSB (low order) */
                    int LSB = audioBytes[2 * i];
                    /* Second byte is MSB (high order) */
                    int MSB = audioBytes[2 * i + 1];
                    audioData[i] = MSB << 8 | (255 & LSB);
                }
            }
        } else if (format.getSampleSizeInBits() == 8) {
            int nlengthInSamples = audioBytes.length;
            audioData = new float[nlengthInSamples];
            if (format.getEncoding().toString().startsWith("PCM_SIGN")) {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i];
                }
            } else {
                for (int i = 0; i < audioBytes.length; i++) {
                    audioData[i] = audioBytes[i] - 128;
                }
            }
        } // end of if..else

        return audioData;
    }

    /**
     * Save to file.
     *
     * @param name     the name
     * @param fileType the file type
     */
    public void saveToFile(String name, AudioFileFormat.Type fileType, AudioInputStream audioInputStream) throws Exception {

        System.out.println("Saving ... WaveData.saveToFile() " + name);

        if (audioInputStream == null) {
            return;
        }
        // reset to the beginnning of the captured data
        audioInputStream.reset();
        File myFile = new File(name + ".wav");
        int i = 0;
        while (myFile.exists()) {
            String temp = String.format(name + "%d", i++);
            myFile = new File(temp + ".wav");
        }
        AudioSystem.write(audioInputStream, fileType, myFile);

        System.out.println("Saved " + myFile.getAbsolutePath());
    }
}
