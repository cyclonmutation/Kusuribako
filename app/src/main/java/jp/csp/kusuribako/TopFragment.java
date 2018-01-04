package jp.csp.kusuribako;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.text.format.Time;

import java.util.ArrayList;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class TopFragment extends Fragment {
    public final static String EXTRA_PILL = "jp.csp.kusuribako.PILL";

    //Realmクラスを保持する
    private Realm mRealm;
    //RealmDB更新時に呼ばれるListener
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };

    private ListView mListView;
    private PillAdapter mPillAdapter;
    private StringBuilder mFrequencyStr, mTimesStr;

    public TopFragment() {
        // Required empty public constructor
    }

    public static TopFragment newInstance(String param1, String param2) {
        TopFragment fragment = new TopFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top, container, false);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PillInputActivity.class);
                startActivity(intent);
            }
        });


        //Realm objectを取得し、Listenerを設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        //ListViewの設定
        mPillAdapter = new PillAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.pillListView);

        //ListViewをタップした時の処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //入力、編集画面に遷移
                Pill pill = (Pill) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), PillInputActivity.class);
                intent.putExtra(EXTRA_PILL, pill.getId());
                startActivity(intent);
            }
        });

        //ListViewを長押しした時の処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //taskを削除する
                final Pill pill = (Pill) parent.getAdapter().getItem(position);

                //Dialog表示
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("削除");
                builder.setMessage(pill.getName() + "を削除しますか？");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //★realmには残して、アラームを消し、次回以降登録時にsuggestできるとベターだけど一旦は削除する
                        //Alarm要否をflagとして定義し、realmに登録しておくと良い？
                        RealmResults<Pill> results = mRealm.where(Pill.class).equalTo("id", pill.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        //Alarmも削除する
//                        Intent resultIntent = new Intent(getApplicationContext(), TaskAlarmReceiver.class);
//                        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(
//                                MainActivity.this,
//                                task.getId(),
//                                resultIntent,
//                                PendingIntent.FLAG_UPDATE_CURRENT
//                        );
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//                        alarmManager.cancel(resultPendingIntent);

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL",null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();

        return view;
    }


    //再描画する
    private void reloadListView() {
//        Time time = new Time("Asia/Tokyo");
//        time.setToNow();

        //今日の曜日
        Calendar cal = Calendar.getInstance();
        //月曜を週初めに設定する
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        int hour = cal.get(Calendar.HOUR_OF_DAY);   //現在の時
        int week = cal.get(Calendar.DAY_OF_WEEK) - 1;  //1日曜〜7土曜
//        String[] week_name = {"日曜日", "月曜日", "火曜日", "水曜日", "木曜日", "金曜日", "土曜日"};
//        Log.d("test_kusuri_week[]", week_name[week]);

        //全てをrealmで取得する
//        RealmResults<Pill> pillRealmResults = mRealm.where(Pill.class).findAll();

        byte[] weekbyte = new byte[7];
        for(int w=1; w < weekbyte.length; w++){
            if(week == w){  //今日が日曜なら
                weekbyte[7] = 1;
            }
        }

        //条件指定して取得の場合
        RealmResults<Pill> pillRealmResults = mRealm.where(Pill.class)
                //毎日→全部取得
                .equalTo("mFrequency", 1)

                //曜日とrealmの曜日が一致していたら取得したい★
//                .or()
//                .beginGroup()
//                    .equalTo("mFequency", "2")
//                    .equalTo("mWeek", weekbyte)
//                .endGroup()
                .findAll();

//        for(int i=0; i < pillRealmResults.size(); i++){
//            Pill pill = pillRealmResults.get(i);
//
//            if(pill.getFrequency() == 1){
//                //毎日は全てそのままcontinue
//
//            } else if(pill.getFrequency() == 2){
//                //特定の週で、曜日が一致しなかったら削除
//
//                for(int j=0; j < pill.getWeek().length; j++){
//                    byte[] weekByte = pill.getWeek();   //月曜始まり
//                    if(weekByte[j] == week){
//                        //あれば削除しない
//                    } else {
//                        //本日以外なので削除する
////                        pillRealmResults.deleteFromRealm(i);
//                    }
//                }
//            } else {
//                //その他（頓服）は全て削除
////                pillRealmResults.deleteFromRealm(i);
//            }
//        }

//        //回数を取得し、現在時刻に合わせて表示するものを変える
//        if(hour <= 12){
//            Log.d("test_kusuri", "12" + String.valueOf(hour));
//        //0〜12時：朝、昼の薬を取得
//        RealmResults<Pill> pillRealmResults = mRealm.where(Pill.class)
//                .equalTo("mTimes", "1")
//                .or()
//                .equalTo("mTimes", "1")
//                .findAll();
//        } else if(hour <= 16) {
//            Log.d("test_kusuri", "16" + String.valueOf(hour));
//        //12〜16時：昼、夜の薬を取得
//
//        } else if(hour <= 21) {
//            Log.d("test_kusuri", "21" + String.valueOf(hour));
//        //16〜21時：夜、寝る前の薬を取得
//
//
//        } else {
//            Log.d("test_kusuri", "24" + String.valueOf(hour));
//        //21〜24時：寝る前、朝の薬を取得
//
//        };

        //上記結果をPillListとしてmPillAdapterにset
        mPillAdapter.setPillList(mRealm.copyFromRealm(pillRealmResults));
        //TaskのListView用のAdapterに渡す
        mListView.setAdapter(mPillAdapter);
        //表示を更新するために、Adapterにdataが変更されたことを通知
        mPillAdapter.notifyDataSetChanged();
    }

}
