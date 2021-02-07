package library.assistant.ui.main;

import javafx.concurrent.Task;
import library.assistant.utils.UtilAccessToken;
import netscape.javascript.JSException;
import okhttp3.*;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonPostVoiceFile extends Task {

    String accessToken = "";
    File wavFile;
    int responseCode = 0;

    public JsonPostVoiceFile(String accessToken, File wavFile) {
        this.accessToken = accessToken;
        this.wavFile = wavFile;
    }

    public static void main(String[] args) {
        try {
            File file = new File("D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\AUD-20201026-WA0006.wav");
            JsonPostVoiceFile jsonPostVoiceFile = new JsonPostVoiceFile(UtilAccessToken.accessToken, file);
            jsonPostVoiceFile.setOnSucceeded((succeededEvent) -> {
                if (jsonPostVoiceFile.responseCode == 200){
                    System.out.println("The file sent successfully!");
                } else {
                    System.out.println("The file not sent!");
                }
            });
            jsonPostVoiceFile.setOnFailed((failedEvent) -> {
                System.out.println("OnFailed!");
            });
            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(jsonPostVoiceFile);
            executorService.shutdown();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected Object call() throws Exception {
        try {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("file", wavFile);
//            } catch (JSException e){
//                e.printStackTrace();
//                System.out.println(e.getMessage());
//            }
//            OkHttpClient okHttpClient = new OkHttpClient().newBuilder().build();
//            MediaType mediaType = MediaType.parse("application/json");
//            RequestBody requestBody = RequestBody.create(mediaType, wavFile);
//            Request request = new Request.Builder().url("http://deepmine.ir:8890/api/speech2text/").method("POST", requestBody).addHeader("Authorization", "Token {"+accessToken+"}").addHeader("X-CSRFToken","dTESXmXRrcnvU5pM8QXOum3XXAgh2soUPZLpGyKk2we6p8M8C4gIhP0hThm3JraF").build();
//            Response response = okHttpClient.newCall(request).execute();
//            CookieJar cookieJar = okHttpClient.cookieJar();
//            System.out.println(cookieJar);
//            responseCode = response.code();
//            String result = response.body().string();
//            System.out.println(response.toString());
//            System.out.println(result);
//            System.out.println(responseCode);

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("file", "D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\AUD-20201026-WA0006.wav", RequestBody.create(MediaType.parse("application/octet-stream"), new File("D:\\IntelliJ\\Mr.Rasekh_JavaFX\\Library-Assistant\\AudioFiles\\AUD-20201026-WA0006.wav"))).build();
            Request request = new Request.Builder().url("http://deepmine.ir:8890/api/speech2text/").method("POST", body).addHeader("Authorization", "Token "+accessToken).addHeader("X-CSRFToken", "dTESXmXRrcnvU5pM8QXOum3XXAgh2soUPZLpGyKk2we6p8M8C4gIhP0hThm3JraF").addHeader("Cookie", "csrftoken=dTESXmXRrcnvU5pM8QXOum3XXAgh2soUPZLpGyKk2we6p8M8C4gIhP0hThm3JraF").build();
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            System.out.println("speech2text: "+result);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
    }
}
