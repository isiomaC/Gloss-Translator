package com.chuck.artranslate.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TranslateApi {

    private static class MsApi {
        private static final String apiKey = "<API Key>";
        private static final String host =
                "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&to=de&to=en";
    }

    private static class OxApi {

        private static final String appID = "0d5a7709";
        private static final String apiKey = "<API key>";
        private static final String host =
                "https://od-api.oxforddictionaries.com:443/api/v1/entries/en/%s/synonyms;antonyms";

        static String getHost(String text) {
            return String.format(host, text.toLowerCase());
        }

    }

    private Tasks mTask;

    private int mResponseCode;

    private List<RequestBody> objList = new ArrayList<>();

    public enum Tasks {
        translate, synonym
    }

    public TranslateApi(@NonNull Tasks task) {
        mTask = task;
    }

    public void postText(String text) {
        objList.add(new RequestBody(text));
    }


    public String connect(String text) {

        if(App.getInstance().isInternetConnected()) {

            try {

                URL url = null;
                String content = null;

                if (mTask.equals(Tasks.translate)) {

                    url = new URL(MsApi.host);
                    content = new Gson().toJson(objList);

                } else if (mTask.equals(Tasks.synonym)) {

                    url = new URL(OxApi.getHost(text));

                }

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Content-Type", "application/json");

                if (content != null) {

                    connection.setDoOutput(true);
                    connection.setRequestProperty("X-ClientTraceId", UUID.randomUUID().toString());
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Ocp-Apim-Subscription-Key", MsApi.apiKey);

                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    byte[] encoded_content = content.getBytes("UTF-8");
                    wr.write(encoded_content, 0, encoded_content.length);
                    wr.flush();
                    wr.close();

                } else {

                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestProperty("app_id", OxApi.appID);
                    connection.setRequestProperty("app_key", OxApi.apiKey);

                }

                mResponseCode = connection.getResponseCode();

                if (taskSuccess()) {

                    StringBuilder response = new StringBuilder();
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(),
                            "UTF-8"));

                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    Log.e("resonse", response.toString());

                    return response.toString();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "";
    }

    protected final boolean taskSuccess() {
        return mResponseCode == HttpURLConnection.HTTP_OK;
    }

    public static class RequestBody {

        String Text;

        public RequestBody(String text) {
            Text = text;
        }

    }
}
