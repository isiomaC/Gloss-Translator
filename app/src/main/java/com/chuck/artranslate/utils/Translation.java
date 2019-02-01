package com.chuck.artranslate.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.chuck.artranslate.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Translation {

    public String text=" ", sourceLang=" ", translationLang=" ";

    private static Context applicationContext = App.getInstance().getApplicationContext();

    public static Translation get(String string) {

        Translation translation = new Translation();

        try {

            JSONObject getVal = new JSONArray(string).getJSONObject(0);

            JSONArray translations = getVal.optJSONArray("translations");
            JSONObject detectedlang = getVal.optJSONObject("detectedLanguage");

            JSONObject germ = translations.optJSONObject(0);
            JSONObject engl = translations.optJSONObject(1);

            if (detectedlang.get("language").equals("de")) {

                translation.text = engl.optString("text");
                translation.sourceLang = applicationContext.getString(R.string.germanString);
                translation.translationLang = applicationContext.getString(R.string.englishString);

            } else if (detectedlang.get("language").equals("en")) {

                translation.text = germ.optString("text");
                translation.sourceLang = applicationContext.getString(R.string.englishString);
                translation.translationLang = applicationContext.getString(R.string.germanString);
                Log.e("translationlang", translation.translationLang);

            } else {
                translation.sourceLang = "unsupportedLanguage";
                Toast.makeText(applicationContext,
                        "Unsupported Language", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return translation;

    }

    public static Translation[] getAll(String string) {

        Translation[] translation = new Translation[0];
        try {

            JSONArray jsonArray = new JSONArray(string);
            JSONArray translations;

            JSONObject detectedlang;
            JSONObject getVal;

            translation = new Translation[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++) {

                getVal = jsonArray.getJSONObject(i);

                translations = getVal.optJSONArray("translations");
                detectedlang = getVal.optJSONObject("detectedLanguage");

                JSONObject germ = translations.optJSONObject(0);
                JSONObject engl = translations.optJSONObject(1);

                translation[i] = new Translation();

                if (detectedlang.get("language").equals("de")) {

                    translation[i].text = engl.optString("text");
                    translation[i].sourceLang = applicationContext.getString(R.string.germanString);
                    translation[i].translationLang = applicationContext.getString(R.string.englishString);

                } else if (detectedlang.get("language").equals("en")) {

                    translation[i].text = germ.optString("text");
                    translation[i].sourceLang = applicationContext.getString(R.string.englishString);
                    translation[i].translationLang = applicationContext.getString(R.string.germanString);

                } else {
                    Toast.makeText(applicationContext,
                            "Unsupported Language", Toast.LENGTH_LONG).show();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return translation;
    }

    public static ArrayList<String> getSynonyms(String string) {

        ArrayList<String> synonyms = new ArrayList<>();

        if (!string.isEmpty()) {

            try {

                JSONArray data = new JSONObject(string)
                        .optJSONArray("results")
                        .optJSONObject(0)
                        .optJSONArray("lexicalEntries")
                        .optJSONObject(0)
                        .optJSONArray("entries")
                        .optJSONObject(0)
                        .optJSONArray("senses")
                        .optJSONObject(0)
                        .optJSONArray("synonyms");

                for (int i = 0; i < data.length(); i++) {
                    synonyms.add(data.optJSONObject(i).optString("text"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return synonyms;
    }
}
