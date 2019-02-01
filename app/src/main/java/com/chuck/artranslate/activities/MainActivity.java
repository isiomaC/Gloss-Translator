package com.chuck.artranslate.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.chuck.artranslate.R;
import com.chuck.artranslate.dbresources.MyDatabase;
import com.chuck.artranslate.dbresources.synonymTable;
import com.chuck.artranslate.fragments.CameraFragment;
import com.chuck.artranslate.fragments.HistoryFragment;
import com.chuck.artranslate.services.TranslateTextServices;
import com.chuck.artranslate.utils.MyBroadcastReceiver;
import com.chuck.artranslate.utils.Translation;
import com.chuck.artranslate.utils.ViewsUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private static final int NUM_PAGES = 2;
    public static final String SYNONYM_BROADCAST_ID = "synonym";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private ViewPager mPager;
    private String word;
    MyBroadcastReceiver myBroadcastReceiver;

    public static void start(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        myBroadcastReceiver = new MyBroadcastReceiver(SYNONYM_BROADCAST_ID ) {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action != null && action.equals(SYNONYM_BROADCAST_ID)) {

                    if (intent.hasExtra("de")) {
                        doSynonymGerman(intent.getStringExtra("de"));
                    } else if (intent.hasExtra("en")) {
                        doSynonymEnglish(intent.getStringExtra("en"));
                    }
                }
            }
        };

        mPager = findViewById(R.id.pager);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(1);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
//                if(position == 1){
//                    TranslateTextServices.start(MainActivity.this);
//                }else{
//                    MyBroadcastReceiver.sendBroadcast(TranslateTextServices.STOP);
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        TranslateTextServices.start(MainActivity.this);
        MyBroadcastReceiver.register(myBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        MyBroadcastReceiver.sendBroadcast(TranslateTextServices.STOP);
        MyBroadcastReceiver.unregister(myBroadcastReceiver);
    }

    public void switchBack(int position) {
        mPager.setCurrentItem(position);
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onLongClick(View view) {

        word = ((TextView) view).getText().toString();

        if (MyDatabase.synonymExists(word)) {
            doSynonymTable(MyDatabase.getSynonym(word));
        } else {
            Bundle b = new Bundle();
            b.putString("lang", (String) view.getTag());
            b.putString("text", word);
            MyBroadcastReceiver.sendBroadcast(TranslateTextServices.SYNO, b);
            Toast.makeText(this, b.getString("text"), Toast.LENGTH_LONG).show();
        }
        return true;
    }

    public void doSynonymTable(synonymTable text) {

        String[] synonyms = text.getmSynonyms();
        StringBuilder dialogMsg = new StringBuilder();
        for (String a: synonyms){
            dialogMsg.append(a).append(", ");
        }
        ViewsUtil.customDialog(this)
                .setTitle(R.string.synonymString)
                .setMessage(dialogMsg)
                .setPositiveButton("OK", null)
                .show();

    }


    private void doSynonymGerman(String text) {

        Translation[] synonyms = Translation.getAll(text);
        ArrayList<String> words = new ArrayList<>();
        StringBuilder dialogMsg = new StringBuilder();
        for (Translation a: synonyms) {
            dialogMsg.append(a.text).append(", ");
            words.add(a.text);
        }
        if (words.size() > 0) {
            new synonymTable(word, words).save();
        }
        ViewsUtil.customDialog(this)
                .setTitle(R.string.synonymString)
                .setMessage(dialogMsg)
                .setPositiveButton("OK", null).show();

    }

    private void doSynonymEnglish(String text){

        ArrayList<String> synonyms = Translation.getSynonyms(text);
        if (synonyms.size() > 0) {
            new synonymTable(word, synonyms).save();
        }
        StringBuilder dialogMsg = new StringBuilder();
        for (String a: synonyms){
            dialogMsg.append(a).append(", ");
        }
        ViewsUtil.customDialog(this)
                .setTitle(R.string.synonymString)
                .setMessage(dialogMsg)
                .setPositiveButton("OK", null)
                .show();

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if (position == 0) {
                return new HistoryFragment();
            } else {
                return new CameraFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
