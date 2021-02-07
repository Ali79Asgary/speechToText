package library.assistant.ui.strip;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.ui.main.SoundRecorder;
import library.assistant.ui.main.Stopwatch;
import library.assistant.util.LibraryAssistantUtil;
import org.apache.logging.log4j.Level;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StripController implements Initializable {

    File audioFile;
    boolean isRecordPressed = true;
    SoundRecorder soundRecorder;
    Stopwatch stopwatch;

    @FXML
    public JFXButton btnRecordStrip;

    @FXML
    private JFXButton btnCloseStrip;

    @FXML
    private JFXButton btnMagnifyStrip;

    @FXML
    private Label lblTimerStrip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnRecordStrip.setShape(new Circle(100));
        btnCloseStrip.setShape(new Circle(100));
        btnMagnifyStrip.setShape(new Circle(100));

        isRecordPressed = true;
        stopwatch = new Stopwatch(lblTimerStrip);
        stopwatch.starter();

        try {
            Font fontIRANSans = Font.loadFont(new FileInputStream(new File("D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\src\\resources\\IRANSans.TTF")), 12);
            lblTimerStrip.setFont(fontIRANSans);
            btnRecordStrip.setFont(fontIRANSans);
            btnMagnifyStrip.setFont(fontIRANSans);
            btnCloseStrip.setFont(fontIRANSans);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    public void closeStrip(ActionEvent event) {
        try {
            close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    @FXML
    void magnifyStrip(ActionEvent event) {
        loadMain();
    }

    @FXML
    void startRecordingStrip(ActionEvent event) {
        if (isRecordPressed){
            btnRecordStrip.setText("پایان ظبط");
            System.out.println("شروع ظبط");

            stopwatch.play();
            soundRecorder = new SoundRecorder();
            Thread thread = new Thread(soundRecorder);
            thread.start();

            isRecordPressed = false;
        } else {
            btnRecordStrip.setText("شروع ظبط");
            System.out.println("پایان ظبط");

            stopwatch.reset(lblTimerStrip);
            soundRecorder.finish();
            soundRecorder.cancel();

            isRecordPressed = true;
        }
    }

    public void close() {
        try {
            Stage stage = (Stage) btnCloseStrip.getScene().getWindow();
            stage.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    void loadMain() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("دیپ ماین");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
