package jp.csp.kusuribako;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;

public class PillInputActivity extends AppCompatActivity {

    //permission許可Dialog表示用パラメータ
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    //複数パラメータから戻る場合の識別用パラメータ
    private static final int CHOOSER_REQUEST_CODE = 100;

    private Pill mPill;
    private int mFrequency;  //飲む頻度（1毎日、2特定の曜日、3頓服）
    private byte[] mWeek = new byte[7];       //特定の曜日（月火水木金土日）
    private byte[] mTimes = new byte[4];;      //飲む回数（朝、昼、夜、寝る前）

    //UI用
    private EditText mNameEdit, mDosageEdit;
    private Spinner mSpinner;
    private ImageView mImageView;
    private Uri mPictureUri; // カメラで撮影した画像を保存するURI
    private TextView mWeekTextView, mTimesTextView;
    private CheckBox mEverydayCB, mWeekCB, mOnceCB,
            mMondayCB, mTuesdayCB, mWednesdayCB, mThursdayCB, mFridayCB, mSaturdayCB, mSundayCB,
            mMorningCB, mNoonCB, mNightCB, mBeforegtbCB;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_input);

        //UI準備
        setTitle("薬を追加する");

        //UI部品の設定
        mNameEdit = (EditText)findViewById(R.id.nameEditText);
        mDosageEdit = (EditText)findViewById(R.id.dosageEditText);

        //Spinnerの設定
        mSpinner = (Spinner) findViewById(R.id.unit_spinner);
        String mSpinnerItems[] = getResources().getStringArray(R.array.unit);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mSpinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setOnClickListener(mOnImageClickListener);

        //Checkbox
        mEverydayCB = (CheckBox)findViewById(R.id.checkbox_everyday);
        mWeekCB = (CheckBox)findViewById(R.id.checkbox_week);
        mOnceCB = (CheckBox)findViewById(R.id.checkbox_once);

        mWeekTextView = (TextView) findViewById(R.id.weekTextView);
        mMondayCB = (CheckBox)findViewById(R.id.checkbox_monday);
        mTuesdayCB = (CheckBox)findViewById(R.id.checkbox_tuesday);
        mWednesdayCB = (CheckBox)findViewById(R.id.checkbox_wednesday);
        mThursdayCB = (CheckBox)findViewById(R.id.checkbox_thursday);
        mFridayCB = (CheckBox)findViewById(R.id.checkbox_friday);
        mSaturdayCB = (CheckBox)findViewById(R.id.checkbox_saturday);
        mSundayCB = (CheckBox)findViewById(R.id.checkbox_sunday);

        mTimesTextView = (TextView) findViewById(R.id.timesTextView);
        mMorningCB = (CheckBox)findViewById(R.id.checkbox_morning);
        mNoonCB = (CheckBox)findViewById(R.id.checkbox_noon);
        mNightCB = (CheckBox)findViewById(R.id.checkbox_night);
        mBeforegtbCB = (CheckBox)findViewById(R.id.checkbox_beforegtb);

        mSendButton = (Button)findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(mOnDoneClickListener);


        // CheckboxがClickされた時に呼び出されるcallbackListenerを登録
        mEverydayCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEverydayCB.isChecked()) {
                    mWeekCB.setEnabled(false);
                    mOnceCB.setEnabled(false);

                    mTimesTextView.setVisibility(View.VISIBLE);
                    mMorningCB.setVisibility(View.VISIBLE);
                    mNoonCB.setVisibility(View.VISIBLE);
                    mNightCB.setVisibility(View.VISIBLE);
                    mBeforegtbCB.setVisibility(View.VISIBLE);
                }
                else {
                    mWeekCB.setEnabled(true);
                    mOnceCB.setEnabled(true);

                    mTimesTextView.setVisibility(View.GONE);
                    mMorningCB.setVisibility(View.GONE);
                    mNoonCB.setVisibility(View.GONE);
                    mNightCB.setVisibility(View.GONE);
                    mBeforegtbCB.setVisibility(View.GONE);
                }
            }
        });

        mWeekCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWeekCB.isChecked()) {
                    mEverydayCB.setEnabled(false);
                    mOnceCB.setEnabled(false);

                    mWeekTextView.setVisibility(View.VISIBLE);
                    mMondayCB.setVisibility(View.VISIBLE);
                    mTuesdayCB.setVisibility(View.VISIBLE);
                    mWednesdayCB.setVisibility(View.VISIBLE);
                    mThursdayCB.setVisibility(View.VISIBLE);
                    mFridayCB.setVisibility(View.VISIBLE);
                    mSaturdayCB.setVisibility(View.VISIBLE);
                    mSundayCB.setVisibility(View.VISIBLE);

                    mTimesTextView.setVisibility(View.VISIBLE);
                    mMorningCB.setVisibility(View.VISIBLE);
                    mNoonCB.setVisibility(View.VISIBLE);
                    mNightCB.setVisibility(View.VISIBLE);
                    mBeforegtbCB.setVisibility(View.VISIBLE);
                }
                else {
                    mEverydayCB.setEnabled(true);
                    mOnceCB.setEnabled(true);

                    mWeekTextView.setVisibility(View.GONE);
                    mMondayCB.setVisibility(View.GONE);
                    mTuesdayCB.setVisibility(View.GONE);
                    mWednesdayCB.setVisibility(View.GONE);
                    mThursdayCB.setVisibility(View.GONE);
                    mFridayCB.setVisibility(View.GONE);
                    mSaturdayCB.setVisibility(View.GONE);
                    mSundayCB.setVisibility(View.GONE);

                    mTimesTextView.setVisibility(View.GONE);
                    mMorningCB.setVisibility(View.GONE);
                    mNoonCB.setVisibility(View.GONE);
                    mNightCB.setVisibility(View.GONE);
                    mBeforegtbCB.setVisibility(View.GONE);
                }
            }
        });

        mOnceCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnceCB.isChecked()) {
                    mEverydayCB.setEnabled(false);
                    mWeekCB.setEnabled(false);
                }
                else {
                    mEverydayCB.setEnabled(true);
                    mWeekCB.setEnabled(true);
                }
            }
        });

        //EXTRA_PILLからPillのidを取得し、idからPillのinstance取得
        Intent intent = getIntent();
        int pillId = intent.getIntExtra(PillListFragment.EXTRA_PILL, -1);
        Realm realm = Realm.getDefaultInstance();
        mPill = realm.where(Pill.class).equalTo("id", pillId).findFirst();
        realm.close();


        //選択されていた項目と一致するCheckboxにチェックを入れる
        //毎日なら曜日のcheckbox非表示、頓服なら曜日と回数のcheckbox非表示
        CheckBox[] weekArray = {mSundayCB, mMondayCB, mTuesdayCB, mWednesdayCB, mThursdayCB, mFridayCB, mSaturdayCB};
        CheckBox[] timesArray = {mMorningCB, mNoonCB, mNightCB, mBeforegtbCB};


        //新規登録の場合
        if (mPill == null) {
            //曜日を非表示にする
            mWeekTextView.setVisibility(View.GONE);
            for(int i=0; i < weekArray.length; i++){
                weekArray[i].setVisibility(View.GONE);
            }

            //回数を非表示にする
            mTimesTextView.setVisibility(View.GONE);
            for(int i=0; i < timesArray.length; i++){
                timesArray[i].setVisibility(View.GONE);
            }

        // 更新の場合、登録されていた薬を表示する
        } else {
            mNameEdit.setText(mPill.getName());
            mDosageEdit.setText(String.valueOf(mPill.getDosage()));

            //unitのSpinnerに今の値と一致するものを表示する
            //spinnerの数だけ文字列をfor文で回しつつ
            for(int i=0; i < mSpinnerItems.length; i++){
                if(mSpinnerItems[i].equals(mPill.getUnit())){
                    mSpinner.setSelection(i);
                }
            }

            mFrequency = mPill.getFrequency();
            if(mFrequency == 1){
                mEverydayCB.setChecked(true);
                mWeekCB.setEnabled(false);
                mOnceCB.setEnabled(false);

                //曜日を非表示にする
                mWeekTextView.setVisibility(View.GONE);
                for(int i=0; i < weekArray.length; i++){
                    weekArray[i].setVisibility(View.GONE);
                }

                //回数にcheck
                byte[] timesBytes = mPill.getTimes();
                for (int i=0; i < mPill.getTimes().length; i++){
                    if(timesBytes[i] == 1){
                        timesArray[i].setChecked(true);
                    }
                }

            } else if(mFrequency == 2){
                mWeekCB.setChecked(true);
                mEverydayCB.setEnabled(false);
                mOnceCB.setEnabled(false);

                //曜日にcheck
                byte[] weekBytes = mPill.getWeek();
                for (int i=0; i < mPill.getWeek().length; i++){
                    if(weekBytes[i] == 1){
                        weekArray[i].setChecked(true);
                    }
                }

                //回数にcheck
                byte[] timesBytes = mPill.getTimes();
                for (int i=0; i < mPill.getTimes().length; i++){
                    if(timesBytes[i] == 1){
                        timesArray[i].setChecked(true);
                    }
                }

            } else {
                mOnceCB.setChecked(true);
                mEverydayCB.setEnabled(false);
                mWeekCB.setEnabled(false);

                //曜日を非表示にする
                mWeekTextView.setVisibility(View.GONE);
                for(int i=0; i < weekArray.length; i++){
                    weekArray[i].setVisibility(View.GONE);
                }

                //回数を非表示にする
                mTimesTextView.setVisibility(View.GONE);
                for(int i=0; i < timesArray.length; i++){
                    timesArray[i].setVisibility(View.GONE);
                }
            }

            //画像が登録されていれば登録画像を表示する
            Bitmap bmp = null;
            if (mPill.getImageBytes() != null) {
                bmp = BitmapFactory.decodeByteArray(mPill.getImageBytes(), 0, mPill.getImageBytes().length);
            }
            //BitmapをImageViewに設定
            mImageView.setImageBitmap(bmp);
        }
    }

    private void addPill(){
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        if (mPill == null) {
            //新規作成の場合
            mPill = new Pill();
            RealmResults<Pill> taskRealmResults = realm.where(Pill.class).findAll();

            int identifier;
            if (taskRealmResults.max("id") != null) {
                identifier = taskRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mPill.setId(identifier);
        }

        String name = mNameEdit.getText().toString();

        int dosage = Integer.parseInt(mDosageEdit.getText().toString());
        String unit = (String)mSpinner.getSelectedItem();

        CheckBox[] weekArray = {mSundayCB, mMondayCB, mTuesdayCB, mWednesdayCB, mThursdayCB, mFridayCB, mSaturdayCB};
        CheckBox[] timesArray = {mMorningCB, mNoonCB, mNightCB, mBeforegtbCB};

        //Checkboxの選択状態に応じてFrequency、Week、Timesに値をセット。未設定の場合は0を入れる。
        if(mEverydayCB.isChecked()){
            mFrequency = 1;
            Arrays.fill(mWeek, (byte) 0);
            setCheckboxTimes(timesArray);

        } else if(mWeekCB.isChecked()){
            mFrequency = 2;
            setCheckboxWeek(weekArray);
            setCheckboxTimes(timesArray);

        } else {
            mFrequency = 3;
            Arrays.fill(mWeek, (byte) 0);
            Arrays.fill(mTimes, (byte) 0);
        }

        mPill.setName(name);
        mPill.setDosage(dosage);
        mPill.setUnit(unit);
        mPill.setFrequency(mFrequency);
        mPill.setWeek(mWeek);
        mPill.setTimes(mTimes);

        //添付画像を取得
        BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();

        //添付画像が設定されていれば画像を取得してBASE64Encode
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            String bitmapString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

            //bitmapをbyte[]に変換して薬に追加
            byte[] bytes = baos.toByteArray();
            mPill.setImageBytes(bytes);
        }

        realm.copyToRealmOrUpdate(mPill);
        realm.commitTransaction();

        realm.close();

        //Alarmを設定する

        //TaskAlarmReceiverを起動するintent
//        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
//
//        resultIntent.putExtra(MainActivity.EXTRA_TASK, mPill.getId());
//        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
//                this,
//                mPill.getId(),
//                resultIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT   //既存のPendingIntentがあれば、それはそのままでextraのデータだけ置き換える
//        );
//
//        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//        //RTC_WAKEUP：UTC時間を指定。画面スリープ中でもアラームを発行
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), resultPendingIntent);

    }

    //特定の曜日のCheckboxにチェックが入っていれば、mWeek[]に1を入れる
    private void setCheckboxWeek(CheckBox[] checkboxArray) {
        for(int i = 0; i < checkboxArray.length; i++){
            if(checkboxArray[i].isChecked()){
                mWeek[i] = 1;
            }else{
                mWeek[i] = 0;
            }
        }
    }

    //飲む回数（朝昼夜寝る前）のCheckboxにチェックが入っていれば、mTimes[]に1を入れる
    private void setCheckboxTimes(CheckBox[] checkboxArray) {
        for(int i = 0; i < checkboxArray.length; i++){
            if(checkboxArray[i].isChecked()){
                mTimes[i] = 1;
            }else{
                mTimes[i] = 0;
            }
        }
    }


    //SendButton押下後、realmに保存、更新後、finish()してInputActivityを閉じて前画面（MainActivity）に戻る
    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){

//            if(mNameEdit.getText().toString().length() == 0) {
//                //名前が未入力の場合はエラー表示
//                Snackbar.make(v, "名前を入力してください", Snackbar.LENGTH_LONG).show();
//                return;
//            }
//
//            if(mDosageEdit.getText().toString().length() == 0) {
//                //量が未入力の場合はエラー表示
//                Snackbar.make(v, "1回あたりの服用量を入力してください", Snackbar.LENGTH_LONG).show();
//                return;
//            }
//
//            CheckBox[] frequencyArray = {mEverydayCB, mWeekCB, mOnceCB};
//            CheckBox[] weekArray = {mMondayCB, mTuesdayCB, mWednesdayCB, mThursdayCB, mFridayCB, mSaturdayCB, mSundayCB};
//            CheckBox[] timesArray = {mMorningCB, mNoonCB, mNightCB, mBeforegtbCB};
//
//            for(int i=0; i<frequencyArray.length; i++){
//                if(frequencyArray[i].isChecked()){
//                    //頻度にチェックが入っていれば何もしない
//                } else {
//                    //頻度いずれかにチェックが入っていなければエラー
//                    Snackbar.make(v, "いつ飲むかを選択してください", Snackbar.LENGTH_LONG).show();
//                    return;
//                }
//            }
//
//            //毎日にチェックが入っている場合
//            if(frequencyArray[0].isChecked()){
//                //回数にチェックが入っていなければエラー
//                for(int i=0; i<timesArray.length; i++){
//                    if(timesArray[i].isChecked()){
//                    } else {
//                        //回数いずれかにチェックが入っていなければエラー
//                        Snackbar.make(v, "1日に飲む回数を選択してください", Snackbar.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//            }
//            //特定の曜日にチェックが入っている場合
//            if(frequencyArray[1].isChecked()){
//                //回数にチェックが入っていなければエラー
//                for(int i=0; i<weekArray.length; i++){
//                    if(weekArray[i].isChecked()){
//                    } else {
//                        //曜日いずれかにチェックが入っていなければエラー
//                        Snackbar.make(v, "曜日を選択してください", Snackbar.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//                //回数にチェックが入っていなければエラー
//                for(int i=0; i<timesArray.length; i++){
//                    if(timesArray[i].isChecked()){
//                    } else {
//                        //回数いずれかにチェックが入っていなければエラー
//                        Snackbar.make(v, "1日に飲む回数を選択してください", Snackbar.LENGTH_LONG).show();
//                        return;
//                    }
//                }
//            }

            addPill();
            finish();
        }
    };

    //onClick：ImageViewがタップされた時の処理
    //ImageViewをタップしたときは必要であれば許可を求めるダイアログを表示。
    private View.OnClickListener mOnImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            //permissionの許可状態を確認
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    //許可されている
                    showChooser();
                } else {
                    //許可されていないので許可ダイアログを表示
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
                    return;
                }
            } else {
                showChooser();
            }
        }
    };


    //onRequestPermissionsResult：許可を求めるダイアログからの結果を受け取る
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults){
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Userが許可した時
                    showChooser();
                }
                return;
            }
        }
    }

    //showChooser：Intent連携の選択ダイアログを表示
    private void showChooser() {
        //Galaryから選択するIntent
        Intent gallaryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        gallaryIntent.setType("image/*");
        gallaryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        //cameraで撮影するIntent
        String filename = System.currentTimeMillis() + ".jpg";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        mPictureUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPictureUri);

        //galary選択Intentを与えてcreateChooser methodを呼ぶ
        Intent chooserIntent = Intent.createChooser(gallaryIntent, "画像を取得");

        // EXTRA_INITIAL_INTENTS にカメラ撮影のIntentを追加
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});

        startActivityForResult(chooserIntent, CHOOSER_REQUEST_CODE);
    }

    //onActivityResult：Intent連携で取得した画像をリサイズしてImageViewに設定
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CHOOSER_REQUEST_CODE) {

            //requestCode：int型で指定
            //resultCode：int型

            if (resultCode != RESULT_OK) {
                if (mPictureUri != null) {
                    getContentResolver().delete(mPictureUri, null, null);
                    mPictureUri = null;
                }
                return; //methodを抜ける
            }

            //画像を取得
            Uri uri = (data == null || data.getData() == null) ? mPictureUri : data.getData();

            //URIからBitmapを取得
            Bitmap image;
            try{
                ContentResolver contentResolver = getContentResolver();
                InputStream inputStream = contentResolver.openInputStream(uri);
                image = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            } catch (Exception e){
                return;
            }

            //取得したbitmapの長辺を500pxにresize
            int imageWidth = image.getWidth();
            int imageHeight = image.getHeight();
            float scale = Math.min((float) 500 / imageWidth, (float) 500 /imageHeight);

            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            Bitmap resizedImage = Bitmap.createBitmap(image, 0, 0, imageWidth, imageHeight, matrix, true);

            //BitmapをImageViewに設定
            mImageView.setImageBitmap(resizedImage);

            mPictureUri = null;
        }
    }
}
