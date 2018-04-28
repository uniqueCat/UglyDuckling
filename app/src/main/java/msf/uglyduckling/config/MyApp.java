package msf.uglyduckling.config;

import android.app.Application;

import io.realm.Realm;

/**
 * Created by Administrator on 2018/4/18.
 */

public class MyApp extends Application {

    private static MyApp install;

    public static MyApp getInstall() {
        return install;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        install = this;
        Realm.init(this);
    }
}
