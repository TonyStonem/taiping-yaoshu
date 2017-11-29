package xjw.app.hencoder;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjw on 2017/11/29 18:02
 * Email 1521975316@qq.com
 */

public class IHelloService extends Service {

    private Binder mBinder = new IHelloAidlInterface.Stub() {
        @Override
        public void setUser(User user) throws RemoteException {
            user.setName("taiping-yaoshu");
            users.add(0, user);
            System.out.println(users.toString());
        }

        @Override
        public List<User> getUser() throws RemoteException {
            return users;
        }
    };

    private List<User> users = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        User user = new User();
        user.setName("tony");
        user.setAddress("布里斯托尔");
        users.add(0, user);
        System.out.println("service >> oncreate");
    }
}
