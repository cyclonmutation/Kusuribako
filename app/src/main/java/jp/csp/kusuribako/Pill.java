package jp.csp.kusuribako;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Pill extends RealmObject implements Serializable {

    private String mName;    //名前
    private byte[] mBitmapArray;    //画像をbyte型の配列にしたもの
    private int mDosage;     //1回の服用量
    private String mUnit;    //1回の服用量の単位（袋、錠など）
    private String mFrequency;  //飲む頻度（毎日、特定の日、頓服など）
    private int mTimes;     //飲む回数（3回/日など）
    private String mHour;       //飲む時間（8時、20時など）　←Alarm対象時刻
//    private boolean mLeftCheck;     //残量チェック要否
//    private int mLeftVolume;        //残数（チェックが必要な場合のみ）

    //idをprimary keyとして設定
    @PrimaryKey
    private int id;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return mName;
    }
    public void setName(String name){
        this.mName = name;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }
    public void setImageBytes(byte[] bytes) {
        this.mBitmapArray = bytes;
    }

    public int getDosage(){
        return mDosage;
    }
    public void setDosage(int dosage){
        this.mDosage = dosage;
    }

    public String getUnit(){
        return mUnit;
    }
    public void setUnit(String unit){
        this.mUnit = unit;
    }

    public String getFrequency(){
        return mFrequency;
    }
    public void setFrequency(String frequency){
        this.mFrequency = frequency;
    }

    public int getmTimes(){
        return mTimes;
    }
    public void setmTimes(int mTimes){
        this.mTimes = mTimes;
    }

    public String getmHour(){
        return mHour;
    }
    public void setmHour(String mHour){
        this.mHour = mHour;
    }

//    public boolean getmLeftCheck(){
//        return mLeftCheck;
//    }
//    public void setmLeftCheck(boolean mLeftCheck){
//        this.mLeftCheck = mLeftCheck;
//    }
//
//    public int getmLeftVolume(){
//        return mLeftVolume;
//    }
//    public void setmLeftVolume(int mLeftVolume){
//        this.mLeftVolume = mLeftVolume;
//    }

//    public Pill(String name, byte[] bytes, int dosage, String unit, String frequency, int times, String hour, boolean leftcheck, int leftVolume) {
//        mName = name;
//        mBitmapArray = bytes;
//        mDosage = dosage;
//        mUnit = unit;
//        mFrequency = frequency;
//        mTimes = times;
//        mHour = hour;
//        mLeftCheck = leftcheck;
//        mLeftVolume = leftVolume;
//    }
}
