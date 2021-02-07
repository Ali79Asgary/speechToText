package library.assistant.ui.main;

import javafx.concurrent.Task;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Objects;

public class SoundRecorder extends Task<Void> {

    static final long RECORD_TIME = 60000;

    int filesCount = Objects.requireNonNull(new File("D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\AudioFiles").listFiles()).length;

    File wavFile = new File("D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\AudioFiles\\RecordFile"+filesCount+".wav");

    AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    TargetDataLine targetDataLine;

    @Override
    protected Void call() throws Exception {
        try {
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            if (!AudioSystem.isLineSupported(info)){
                System.out.println("Line not supported!");
                System.exit(0);
            }
            targetDataLine = (TargetDataLine) AudioSystem.getLine(info);
            targetDataLine.open(format);
            targetDataLine.start();

            System.out.println("Start Capturing!");

            AudioInputStream audioInputStream = new AudioInputStream(targetDataLine);

            System.out.println("Start Recording!");

            AudioSystem.write(audioInputStream, fileType, wavFile);

        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 8;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
        return format;
    }

    public void finish(){
        targetDataLine.stop();
        targetDataLine.close();
        System.out.println("Finished!");
    }
}
