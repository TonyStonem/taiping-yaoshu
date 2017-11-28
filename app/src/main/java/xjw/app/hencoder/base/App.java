package xjw.app.hencoder.base;

import android.app.Application;

import xjw.app.hencoder.xble.XBleUtils;


/**
 * Created by xjw on 2017/8/15.
 */

public class App extends Application {

    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        XBleUtils.get().init(this);
    }

    public static Application getInstance() {
        return instance;
    }

}
