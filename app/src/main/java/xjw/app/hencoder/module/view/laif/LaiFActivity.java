package xjw.app.hencoder.module.view.laif;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;

import com.laifeng.sopcastsdk.camera.exception.CameraNotSupportException;

import java.util.ArrayList;
import java.util.List;

import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.module.view.laif.bean.CameraData;

public class LaiFActivity extends BaseActivity {

    private CameraData mBean;
    private List<CameraData> cameraList = new ArrayList<>();
    private Camera mCameraDevice;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lai_f;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        if (checkDiv()) return;
        //TODO 权限检测
        cameraList = getAllCamerasData(true);
        try {
            openCamera();
        } catch (CameraNotSupportException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启摄像头
     *
     * @return 开启的摄像头
     * @throws CameraNotSupportException
     */
    private synchronized Camera openCamera() throws CameraNotSupportException {
        CameraData bean = cameraList.get(0);
        if (mCameraDevice != null && mBean == bean) {
            return mCameraDevice;
        }
        if (mCameraDevice != null) {
            releaseCamera();
        }
        try {
            System.out.println("开启摄像头 >> " + bean.cameraID);
            mCameraDevice = Camera.open(bean.cameraID);
        } catch (RuntimeException e) {
            System.out.println("摄像头不可用 >> Camera.open(bean.cameraID);");
            e.printStackTrace();
        }
        if (mCameraDevice == null) {
            System.out.println("摄像头不可用 >> mCameraDevice == null");
            throw new CameraNotSupportException();
        }
        //TODO 初始化摄像头参数
        mBean = bean;
        return mCameraDevice;
    }

    /**
     * 释放Camera
     */
    private synchronized void releaseCamera() {
        if (mCameraDevice == null) return;
        mCameraDevice.release();
        if (mBean != null) {
            System.out.println("释放摄像头 >> " + mBean.cameraID);
        }
        mCameraDevice = null;
        mBean = null;
    }

    /**
     * 检查摄像头个数,并初始化摄像头数据
     *
     * @param isBackFirst 默认是否先开启后摄像头
     * @return CameraDate的集合
     */
    private List<CameraData> getAllCamerasData(boolean isBackFirst) {
        ArrayList<CameraData> beans = new ArrayList<>();
        int i = Camera.getNumberOfCameras();
        System.out.println("摄像头个数 >> " + i);
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int j = 0; j < i; j++) {
            Camera.getCameraInfo(j, cameraInfo);
            //前摄像头
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                CameraData bean = new CameraData(j, CameraData.FACING_FRONT);
                if (isBackFirst) {
                    beans.add(bean);
                } else {
                    beans.add(0, bean);
                }
            } else if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                //后摄像头
                CameraData bean = new CameraData(j, CameraData.FACING_BACK);
                if (isBackFirst) {
                    beans.add(0, bean);
                } else {
                    beans.add(bean);
                }
            }
        }
        return beans;
    }

    /**
     * 检测是否支持摄像头
     *
     * @return 是否支持摄像头
     */
    private boolean checkDiv() {
        DevicePolicyManager manager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        boolean b = manager.getCameraDisabled(null);
        if (b) {
            System.out.println("当前设备不支持摄像头 >> checkDiv()");
        }
        return b;
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }
}
