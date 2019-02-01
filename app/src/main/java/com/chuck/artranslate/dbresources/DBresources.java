package com.chuck.artranslate.dbresources;

import com.chuck.artranslate.utils.MyBroadcastReceiver;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;

@Table(database = MyDatabase.class)
public class DBresources extends BaseModel implements Serializable {

    @Column
    @PrimaryKey(autoincrement = true)
    public int id;

    @Column
    public String sourceText;

    @Column
    public String destText;

    @Column
    public String detection;

    @Column
    public String translation;

    @Column
    public double latitude;

    @Column
    public double longitude;

    public void insertdData(String sourceText, String destText, String detection, String translation){
        this.sourceText = sourceText;
        this.destText = destText;
        this.detection = detection;
        this.translation = translation;
    }

    @Override
    public boolean save() {

        if (sourceText.isEmpty() ||translation.isEmpty() || destText.isEmpty()) {
            return false;
        }

        if (super.save()) {

            MyBroadcastReceiver.sendBroadcast("new", "a", this);
            return true;
        }
        return false;
    }

    public void setLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude(){
        return latitude;
    }

    public double getLongitude(){
        return longitude;
    }

    public String getSourceText() {
        return sourceText;
    }

    public String getDestText() {
        return destText;
    }

    public String getDetection() {
        return detection;
    }

    public String getTranslation() {
        return translation;
    }

}
