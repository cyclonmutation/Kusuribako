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
import android.widget.ListView;
import android.text.format.Time;

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
        //★RealmDBから「直近2回分」を取得

        //現在時刻がAMなら、朝をチェックして、アラームがクリアされていれば、昼と夜の分の表示。朝が未チェックなら朝と昼の表示。
        //以下、昼、夜、寝る前も同様
        //ユーザ設定で、朝昼夜寝る時間のalarm時刻を設定できるようにした場合は、そのアラームの中間地点になるよう設定したい。

//        Time time = new Time("Asia/Tokyo");
//        time.setToNow();

        //まず、当日の情報だけ全てrealmで取得する
        //毎日→全部取得
        //曜日取得
        //曜日とrealmの曜日が一致していたら取得
        //回数は全部取っておく
//
//
//        if(time.hour <= 12){
//            Log.d("test_kusuri", "12" + String.valueOf(time.hour));
//
//            //
//
//        } else if(time.hour <= 16) {
//            Log.d("test_kusuri", "16" + String.valueOf(time.hour));
//
//        } else if(time.hour <= 21) {
//            Log.d("test_kusuri", "21" + String.valueOf(time.hour));
//        } else {
//            Log.d("test_kusuri", "24" + String.valueOf(time.hour));
//
//        };

        RealmResults<Pill> taskRealmResults = mRealm.where(Pill.class).findAll();

        //上記結果をPillListとしてmPillAdapterにset
        mPillAdapter.setPillList(mRealm.copyFromRealm(taskRealmResults));
        //TaskのListView用のAdapterに渡す
        mListView.setAdapter(mPillAdapter);
        //表示を更新するために、Adapterにdataが変更されたことを通知
        mPillAdapter.notifyDataSetChanged();
    }

}
