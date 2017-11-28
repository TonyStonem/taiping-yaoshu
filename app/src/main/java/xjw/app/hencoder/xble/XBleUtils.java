package xjw.app.hencoder.xble;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xjw on 2017/11/28 14:35
 * Email 1521975316@qq.com
 */

public class XBleUtils {

    private BluetoothGattCallback mConnecCallback = new BluetoothGattCallback() {

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

    };

    private BluetoothAdapter.LeScanCallback mScanCallBack = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            System.out.println("mScanCallBack >> " + device.getAddress());
            if (adds.contains(device.getAddress())) {
                adds.add(device.getAddress());
                if (mScanResultCallBack != null) {
                    mScanResultCallBack.onSuccess(device);
                }
            }
        }
    };

    private ScanCallback mScanSupportCallBack = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            System.out.println("mScanSupportCallBack >> " + result.getDevice().getAddress());
            if (adds.contains(result.getDevice().getAddress())) {
                adds.add(result.getDevice().getAddress());
                if (mScanResultCallBack != null) {
                    mScanResultCallBack.onSuccess(result.getDevice());
                }
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private static final int STATE_SCAN_END = 0;
    private static final int STATE_SCAN_ING = 1;

    private int mState = STATE_SCAN_END;

    private Context mContext;
    private final static XBleUtils mUtils = new XBleUtils();
    private BluetoothAdapter mBleAdapter;

    private List<String> adds = new ArrayList<>();
    private OnScanCallBack mScanResultCallBack;


    private XBleUtils() {
    }

    public static XBleUtils get() {
        return mUtils;
    }

    public void init(Application context) {
        this.mContext = context;
        mBleAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    private boolean isSupportBle() {
        return mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    private void enableBle() {
        if (isEnable()) {
            mBleAdapter.enable();
        }
    }

    private void disableBle() {
        if (isEnable()) {
            mBleAdapter.disable();
        }
    }

    private boolean isEnable() {
        return mBleAdapter != null && mBleAdapter.isEnabled();
    }

    public void startScan(OnScanCallBack callBack) {
        if (mState == STATE_SCAN_ING) return;
        this.mScanResultCallBack = callBack;
        if (isSupportBle()) {
            enableBle();
            adds.clear();
            if (Build.VERSION.SDK_INT <= 22) {
                mBleAdapter.startLeScan(mScanCallBack);
            } else {
                BluetoothLeScanner scanner =
                        mBleAdapter.getBluetoothLeScanner();
                scanner.startScan(mScanSupportCallBack);
            }
            mState = STATE_SCAN_ING;
        }
    }

    public void stopScan() {
        if (mState == STATE_SCAN_END) return;
        if (Build.VERSION.SDK_INT <= 22) {
            mBleAdapter.stopLeScan(mScanCallBack);
        } else {
            BluetoothLeScanner scanner =
                    mBleAdapter.getBluetoothLeScanner();
            scanner.stopScan(mScanSupportCallBack);
        }
        disableBle();
        mState = STATE_SCAN_END;
    }

    public void connec(BluetoothDevice device, boolean autoConnec) {
        if (mState == STATE_SCAN_ING) return;
        device.connectGatt(mContext, autoConnec, mConnecCallback);
    }

    public void connec(BluetoothDevice device) {
        connec(device, true);
    }


}
