package jp.csp.kusuribako;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.TimedText;
import android.util.Log;
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

    Context context; //追加

    public PillAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context; //追加
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
        //convertViewがnullの場合、list_pills（薬一覧表示用レイアウト）
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_pills, parent, false);
        }

        TextView nameText = (TextView) convertView.findViewById(R.id.nameTextView);
        nameText.setText(mPillList.get(position).getName());

        TextView dosageText = (TextView) convertView.findViewById(R.id.dosageTextView);
        dosageText.setText(mPillList.get(position).getDosage() + mPillList.get(position).getUnit());

        TextView frequencyText = (TextView) convertView.findViewById(R.id.frequencyTextView);
        TextView timesText = (TextView) convertView.findViewById(R.id.timesTextView);

        //頻度が毎日の場合、timeStringBuilderに朝昼夜寝る前を格納
        if(mPillList.get(position).getFrequency() == 1){
            StringBuilder timeSB = new StringBuilder();
            byte[] timeByte = mPillList.get(position).getTimes();

            if(timeByte[0] == 1){
                timeSB.append(this.context.getString(R.string.morning));
                timeSB.append(" ");
            }
            if(timeByte[1] == 1){
                timeSB.append(this.context.getString(R.string.noon));
                timeSB.append(" ");
            }
            if(timeByte[2] == 1){
                timeSB.append(this.context.getString(R.string.night));
                timeSB.append(" ");
            }
            if(timeByte[3] == 1){
                timeSB.append(this.context.getString(R.string.beforegdb));
                timeSB.append(" ");
            }

            //最後に空白が入っていたら消す
//            if(timeSB.lastIndexOf(" ") == 1){
//                timeSB.deleteCharAt(timeSB.length());
//            }


            frequencyText.setText(this.context.getString(R.string.everyday));
            timesText.setText(timeSB.toString());

        } else if(mPillList.get(position).getFrequency() == 2){
            StringBuilder stb = new StringBuilder();
            byte[] data = mPillList.get(position).getWeek();
            if(data[0] == 1){
                stb.append(this.context.getString(R.string.sunday));
                stb.append(" ");
            }
            if(data[1] == 1){
                stb.append(this.context.getString(R.string.monday));
                stb.append(" ");
            }
            if(data[2] == 1){
                stb.append(this.context.getString(R.string.tuesday));
                stb.append(" ");
            }
            if(data[3] == 1){
                stb.append(this.context.getString(R.string.wednesday));
                stb.append(" ");
            }
            if(data[4] == 1){
                stb.append(this.context.getString(R.string.thursday));
                stb.append(" ");
            }
            if(data[5] == 1){
                stb.append(this.context.getString(R.string.friday));
                stb.append(" ");
            }
            if(data[6] == 1){
                stb.append(this.context.getString(R.string.saturday));
                stb.append(" ");
            }

            //最後に空白が入っていたら消す
//            if(stb.lastIndexOf(" ") == 1){
//                stb.deleteCharAt(stb.length());
//            }

            StringBuilder timeSB2 = new StringBuilder();
            byte[] timeByte2 = mPillList.get(position).getTimes();
            if(timeByte2[0] == 1){
                timeSB2.append(this.context.getString(R.string.morning));
                timeSB2.append(" ");
            }
            if(timeByte2[1] == 1){
                timeSB2.append(this.context.getString(R.string.noon));
                timeSB2.append(" ");
            }
            if(timeByte2[2] == 1){
                timeSB2.append(this.context.getString(R.string.night));
                timeSB2.append(" ");
            }
            if(timeByte2[3] == 1){
                timeSB2.append(this.context.getString(R.string.beforegdb));
                timeSB2.append(" ");
            }

//            //最後に空白が入っていたら消す
//            if(timeSB2.lastIndexOf(" ") == 1){
//                timeSB2.deleteCharAt(stb.length());
//            }

            frequencyText.setText(stb.toString());
            timesText.setText(timeSB2.toString());

        } else {
            frequencyText.setText(R.string.once);
            timesText.setVisibility(View.GONE);
        }

        byte[] bytes = mPillList.get(position).getImageBytes();
        if (bytes!=null && bytes.length != 0) {     //画像がある場合
            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length).copy(Bitmap.Config.ARGB_8888, true);
            ImageView imageView =(ImageView) convertView.findViewById(R.id.imageView);
            imageView.setImageBitmap(image);
        } else {
            //ここに画像が登録されない場合の処理を記載する
        }


//★次に飲むタイミングの日時を取得する
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH;mm", Locale.JAPANESE);
//        Date date = mPillList.get(position).getDate();
//        textView2.setText(simpleDateFormat.format(date));

        return convertView;
    }

}

