package xjw.app.hencoder.module.view.ble;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.xble.OnScanCallBack;
import xjw.app.hencoder.module.view.ble.server.ServerActivity;
import xjw.app.hencoder.xble.XBleUtils;

public class BleSActivity extends BaseActivity {

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            XBleUtils.get().stopScan();
            if (devices.size() > 0) {
                XBleUtils.get().connec(devices.get(0));
            }
        }
    };

    private List<BluetoothDevice> devices = new ArrayList<>();

    @Override

    protected int getLayoutID() {
        return R.layout.activity_ble_s;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        test();
    }

    private void test() {
        XBleUtils.get().startScan(new OnScanCallBack() {
            @Override
            public void onSuccess(BluetoothDevice device) {
                devices.add(0, device);
            }
        });
        mHandler.sendEmptyMessageDelayed(0, 10 * 1000);
    }

    public void server(View view) {
        startActivity(new Intent(BleSActivity.this, ServerActivity.class));
    }

    public void client(View view) {

    }


}
