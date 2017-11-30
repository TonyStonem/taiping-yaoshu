package xjw.app.tp_client;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import xjw.app.hencoder.IHelloAidlInterface;
import xjw.app.hencoder.User;

public class MainActivity extends AppCompatActivity {

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            isConnec = true;
            iHello = IHelloAidlInterface.Stub.asInterface(iBinder);
            if (iHello == null) {
                connecS();
            }
            System.out.println("connec >> true");
            try {
                System.out.println(iHello.getUser().toString());
                User user = new User();
                user.setName("sid");
                user.setAddress("布里斯托尔");
                iHello.setUser(user);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            System.out.println("connec >> false");
            isConnec = false;
        }
    };

    private boolean isConnec = false;
    private IHelloAidlInterface iHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void connecS() {
        Intent service = new Intent();
        service.setAction("xjw.app.aidl");
        service.setPackage("xjw.app.hencoder");
        bindService(service, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!isConnec) {
            connecS();
            System.out.println("onStart() >> connecS");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isConnec) {
            unbindService(connection);
        }
    }
}
