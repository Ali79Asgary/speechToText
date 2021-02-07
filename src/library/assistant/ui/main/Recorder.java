package library.assistant.ui.main;

import javax.sound.sampled.TargetDataLine;

public class Recorder implements Runnable {

    MainControllerVoiceToText mainController;
    TargetDataLine targetDataLine;
    Thread thread;

    public Recorder(MainControllerVoiceToText mainController) { this.mainController = mainController; }

    public void start(){

    }

    @Override
    public void run() {

    }
}
