package jp.csp.kusuribako;

import android.graphics.Bitmap;
import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


public class Pill extends RealmObject implements Serializable {

    private String mName;    //名前
    private byte[] mBitmapArray;    //画像をbyte型の配列にしたもの
    private int mDosage;     //1回の服用量
    private String mUnit;    //1回の服用量の単位（袋、錠など）


//    private ArrayList<String> mHourList;       //飲む時間（8時、20時など）
//    private boolean mLeftCheck;     //残量チェック要否
//    private int mLeftVolume;        //残数（チェックが必要な場合のみ）

    //idをprimary keyとして設定
    @PrimaryKey
    private int id;
    private int mFrequency;  //飲む頻度（1、毎日、2、特定の日、3頓服）
    private byte[] mWeek;      //特定の日の場合の曜日
    private byte[] mTimes;     //飲む回数（1、朝、2、昼、3、夜、8寝る前）


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
    public void setImageBytes(byte[] img) {
        this.mBitmapArray = img;
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

    public int getFrequency() {
        return mFrequency;
    }
    public void setFrequency(int frequency) {
        this.mFrequency = frequency;
    }

    public byte[] getWeek() {
        return mWeek;
    }
    public void setWeek(byte[] week) {
        this.mWeek = week;
    }

    public byte[] getTimes() {
        return mTimes;
    }
    public void setTimes(byte[] times) {
        this.mTimes = times;
    }

}
