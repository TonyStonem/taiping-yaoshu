package xjw.app.hencoder.module.view.ble.debug;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * 作为客户端连接
 * Created by xjw on 2017/11/27 9:49
 * Email 1521975316@qq.com
 */

public class ConnectThread extends Thread {

    private final BluetoothSocket mSocket;
    private final BluetoothDevice mDevice;

    //    private final static UUID mUUID = UUID.randomUUID();
    private final static UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectThread(BluetoothDevice device) {
        mDevice = device;
        BluetoothSocket tmp = null;
        try {
            tmp = device.createRfcommSocketToServiceRecord(mUUID);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSocket = tmp;
        //TODO Bluetooth & GPS权限检测
        BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
    }

    @Override
    public void run() {

        try {
            mSocket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                mSocket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
        //TODO 回调该socket

    }

    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
