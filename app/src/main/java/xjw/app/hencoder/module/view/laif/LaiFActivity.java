package xjw.app.hencoder.module.view.laif;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.laifeng.sopcastsdk.camera.exception.CameraNotSupportException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.module.view.laif.bean.CameraData;

public class LaiFActivity extends BaseActivity {

    private Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            //需将NV21转为I420格式 然后交给x264编码库 如果是硬编 安卓支持I420和NV12

        }
    };

    @BindView(R.id.sv_laif)
    SurfaceView svLaif;

    private CameraData mBean;
    private List<CameraData> cameraList = new ArrayList<>();
    private Camera mCameraDevice;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_lai_f;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        initView();
    }

    private void initCamera() {
        if (!checkDiv()) {
            //TODO 权限检测
            cameraList = getAllCamerasData(false);
            try {
                openCamera();
            } catch (CameraNotSupportException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {

        svLaif.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder == null) {
                    return;
                }
                if (mCameraDevice == null) {
                    initCamera();
                }
                try {
                    mCameraDevice.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCameraDevice.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (holder == null) {
                    return;
                }
                mCameraDevice.stopPreview();
                try {
                    mCameraDevice.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCameraDevice.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });

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
        initCameraParameters();
        //TODO 图像旋转
        mBean = bean;
        return mCameraDevice;
    }

    /**
     * 初始化摄像头参数
     */
    private void initCameraParameters() {
        //设置预览回调的图片格式 Android推荐的PreView Format时NV21
        Camera.Parameters parameters = mCameraDevice.getParameters();
        parameters.setPreviewFormat(ImageFormat.NV21);
        /* 设置预览图像大小 */
        Camera.Size optimalSize = null;
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        double minWidthDiff = Double.MAX_VALUE;
        double minHeightDiff = Double.MAX_VALUE;
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes != null && sizes.size() > 0) {
            //找到宽度差距最小的
            for (Camera.Size size :
                    sizes) {
                if (Math.abs(size.width - screenWidth) < minWidthDiff) {
                    minWidthDiff = Math.abs(size.width - screenWidth);
                    System.out.println("minWidthDiff >> " + minWidthDiff);
                }
            }
            //在宽度差距最小的里面 找到高度差距最小的
            for (Camera.Size size :
                    sizes) {
                if (Math.abs(size.width - screenWidth) == minWidthDiff){
                    if (Math.abs(size.height - screenHeight) < minHeightDiff){
                        optimalSize = size;
                        minHeightDiff = Math.abs(size.height - screenHeight);
                    }
                }
            }
        }
        //TODO 测试
        svLaif.getLayoutParams().height = optimalSize.height;
        svLaif.getLayoutParams().width = optimalSize.width;

        parameters.setPreviewSize(optimalSize.width, optimalSize.height);
        mCameraDevice.setParameters(parameters);
        //预览回调的callback 在PreviewCallback中会返回Preview的N21图片
        mCameraDevice.setPreviewCallback(previewCallback);
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
