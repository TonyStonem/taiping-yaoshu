package xjw.app.hencoder.module.view.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
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

    private final BluetoothServerSocket mServerSocket;

    public AcceptThread(BluetoothAdapter adapter, String name, UUID uuid) {
        BluetoothServerSocket tmp = null;
        try {
            //获得一个BluetoothServerSocket对象
            tmp = adapter.listenUsingRfcommWithServiceRecord(name, uuid);
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
                break;
            }
            //此时socket已经连接好了,客户端不应该呼叫connect()
            if (socket != null) {
                //TODO 将socket回调
                cancel();
                break;
            }
        }
    }

    public void cancel() {
        try {
            //释放server socket和它的资源,但不会关闭accept()返回的连接好的BluetoothSocket对象
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
