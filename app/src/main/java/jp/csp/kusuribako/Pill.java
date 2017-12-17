package jp.csp.kusuribako;

import android.media.Image;

import java.io.Serializable;
import java.util.ArrayList;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Pill extends RealmObject implements Serializable {

    private String mName;    //名前
    private String mImage;    //画像をString型の配列にしたもの
    private int mDosage;     //1回の服用量
    private String mUnit;    //1回の服用量の単位（袋、錠など）
    private String mFrequency;  //飲む頻度（毎日、特定の日、頓服など）
    private int mTimes;     //飲む回数（3回/日など）
    private ArrayList<String> mHourList;       //飲む時間（8時、20時など）　←Alarm対象時刻
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

    public String getImage() {
        return mImage;
    }
    public void setImageBytes(String img) {
        this.mImage = img;
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

    public ArrayList<String>  getmHour(){
        return mHourList;
    }
    public void setmHour(ArrayList<String>  hourList){
        this.mHourList = hourList;
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
//


//    public Pill(String name, String img, int dosage, String unit, String frequency, int times, ArrayList<String> hourlist) {
//        mName = name;
//        mImage = img;
//        mDosage = dosage;
//        mUnit = unit;
//        mFrequency = frequency;
//        mTimes = times;
//        mHourList = hourlist;
////        mLeftCheck = leftcheck;
////        mLeftVolume = leftVolume;
//    }

    
}
