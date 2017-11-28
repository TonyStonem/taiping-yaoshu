package xjw.app.hencoder.base;

import android.app.Application;

import xjw.app.hencoder.db.gen.DaoMaster;
import xjw.app.hencoder.db.gen.DaoSession;
import xjw.app.hencoder.xble.XBleUtils;


/**
 * Created by xjw on 2017/8/15.
 */

public class App extends Application {

    private static Application instance;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        XBleUtils.get().init(this);
        greenPlay();
    }

    private void greenPlay() {
        //TODO new DaoMaster.OpenHelper
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "db_tpys");
        //TODO helper.getWritableDb()
        mDaoSession = new DaoMaster(helper.getWritableDatabase()).newSession();
    }

    public DaoSession getDS() {
        return mDaoSession;
    }

    public static Application getInstance() {
        return instance;
    }

}
