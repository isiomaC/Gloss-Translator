package com.chuck.artranslate.dbresources;

import android.text.TextUtils;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;


@Table(database = MyDatabase.class)
public class synonymTable extends BaseModel {
    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String mWord;

    @Column
    public String mSynonyms;

    public int getId() {
        return id;
    }

    public synonymTable() {}

    public synonymTable(String mWord, ArrayList<String> mSynonyms){

        this.mSynonyms = TextUtils.join("," , mSynonyms);
        this.mWord = mWord;

    }

    public String getmWord() {
        return mWord;
    }

    public String[] getmSynonyms() {
        return mSynonyms.split(",") ;
    }

}
