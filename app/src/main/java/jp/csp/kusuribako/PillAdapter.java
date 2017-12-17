package jp.csp.kusuribako;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PillAdapter extends BaseAdapter{

    //他のxmlリソースのViewを取り扱う
    private LayoutInflater mLayoutInflater = null;

    //Pillを保持するlist
    private List<Pill> mPillList;

    public PillAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    public void setPillList(List<Pill> pillList) {
        mPillList = pillList;
    }

    //mPillListの数を返す
    @Override
    public int getCount() {
        return mPillList.size();
    }

    //mPillListのdataを返す
    @Override
    public Object getItem(int position) {
        return mPillList.get(position);
    }

    //mPillListのIDを返す
    @Override
    public long getItemId(int position) {
        return mPillList.get(position).getId();
    }

    //Viewを返す
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //convertViewがnullの場合、simple_list_item_2（タイトルとサブタイがあるセル）からViewを取得する
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }

//★画像を表示したい
//        ImageView imageView = (ImageView) convertView.findViewById(android.R.id.imageView);
//        TextView textView = (TextView) convertView.findViewById(android.R.id.content);

        TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);

        //Pill classから情報取得
        textView.setText(mPillList.get(position).getName());

//★次に飲むタイミングの日時を取得する
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH;mm", Locale.JAPANESE);
//        Date date = mPillList.get(position).getDate();
//        textView2.setText(simpleDateFormat.format(date));

        return convertView;
    }

}
