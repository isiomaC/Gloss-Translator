package com.chuck.artranslate.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chuck.artranslate.R;
import com.chuck.artranslate.activities.MainActivity;
import com.chuck.artranslate.dbresources.DBresources;
import com.chuck.artranslate.dbresources.MyDatabase;
import com.chuck.artranslate.services.TranslateTextServices;
import com.chuck.artranslate.utils.App;
import com.chuck.artranslate.utils.FusedLocationClient;
import com.chuck.artranslate.utils.MyBroadcastReceiver;
import com.chuck.artranslate.utils.Translation;
import com.chuck.artranslate.utils.ViewsUtil;
import com.google.android.flexbox.FlexboxLayout;

public class CameraFragment extends OcrCaptureFragment implements View.OnClickListener {

    public static final String BROADCAST_ID = "translate";
    public static final String SYNONYM_BROADCAST_ID = "synonym";
    public static final String LOCATION_ID = "location";

    private FusedLocationClient location;
    private AlertDialog mProgressDialog;

    private TextView mDetectedTextView,
            mDetectedLanguageTextView,
            mTranslatedLanguageTextView,
            miniTextTranslate;

    private FlexboxLayout mTranslatedFlexBox;
    private LinearLayout box;
    private RelativeLayout bottomView;

    private String mDetectedText;
    protected Translation translation;
    private ImageButton iconTranslate;
    private ProgressBar miniProgress;
    private View infoBox;
    private ImageButton flashButton;

    @Override
    protected void initializeDefaults() {
        mLayoutRes = R.layout.fragment_camera;
        mBroadcastIds = new String[] {
                BROADCAST_ID,
                LOCATION_ID
        };

        location = new FusedLocationClient(mActivity);
        location.register();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDetectedTextView = view.findViewById(R.id.detected_text);
        mTranslatedFlexBox = view.findViewById(R.id.translated_text);
        mDetectedLanguageTextView = view.findViewById(R.id.detected_language);
        mTranslatedLanguageTextView = view.findViewById(R.id.translated_language);

        iconTranslate = view.findViewById(R.id.translate);
        iconTranslate.setOnClickListener(this);
        flashButton = view.findViewById(R.id.flash);
        flashButton.setOnClickListener(this);

        view.findViewById(R.id.down).setOnClickListener(this);
        miniProgress = view.findViewById(R.id.progressBar);
        miniTextTranslate = view.findViewById(R.id.translate_text);
        bottomView = view.findViewById(R.id.bottom_view);

        infoBox = view.findViewById(R.id.info);
        box = view.findViewById(R.id.box);
    }

    @Override
    protected void useText(String value) {

        mPreview.stop();

        mDetectedText = value;
        mDetectedTextView.setText(value);

        ViewsUtil.makeVisible(bottomView, mContext, R.anim.slide_up);
        ViewsUtil.makeGone(box);
        ViewsUtil.makeVisible(infoBox);
        miniTextTranslate.setText(R.string.tap_to_translate);

        mTranslatedFlexBox.removeAllViews();

//        Log.e("getTranslationLocation", location.getmCurrentLocation().getLatitude()+"   " + location.getmCurrentLocation().getLongitude());

    }

    @Override
    protected void createOneTimeVariables() {
        mProgressDialog = ViewsUtil.progressDialog(mActivity);
    }

    @Override
    protected void onReceiveBroadcast(String action, Intent intent) {

        Log.e(TAG, action);

        switch (action) {
            case BROADCAST_ID:
                showTranslationBox();
                doTranslate(intent.getStringExtra("text"));
                break;

            case LOCATION_ID:
                ViewsUtil.customDialog(mActivity)
                        .setTitle(R.string.title_location_permission)
                        .setMessage(R.string.text_location_turned_off)
                        .setPositiveButton("TURN ON LOCATION", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.down:
                detect();
                break;

            case R.id.translate:
                translate();
                break;

            case R.id.flash:
                flashButton.setImageResource(changeFlash() ? R.drawable.flash_on : R.drawable.flash_off);
                break;
        }
    }


    private void detect() {
        ViewsUtil.makeGone(bottomView, mContext, R.anim.slide_down);
        ViewsUtil.makeGone(box);
        ViewsUtil.makeVisible(infoBox);
        miniTextTranslate.setText(R.string.tap_to_translate);
        startCameraSource();
    }

    private void translate() {

        mProgressDialog.show();

        iconTranslate.setEnabled(false);
        ViewsUtil.makeInvisible(iconTranslate, mContext, R.anim.fade_out);
        ViewsUtil.makeVisible(miniProgress, mContext, R.anim.fade_in);
        miniTextTranslate.setText(R.string.translating);

        mTranslatedFlexBox.removeAllViews();

        if (MyDatabase.translationExists(mDetectedText)) {
            showTranslationBox();
            doTranslateTable(MyDatabase.getTranslation(mDetectedText));
        } else {
            MyBroadcastReceiver.sendBroadcast(TranslateTextServices.TRANSLATE, "text", mDetectedText);
        }
    }


    private void showTranslationBox() {
        ViewsUtil.makeGone(infoBox, mContext, R.anim.slide_down);
        ViewsUtil.makeGone(miniProgress);
        ViewsUtil.makeVisible(iconTranslate, mContext, R.anim.fade_in);
        iconTranslate.setEnabled(true);
        ViewsUtil.makeVisible(box, mContext, R.anim.slide_up);
    }

    private void doTranslateTable(DBresources trans){

        mProgressDialog.dismiss();

        Log.e("GOTTEN FROM DB",trans+"");
        mDetectedLanguageTextView.setText(getString(R.string.detected_text, trans.sourceText));
        mTranslatedLanguageTextView.setText(getString(R.string.translation_text, trans.destText));

        TextView tt;
        LayoutInflater inf = getLayoutInflater();

        for (String s : trans.getTranslation().replaceAll("[+.^:,]","").trim().split(" ")) {
            tt = (TextView) inf.inflate(R.layout.text, mTranslatedFlexBox, false);

            s = s.trim();

            if(!s.isEmpty()){
                tt.setText(s);
                tt.setOnLongClickListener((MainActivity) mActivity);
                tt.setTag(trans.getDestText());
                mTranslatedFlexBox.addView(tt);
            }
        }

        ViewsUtil.makeVisible(mTranslatedLanguageTextView, mContext, R.anim.fade_in);
        ViewsUtil.makeVisible(mTranslatedFlexBox, mContext, R.anim.fade_in);

    }

    @Override
    public void onResume() {
        super.onResume();
        App.checkLocationPermission(mActivity);
        location.startLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        location.stopLocationUpdates();
    }

    private void doTranslate(String intent) {

        mProgressDialog.dismiss();

        DBresources resources = new DBresources();

        translation = Translation.get(intent);

        mDetectedLanguageTextView.setText(getString(R.string.detected_text, translation.sourceLang));
        mTranslatedLanguageTextView.setText(getString(R.string.translation_text, translation.translationLang));

        TextView tt;
        LayoutInflater inf = getLayoutInflater();


        for (String s : translation.text.replaceAll("[+.^:,]","").trim().split(" ")) {
            tt = (TextView) inf.inflate(R.layout.text, mTranslatedFlexBox, false);

            s = s.trim();

            if(!s.isEmpty()){
                tt.setText(s);
                MainActivity mActivity = (MainActivity) this.mActivity;
                tt.setOnLongClickListener(mActivity);
                tt.setTag(translation.translationLang);
                mTranslatedFlexBox.addView(tt);
            }

        }

        if(!translation.sourceLang.equals("unsupportedLanguage")) {
            resources.insertdData(translation.sourceLang, translation.translationLang, mDetectedText, translation.text);

            if (location.getmCurrentLocation() != null) {
                resources.setLocation(location.getmCurrentLocation().getLatitude(), location.getmCurrentLocation().getLongitude());
            }

            resources.save();
        }

        Log.e("locations....", location.getmLocations().toString());

        ViewsUtil.makeVisible(mTranslatedLanguageTextView, mContext, R.anim.fade_in);
        ViewsUtil.makeVisible(mTranslatedFlexBox, mContext, R.anim.fade_in);

    }
}
