package xjw.app.hencoder.module.view.ble.server;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * 作为服务器连接
 * Created by xjw on 2017/11/27 9:28
 * Email 1521975316@qq.com
 * <p>
 * RFCOMM同一时刻一信道只允许一个客户端连接
 * BluetoothServerSocket,BluetoothSocket的方法都是线程安全的
 */

public class AcceptThread extends Thread {

    private OutputStream mOutSocket;

    public interface onResponce {
        void onSuccess(BluetoothSocket bluetoothSocket);
    }

    private onResponce mResponce;

    private final BluetoothServerSocket mServerSocket;

    private final static String mName = "XJWStudioBluetoothChart";
    //    private final static UUID mUUID = UUID.randomUUID();
    private final static UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public AcceptThread(onResponce responce) {
        mResponce = responce;
        BluetoothServerSocket tmp = null;
        try {
            //TODO Bluetooth & GPS权限检测
            //获得一个BluetoothServerSocket对象
            tmp = BluetoothAdapter.getDefaultAdapter().
                    listenUsingRfcommWithServiceRecord(mName, mUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = tmp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        while (true) {
            try {
                //侦听连接请求,阻塞
                socket = mServerSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("error >> socket = mServerSocket.accept()");
                return;
            }
            //此时socket已经连接好了,客户端不应该呼叫connect()
            if (socket != null) {
                mResponce.onSuccess(socket);
                cancel();
            }
        }
    }

    private void cancel() {
        try {
            //释放server socket和它的资源,但不会关闭accept()返回的连接好的BluetoothSocket对象
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
