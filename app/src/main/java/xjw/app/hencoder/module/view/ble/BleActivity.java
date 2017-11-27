package xjw.app.hencoder.module.view.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;

public class BleActivity extends BaseActivity {

    private MyScanModeChanged myScanModeChanged;

    //监听 本地设备被其他设备发现 状态改变
    private class MyScanModeChanged extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {


        }
    }

    private ScanCallback scanCallbackMax = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if (result.getDevice() != null) {
                System.out.println("name >> " + result.getDevice().getName());
                devices.add(result.getDevice());
                mAdapter.notifyDataSetChanged();
            }
        }
    };

    private BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        /**
         * device : 蓝牙设备类
         * rssi : 信号强弱指标 可以大概计算出蓝牙设备离手机的距离。
         *          计算公式为：d = 10^((abs(RSSI) - A) / (10 * n))
         */
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            System.out.println("get device. device >> " + device.getAddress());
            devices.add(device);
            mAdapter.notifyDataSetChanged();
        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                if (devices == null || devices.size() <= 0) {
                    mHandler.sendEmptyMessageDelayed(1, 30 * 1000);
                    return;
                }
                canScan = !canScan;
                blueScan();
            } else if (msg.what == 1) {
                canScan = false;
                blueScan();
            }
        }
    };

    private static final int REQUEST_ENABLE_BT = 100;
    private static final int REQUEST_ENABLE_GPS = 101;
    private static final int REQUEST_OPEN_BLE = 102;

    private List<BluetoothDevice> devices = new ArrayList<>();
    private boolean canScan = true;

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private BluetoothAdapter bluetoothAdapter;
    private BlueRvAdapter mAdapter;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;
    private String uuid;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_ble;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        initView();
        startScan();
        //让本地设备被其他设备发现 如果此时Bluetooth没有启用 会自动开启Bluetooth
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
        startActivityForResult(intent, REQUEST_OPEN_BLE);
        myScanModeChanged = new MyScanModeChanged();

        //TODO 注册监听蓝牙可被检测的广播
//        IntentFilter filter = new IntentFilter();
//        registerReceiver(myScanModeChanged, filter);
    }

    private void useScan() {
        canScan = true;
        blueScan();
    }

    private void startScan() {
        if (checkUsedGPS()) {
            if (checkUsedBleAndIsOpen()) blueScan();
        }
    }

    private boolean checkUsedGPS() {
        //TODO 定位权限请求
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有定位权限", Toast.LENGTH_LONG).show();
            return false;
        }
        //GPS请求
//        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//        startActivityForResult(intent, REQUEST_ENABLE_GPS);
        return true;
    }

    private void blueScan() {
        if (canScan) {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                checkUsedBleAndIsOpen();
                return;
            }
            if (Build.VERSION.SDK_INT <= 22) {
                bluetoothAdapter.startLeScan(scanCallback);
            } else {
                BluetoothLeScanner scanner =
                        bluetoothAdapter.getBluetoothLeScanner();
                scanner.startScan(scanCallbackMax);
            }
            //已配对的设备
            Set<BluetoothDevice> set = bluetoothAdapter.getBondedDevices();
            if (set != null && set.size() > 0) {
                for (BluetoothDevice dev :
                        set) {
                    devices.add(dev);
                    System.out.println(dev.getName() + " >> " + dev.getAddress());
                }
            }
            System.out.println("start scan.");
            //15s后关闭扫描
            mHandler.sendEmptyMessageDelayed(0, 15 * 1000);
            return;
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            if (Build.VERSION.SDK_INT <= 22) {
                bluetoothAdapter.stopLeScan(scanCallback);
            } else {
                BluetoothLeScanner scanner =
                        bluetoothAdapter.getBluetoothLeScanner();
                scanner.stopScan(scanCallbackMax);
            }
            System.out.println("stop scan.");
        }
    }

    private boolean checkUsedBleAndIsOpen() {
        //TODO 权限检测
        //manager.getAdapter 内部其实还是BluetoothAdapter.getDefaultAdapter();
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = manager.getAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "该设备不支持蓝牙功能或蓝牙未开启", Toast.LENGTH_LONG)
                    .show();
            //尝试打开蓝牙
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return false;
        }
        return true;
    }

    private void initView() {
        tvTitle.setText("Blue tooth");
        ivHead.setBackground(ContextCompat.getDrawable(this, R.mipmap.img_05));

        mAdapter = new BlueRvAdapter(devices);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(manager);
        rvContent.setAdapter(mAdapter);

        rvContent.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                BluetoothDevice device = devices.get(position);
                System.out.println("开始连接 >> " + device.getAddress());
                //进行蓝牙连接
                device.connectGatt(BleActivity.this, false, new BluetoothGattCallback() {

                    @Override
                    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
//往蓝牙数据通道的写入数据
//                        BluetoothGattService service = gatt.getService(SERVICE_UUID);
//                        BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
//                        characteristic.setValue(sendValue);
//                        gatt.writeCharacteristic(characteristic);
//                        if (!characteristic.getValue().equal(sendValue)) {
//                        执行重发策略
//                            gatt.writeCharacteristic(characteristic);
//                        }

                    }

                    @Override
                    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic,
                                                     int status) {
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            // 读取数据
//                            BluetoothGattService service = gattt.getService(SERVICE_UUID);
//                            BluetoothGattCharacteristic characteristic = gatt.getCharacteristic(CHARACTER_UUID);
//                            gatt.readCharacteristic();
                            System.out.println("read value: " + characteristic.getValue());
                        }
                    }

                    @Override
                    /**
                     * 发现服务
                     * 真正建立了可通信的连接
                     *
                     */
                    public void onServicesDiscovered(BluetoothGatt gatt, int status) {

                        System.out.println("发现服务 >> " + gatt.getServices());

                    }

                    @Override
                    /**
                     * 成功连接到蓝牙设备
                     * gatt : 蓝牙设备的 Gatt 服务连接类
                     * status : 是否成功执行了连接操作，如果为
                     *          BluetoothGatt.GATT_SUCCESS 表示成功执行连接操作，
                     *          第三个参数才有效
                     *          status == 133 的情况，根据网上大部分人的说法，
                     *          这是因为 Android 最多支持连接 6 到 7 个左右的蓝牙设备，
                     *          如果超出了这个数量就无法再连接了。
                     *          所以当我们断开蓝牙设备的连接时，
                     *          还必须调用 BluetoothGatt#close 方法释放连接资源。
                     *          否则，在多次尝试连接蓝牙设备之后很快就会超出这一个限制，
                     *          导致出现这一个错误再也无法连接蓝牙设备。
                     * newState : 果 newState == BluetoothProfile.STATE_CONNECTED
                     *          说明设备已经连接
                     */
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        //Binder线程,拒绝耗时
                        if (status == BluetoothGatt.GATT_SUCCESS) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {
                                gatt.discoverServices();
                                System.out.println("连接成功");
                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                                System.out.println("连接关闭");
                                gatt.close();
                            }
                        } else {
                            gatt.close();
                            System.out.println("连接失败");
                        }
                    }
                });

            }
        });
    }

    @Override
    protected void onDestroy() {
        canScan = false;
        if (myScanModeChanged != null) {
            unregisterReceiver(myScanModeChanged);
        }
        blueScan();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == RESULT_OK) {
                    System.out.println("bt 可用 >> ok");
                    startScan();
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("bt 不可用 >> canceled");
                }
                break;
            case REQUEST_OPEN_BLE:
                if (resultCode == RESULT_OK) {
                    System.out.println("bt 被发现 >> ok");
                } else if (resultCode == RESULT_CANCELED) {
                    System.out.println("bt 被发现 >> canceled");
                }
                break;
        }
    }


}
