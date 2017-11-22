package xjw.app.hencoder.module.view.laif;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.Surface;
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

    //TODO 设置手势缩放
    private class MyZoomGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //TODO 当前一些状态的判断
            float i = 0;
            if (detector.getScaleFactor() > 1.0f) {
                i = cameraZoom(true);//放大
            } else if (detector.getScaleFactor() < 1.0f) {
                i = cameraZoom(false);//缩小
            } else {
                return false;
            }
            System.out.println("scale >> " + i);
            return true;
        }
    }

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

    private ScaleGestureDetector mZoomGestureDetector;

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
                //TODO 使用标记记录当前摄像头的状态(INIT,OPENED,PREVIEW)
                openCamera();
            } catch (CameraNotSupportException e) {
                e.printStackTrace();
            }
        }
    }

    private void initView() {
        mZoomGestureDetector = new ScaleGestureDetector(this, new MyZoomGestureListener());
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
        mBean = bean;

        //TODO 初始化摄像头参数
        setCameraParameters();
        //TODO 图像旋转
        setViewRota();
        //TODO 设置预览帧率
        setCameraFps();
        //TODO 设置对焦方式
        setAutoFocusMode();
//        setTouchFocusMode();

        return mCameraDevice;
    }

    private float cameraZoom(boolean b) {
        //TODO 一些标记状态判断
        if (mCameraDevice == null || mBean == null) {
            return -1;
        }
        Camera.Parameters parameters = mCameraDevice.getParameters();
        if (b) {
            parameters.setZoom(Math.min(parameters.getZoom() + 1, parameters.getMaxZoom()));
        } else {
            parameters.setZoom(Math.max(parameters.getZoom() - 1, 0));
        }
        mCameraDevice.setParameters(parameters);
        return (float) parameters.getZoom() / parameters.getMaxZoom();
    }

    /**
     * 设置手动对焦方式
     */
    private void setTouchFocusMode() {
        //TODO 准确设置对焦区域
        // 1.获取当前点击坐标位置
        // 2.通过点击坐标位置转换到摄像头预览界面坐标体系的坐标
        // 3.根据坐标生成对焦区并且设置给摄像头
        try {
            Camera.Parameters parameters = mCameraDevice.getParameters();
            List<String> modes = parameters.getSupportedFocusModes();
            if (modes != null && modes.size() > 0 && modes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCameraDevice.setParameters(parameters);
            } else if (modes != null && modes.size() > 0) {
                parameters.setFocusMode(modes.get(0));
                mCameraDevice.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置自动对焦方式
     */
    private void setAutoFocusMode() {
        try {
            Camera.Parameters parameters = mCameraDevice.getParameters();
            List<String> modes = parameters.getSupportedFocusModes();
            if (modes != null && modes.size() > 0 && modes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                mCameraDevice.setParameters(parameters);
            } else if (modes != null && modes.size() > 0) {
                parameters.setFocusMode(modes.get(0));
                mCameraDevice.setParameters(parameters);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置预览帧率
     * Oppo和Vivo的前置摄像头，当fps不为15的时候，在弱光环境下预览图像会很黑。
     */
    private void setCameraFps() {
        int expectedFps = 15;
        Camera.Parameters parameters = mCameraDevice.getParameters();
        expectedFps *= 1000;
        //摄像头帧率变化范围
        List<int[]> ints = parameters.getSupportedPreviewFpsRange();
        int[] closestRange = ints.get(0);
        int measure = Math.abs(closestRange[0] - expectedFps) + Math.abs(closestRange[1] - expectedFps);
        for (int[] range : ints) {
            if (range[0] <= expectedFps && range[1] >= expectedFps) {
                int curMeasure = Math.abs(range[0] - expectedFps) + Math.abs(range[1] - expectedFps);
                if (curMeasure < measure) {
                    closestRange = range;
                    measure = curMeasure;
                }
            }
        }
        parameters.setPreviewFpsRange(closestRange[0], closestRange[1]);
        mCameraDevice.setParameters(parameters);
    }

    /**
     * 图像旋转 要根据当前应用的屏幕旋转情况实时调整摄像头角度
     * 1.通过摄像头的setDisplayOrientation(result)方法
     * 2.通过OpenGL的矩阵进行旋转
     */
    private void setViewRota() {
        int result;
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                rotation = 0;
                break;
            case Surface.ROTATION_90:
                rotation = 90;
                break;
            case Surface.ROTATION_180:
                rotation = 180;
                break;
            case Surface.ROTATION_270:
                rotation = 270;
                break;
        }
        rotation = 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mBean.cameraID, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + rotation) % 360;
            result = (360 - result) % 360;
        }
//        else if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
//            result = (info.orientation - rotation + 360) % 360;
//        }
        else {
            result = (info.orientation - rotation + 360) % 360;
        }
        mCameraDevice.setDisplayOrientation(result);
    }

    /**
     * 初始化摄像头参数
     */
    private void setCameraParameters() {
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
                if (Math.abs(size.width - screenWidth) == minWidthDiff) {
                    if (Math.abs(size.height - screenHeight) < minHeightDiff) {
                        optimalSize = size;
                        minHeightDiff = Math.abs(size.height - screenHeight);
                    }
                }
            }
        }
        //TODO 测试
//        svLaif.getLayoutParams().height = optimalSize.height;
//        svLaif.getLayoutParams().width = optimalSize.width;

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
    public boolean onTouchEvent(MotionEvent event) {
        //TODO 判断手动聚焦时的状态
        return mZoomGestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }
}
