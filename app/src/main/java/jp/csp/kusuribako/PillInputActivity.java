package jp.csp.kusuribako;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

public class PillInputActivity extends AppCompatActivity {


    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button mSendButton;
    private EditText mNameEdit;
    private Pill mPill;

//    private View.OnClickListener mOnDateClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            DatePickerDialog datePickerDialog = new DatePickerDialog(PillInputActivity.this,
//                    new DatePickerDialog.OnDateSetListener() {
//                        @Override
//                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//                            mYear = year;
//                            mMonth = monthOfYear;
//                            mDay = dayOfMonth;
//                            String dateString = mYear + "/" + String.format("%02d", (mMonth + 1)) + "/" + String.format("%02d", mDay);
//                            mDateButton.setText(dateString);
//                        }
//                    }, mYear, mMonth, mDay);
//            datePickerDialog.show();
//        }
//    };
//
//    private View.OnClickListener mOnTimeClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            TimePickerDialog timePickerDialog = new TimePickerDialog(PillInputActivity.this,
//                    new TimePickerDialog.OnTimeSetListener() {
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                            mHour = hourOfDay;
//                            mMinute = minute;
//                            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
//                            mTimeButton.setText(timeString);
//                        }
//                    }, mHour, mMinute, false);
//            timePickerDialog.show();
//        }
//    };

    //realmに保存、更新後、finish()してInputActivityを閉じて前画面（MainActivity）に戻る
    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v){
            addPill();
            finish();
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_input);

        //UI部品の設定
        mSendButton = (Button)findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(mOnDoneClickListener);
//        mTimeButton = (Button)findViewById(R.id.times_button);
//        mTimeButton.setOnClickListener(mOnTimeClickListener);
//        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);
        mNameEdit = (EditText)findViewById(R.id.nameEditText);
//        mCategoryEdit = (EditText)findViewById(R.id.category_edit_text);
//        mContentEdit = (EditText)findViewById(R.id.content_edit_text);

        //EXTRA_PILLからPillのidを取得し、idからTaskのinstance取得
        Intent intent = getIntent();
        int taskId = intent.getIntExtra(PillListFragment.EXTRA_PILL, -1);
        Realm realm = Realm.getDefaultInstance();
        mPill = realm.where(Pill.class).equalTo("id", taskId).findFirst();
        realm.close();

        if (mPill == null) {
            //新規作成の場合
//            Calendar calendar = Calendar.getInstance();
//            mYear = calendar.get(Calendar.YEAR);
//            mMonth = calendar.get(Calendar.MONTH);
//            mDay = calendar.get(Calendar.DAY_OF_MONTH);
//            mHour = calendar.get(Calendar.HOUR_OF_DAY);
//            mMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合
            mNameEdit.setText(mPill.getName());
//            mCategoryEdit.setText(mPill.getCategory());
//            mContentEdit.setText(mPill.getContents());

//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(mPill.getDate());
//            mYear = calendar.get(Calendar.YEAR);
//            mMonth = calendar.get(Calendar.MONTH);
//            mDay = calendar.get(Calendar.DAY_OF_MONTH);
//            mHour = calendar.get(Calendar.HOUR_OF_DAY);
//            mMinute = calendar.get(Calendar.MINUTE);

//            String dateString = mYear + "/" + String.format("%02d",(mMonth + 1)) + "/" + String.format("%02d", mDay);
//            String timeString = String.format("%02d", mHour) + ":" + String.format("%02d", mMinute);
//            mDateButton.setText(dateString);
//            mTimeButton.setText(timeString);
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
//        String category = mCategoryEdit.getText().toString();
//        String content = mContentEdit.getText().toString();

        mPill.setName(name);
//        mPill.setCategory(category);
//        mPill.setContents(content);

//        GregorianCalendar calendar = new GregorianCalendar(mYear,mMonth,mDay,mHour,mMinute);
//        Date date = calendar.getTime();
//        mPill.setDate(date);

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



}
