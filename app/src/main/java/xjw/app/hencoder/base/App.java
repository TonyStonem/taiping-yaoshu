package xjw.app.hencoder.base;

import android.app.Application;

/**
 * Created by xjw on 2017/8/15.
 */

public class App extends Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }

}
