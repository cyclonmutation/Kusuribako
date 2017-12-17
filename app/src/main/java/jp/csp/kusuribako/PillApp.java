package jp.csp.kusuribako;

import android.app.Application;

import io.realm.Realm;

public class PillApp extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        //Realm初期化
        Realm.init(this);
    }

}
