package library.assistant.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextArea;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.ui.strip.StripController;
import library.assistant.util.LibraryAssistantUtil;
import library.assistant.utils.UtilAccessToken;
import sun.plugin2.message.JavaScriptBaseMessage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainControllerVoiceToText implements Initializable {

    public Group decibelSoundMeterGroup;
    public AnchorPane decibelSoundMeterParent;
    public AnchorPane decibelSoundMeterShow;
    LevelMeter meter;
    Thread recordThread;
    //new recorder fields
    private static final long serialVersionUID = 1L;
    byte[] audioBytes = null;
    float[] audioData = null;
    final int BUFFER_SIZE = 16384;
    final FormatControlConf formatControlConf = new FormatControlConf();
//    final Recorder recorder = new Recorder(meter);

    //last recorder fields
    File audioFile;
    boolean isRecordPressed = true;
    SoundRecorder soundRecorder;
    Stopwatch stopwatch;
    boolean isStripOpen = true;
    StripController stripController = new StripController();

    @FXML
    private JFXButton btnRecord;

    @FXML
    private Label lblTimer;

    @FXML
    private StackPane rootPane;

    @FXML
    private JFXDrawer drawer;

    @FXML
    private AnchorPane rootAnchorPane;

    @FXML
    private JFXTabPane mainTabPane;

    @FXML
    private Tab bookIssueTab;

    @FXML
    private JFXButton btnFileChooser;

    @FXML
    private Circle startRecordingCircle;

    @FXML
    private JFXTextArea lblVoiceText;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnRecord.setShape(new Circle(100));
        isRecordPressed = true;
        stopwatch = new Stopwatch(lblTimer);
        stopwatch.starter();
        meter = new LevelMeter();
        meter.setPreferredSize(new Dimension(9, 100));
        decibelSoundMeterShow.setPrefWidth(50);

        try {
            Font fontIRANSans = Font.loadFont(new FileInputStream(new File("src\\resources\\IRANSans.TTF")), 20);
            lblVoiceText.setFont(fontIRANSans);
            lblTimer.setFont(fontIRANSans);
            btnRecord.setFont(fontIRANSans);
            btnFileChooser.setFont(fontIRANSans);
        } catch (Exception e){
            e.printStackTrace();
        }
//        mainTabPane.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//                if (newValue) {
//                    System.out.println("on focus tab pane");
//                    loadStrip(isStripOpen);
//                } else {
//                    System.out.println("out focus tab pane");
//                    loadStrip(isStripOpen);
//                }
//            }
//        });
    }

    @FXML
    void loadFileChooser(ActionEvent event) {
        audioFile = selectFile(getStage());
    }

    @FXML
    void startRecording(ActionEvent event) {
        decibelSoundMeterShow.setPrefWidth(80);
        if (isRecordPressed){
            btnRecord.setText("پایان ظبط");
            System.out.println("شروع ظبط");

            stopwatch.play();

            recordThread = new Thread(new Recorder(meter));
            recordThread.start();
//            soundRecorder = new SoundRecorder();
//            Thread thread = new Thread(soundRecorder);
//            thread.start();

            isRecordPressed = false;
        } else {
            btnRecord.setText("شروع ظبط");
            System.out.println("پایان ظبط");

            stopwatch.reset(lblTimer);

            JsonPostVoiceFile jsonPostVoiceFile = new JsonPostVoiceFile(UtilAccessToken.accessToken, audioFile, lblVoiceText);
            jsonPostVoiceFile.setOnSucceeded((succeededEvent) ->{
                if (jsonPostVoiceFile.responseCode == 200){
                    if (jsonPostVoiceFile.resultStatus == 200){
                        lblVoiceText.setText(jsonPostVoiceFile.text);
                    }
                }
            });
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(jsonPostVoiceFile);
            executorService.shutdown();
            recordThread.interrupt();
//            soundRecorder.finish();
//            soundRecorder.cancel();

            isRecordPressed = true;
        }
    }

    public File selectFile(Stage stage){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("فایل صوتی خود را انتخاب نمایید!");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("wav files", "*.wav"));
        return fileChooser.showOpenDialog(stage);
    }

    public void loadStrip(boolean isStripOpen) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/strip/strip.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            if (!isStripOpen){
                stage.setTitle("Strip");
                stage.setScene(new Scene(parent));
                stage.setAlwaysOnTop(true);
                stage.initStyle(StageStyle.TRANSPARENT);
                stage.show();
                LibraryAssistantUtil.setStageIcon(stage);
                this.isStripOpen = true;
            } else {
                stage.close();
                this.isStripOpen = false;
            }

        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void closeStrip() {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/strip/strip.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Strip");
            stage.setScene(new Scene(parent));
            stage.close();
            LibraryAssistantUtil.setStageIcon(stage);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    private Stage getStage() {
        return (Stage) rootPane.getScene().getWindow();
    }

    //unusable methods.
    @FXML
    void handleAboutMenu(ActionEvent event) {

    }

    @FXML
    void handleIssuedList(ActionEvent event) {

    }

    @FXML
    void handleMenuAddBook(ActionEvent event) {

    }

    @FXML
    void handleMenuAddMember(ActionEvent event) {

    }

    @FXML
    void handleMenuClose(ActionEvent event) {

    }

    @FXML
    void handleMenuFullScreen(ActionEvent event) {

    }

    @FXML
    void handleMenuOverdueNotification(ActionEvent event) {

    }

    @FXML
    void handleMenuSettings(ActionEvent event) {

    }

    @FXML
    void handleMenuViewBook(ActionEvent event) {

    }

    @FXML
    void handleMenuViewMemberList(ActionEvent event) {

    }
}
