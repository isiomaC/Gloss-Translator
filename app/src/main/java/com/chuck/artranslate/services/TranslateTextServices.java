package com.chuck.artranslate.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.chuck.artranslate.fragments.CameraFragment;
import com.chuck.artranslate.utils.MyBroadcastReceiver;
import com.chuck.artranslate.utils.Stemmer;
import com.chuck.artranslate.utils.TranslateApi;
import com.chuck.artranslate.utils.Translation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;


public class TranslateTextServices extends Service {

    private static Stemmer stemmer = new Stemmer();

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {

        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            TranslateApi.Tasks task = null;
            String id = null;

            switch (msg.arg2) {

                case 1:
                    task = TranslateApi.Tasks.translate;
                    id = CameraFragment.BROADCAST_ID;
                    break;

                case 2:
                    task = TranslateApi.Tasks.synonym;
                    id = CameraFragment.SYNONYM_BROADCAST_ID;
                    break;

            }

            if (task != null) {

                TranslateApi translateApi;

                String result = "";

                Bundle data = msg.getData();
                String text = data.getString("text");

                if (msg.arg2 == 1) {

                    translateApi = new TranslateApi(TranslateApi.Tasks.translate);
                    translateApi.postText(text);
                    result = translateApi.connect(null);
                    MyBroadcastReceiver.sendBroadcast(id, "text", result);

                } else if (msg.arg2 == 2) {

                    String language = data.getString("lang");

                    if (language != null && language.equalsIgnoreCase("german")) {

                        // Translate the german word to English
                        translateApi = new TranslateApi(TranslateApi.Tasks.translate);
                        translateApi.postText(text);
                        result = translateApi.connect(null);

                        // Unwrapping JSON Array received with the translation object get method
                        Translation enTranslation = Translation.get(result);
                        if (enTranslation != null) {

                            // Get the synonyms of the word gotten from the JSON Array
                            result = new TranslateApi(TranslateApi.Tasks.synonym)
                                    .connect(stemWord(enTranslation.text.toCharArray()));
                            ArrayList<String> synonyms = Translation.getSynonyms(result);

                            // Send the english synonyms individually from the ArrayList
                            translateApi = new TranslateApi(TranslateApi.Tasks.translate);
                            for (String word : synonyms) {
                                translateApi.postText(word);
                            }
                            result = translateApi.connect(null);

                            MyBroadcastReceiver.sendBroadcast(id, "de", result);
                        }

                    } else {

                        result = new TranslateApi(TranslateApi.Tasks.synonym).connect(text);
                        MyBroadcastReceiver.sendBroadcast(id, "en", result);

                    }

                }

            }

        }

    }

    public static String prettify(String json_text) {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(json_text);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(json);
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    public static final String TRANSLATE = "do_translate",
            SYNO = "do_syno",
            STOP = "stop",
            UNSUPPORTED = "unsupported";

    private int startId = 0;

    MyBroadcastReceiver myBroadcastReceiver;

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        myBroadcastReceiver = new MyBroadcastReceiver(new String[] {
                TRANSLATE, SYNO, STOP}) {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (action != null) {

                    if (action.equals(STOP)) {

                        stopSelf();

                    } else {

                        Message msg = mServiceHandler.obtainMessage();
                        Bundle b = new Bundle();
                        msg.arg1 = startId++;

                        switch (action) {
                            case TRANSLATE:
                                b.putString("text", intent.getStringExtra("text"));
                                msg.arg2 = 1;
                                break;

                            case SYNO:
                                b = intent.getExtras();
                                msg.arg2 = 2;
                                break;
                        }

                        Log.e("Bundle value for " + action , "" + b);
                        msg.setData(b);
                        mServiceHandler.sendMessage(msg);

                    }

                }

            }
        };

    }

    static String stemWord(char[] wordChars) {
        stemmer.add(wordChars, wordChars.length);
        stemmer.stem();
        return stemmer.toString();

    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_LONG).show();
        MyBroadcastReceiver.register(myBroadcastReceiver);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service stopping", Toast.LENGTH_LONG).show();
        MyBroadcastReceiver.unregister(myBroadcastReceiver);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void start(Context context) {
        context.startService(new Intent(context, TranslateTextServices.class));
    }

}



