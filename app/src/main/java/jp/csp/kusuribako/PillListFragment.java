package jp.csp.kusuribako;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class PillListFragment extends Fragment {
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


//    private Spinner mSpinner;
//    private ArrayList<String> mCategoryList = new ArrayList<>();


    public PillListFragment() {
    }

    public static PillListFragment newInstance() {
        PillListFragment fragment = new PillListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pilllist_list, container, false);

//        onCreateで入れてた処理を全部入れる
        //onClickListener
        //task管理appのadapter、
        //ListViewを作る
        //view.findViewById(R.layout.)

        //Realm objectを取得し、Listenerを設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        //ListViewの設定
        mPillAdapter = new PillAdapter(getActivity());
        mListView = (ListView) view.findViewById(R.id.pillListView);


        //        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCategoryList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(adapter);
//
//        //categoryのプルダウン選択時に、選択したcategoryに絞ってtask一覧を再描画する
//        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                Spinner spinner = (Spinner) parent;
//                // 選択されたitemを取得
//                String item = (String) spinner.getSelectedItem();
//
//                if(item.equals("all")){
//                    reloadListView();
//                } else {
//                    //選択されたitemに合致するcategoryを絞り込み
//                    RealmResults<Task> categoryTask = mRealm.where(Task.class).equalTo("category", item).findAll();
//
//                    //上記結果をTaskListとしてmTaskAdapterにset
//                    mTaskAdapter.setTaskList(mRealm.copyFromRealm(categoryTask));
//                    //TaskのListView用のAdapterに渡す
//                    mListView.setAdapter(mTaskAdapter);
//                    //表示を更新するために、Adapterにdataが変更されたことを通知
//                    mTaskAdapter.notifyDataSetChanged();
//                }
//            }
//            @Override
//            public void onNothingSelected(AdapterView<?> arg0) {
//                //何も選択されなかった場合、全task表示
//                reloadListView();
//            }
//        });
//

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

                        //★realmには残して、アラームを消し、次回以降登録時にsuggestできるとベター
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
        //RealmDBから「全dataを取得して新しい日時順に並べた結果」を取得
        RealmResults<Pill> taskRealmResults = mRealm.where(Pill.class).findAll();
        //上記結果をPillListとしてmPillAdapterにset
        mPillAdapter.setPillList(mRealm.copyFromRealm(taskRealmResults));
        //TaskのListView用のAdapterに渡す
        mListView.setAdapter(mPillAdapter);
        //表示を更新するために、Adapterにdataが変更されたことを通知
        mPillAdapter.notifyDataSetChanged();
    }


//★fragmentもonDestroy()を入れるべき？　MainActivityに入れるべき？
//    @Override
//    protected void onDestroy(){ //App終了時、Realmを終了
//        super.onDestroy();
//        mRealm.close();
//    }
//
//    @Override
//    protected void onResume(){
//        super.onResume();
//
//        //categoryを取得する
//        RealmResults<Task> categoryResult = mRealm.where(Task.class).distinct("category");
//        categoryResult = categoryResult.sort("category");
//
//        mCategoryList.clear();
//        mCategoryList.add("all");
//        for(Task task:categoryResult){
//            mCategoryList.add(task.getCategory());
//        }
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mCategoryList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        mSpinner.setAdapter(adapter);
//    }


}
