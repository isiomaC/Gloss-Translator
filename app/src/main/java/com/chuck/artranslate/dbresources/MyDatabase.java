package com.chuck.artranslate.dbresources;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

@Database(name = MyDatabase.NAME, version = MyDatabase.VERSION)
public class MyDatabase {
    public static final String NAME = "MyDataBase";

    public static final int VERSION = 1;

    public String getName(){
        return NAME;
    }

    public static void deleteAll(){
        Delete.table(DBresources.class);
    }

    public static  List<synonymTable> getAllSynonyms(){
        return SQLite.select()
                .from(synonymTable.class)
                .orderBy(synonymTable_Table.mWord.desc())
                .queryList();
    }

    public static List<DBresources> getAll (){
        return SQLite.select()
                .from(DBresources.class)
                .orderBy(DBresources_Table.detection.desc())
                .queryList();
    }

    public static DBresources getTranslation(String str ) {
        return SQLite.select()
                .from(DBresources.class)
                .where(DBresources_Table.detection.is(str))
                .querySingle();
    }

    public static boolean translationExists(String text) {
        return SQLite.select()
                .from(DBresources.class)
                .where(DBresources_Table.detection.is(text)).count() > 0;
    }

    public static synonymTable getSynonym(String word) {
        return SQLite.select()
                .from(synonymTable.class)
                .where(synonymTable_Table.mWord.is(word)).querySingle();
    }

    public static boolean synonymExists(String word) {
        return SQLite.select()
                .from(synonymTable.class)
                .where(synonymTable_Table.mWord.is(word)).count() > 0;
    }
}
