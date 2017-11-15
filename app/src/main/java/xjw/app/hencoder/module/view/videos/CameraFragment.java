package xjw.app.hencoder.module.view.videos;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseFragment;
import xjw.app.hencoder.utils.ThreadUtils;

public class CameraFragment extends BaseFragment implements AdapterView.OnItemLongClickListener {
    public final static int REQUEST_PREVIEW_PHOTO = 101;
    private final static int MSG_SAVE_IMAGE = 1;
    private final static int MSG_SHOW_IMAGE = 2;
    @BindView(R.id.ib_done)
    ImageView mdoneIb;
    @BindView(R.id.surface_take_photo)
    SurfaceView surfaceTakePhoto;
    @BindView(R.id.tv_photo_count)
    TextView mCountTv;
    @BindView(R.id.iv_preview)
    ImageView mPreviewIv;
    @BindView(R.id.btn_capture)
    ImageView mCameraIb;

    private Camera camera;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_IMAGE:
                    mPreviewIv.setVisibility(View.VISIBLE);
                    /*BitmapUtils.loadFullyBitmap(act, new File((String) msg.obj), mPreviewIv, 130,
                            130);*/
                    break;
                case MSG_SAVE_IMAGE:
                    camera.startPreview();
                    break;
                default:
                    break;

            }
            return false;
        }
    });

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_camera;
    }

    @Override
    protected void onBindFinish() {
        surfaceTakePhoto.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (holder == null) {
                    return;
                }
                if (camera == null) {
                    initCamera();
                }
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (holder == null) {
                    return;
                }
                camera.stopPreview();
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                /*showToast("destroy");
                holder.removeCallback(this);
                releaseCamera();*/
            }
        });
    }

    @OnClick({R.id.btn_capture, R.id.iv_preview, R.id.ib_back, R.id.ib_done})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_capture:
                mCameraIb.setEnabled(false);
                camera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        handleCameraData(data);
                        mCameraIb.setEnabled(true);
                        camera.startPreview();
                    }
                });
                break;
            case R.id.iv_preview:
                break;
            case R.id.ib_back:
                break;
            case R.id.ib_done:
                break;
            default:
                break;
        }
    }

    Comparator<Camera.Size> comparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return o2.width - o1.width;
        }
    };

    private void initCamera() {
        camera = Camera.open();
        if (camera == null) {
            return;
        }
        Camera.Parameters parameters = camera.getParameters();
        List<String> flashModes = parameters.getSupportedFlashModes();
        if (flashModes != null && flashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        }
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes != null && focusModes.contains(Camera.Parameters
                .FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;
        List<Camera.Size> previewSize = parameters.getSupportedPreviewSizes();
        if (previewSize != null && previewSize.size() > 0) {
            Collections.sort(previewSize, comparator);
            for (int i = 0; i < previewSize.size(); i++) {
                Camera.Size size = previewSize.get(i);
                if (size.width * size.height <= screenWidth * screenHeight) {
                    if (i > 0) {
                        size = previewSize.get(i - 1);
                    }
                    parameters.setPreviewSize(size.width, size.height);
                    break;
                }
            }
        }
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        if (pictureSizes != null && pictureSizes.size() > 0) {
            Collections.sort(pictureSizes, comparator);
            for (int i = 0; i < pictureSizes.size(); i++) {
                Camera.Size size = pictureSizes.get(i);
                if (size.width * size.height <= screenWidth * screenHeight) {
                    if (i > 0) {
                        size = pictureSizes.get(i - 1);
                    }
                    parameters.setPictureSize(size.width, size.height);
                    break;
                }
            }
        }
        camera.setDisplayOrientation(90);
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.setJpegQuality(100);
        parameters.setZoom(0);
        parameters.setRotation(90);
        camera.setParameters(parameters);
        if (surfaceTakePhoto != null && surfaceTakePhoto.getHolder() != null) {
            try {
                camera.setPreviewDisplay(surfaceTakePhoto.getHolder());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void handleCameraData(final byte[] data) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                //FileUtil.createFileDir(ConstantValues.DIILIGHT_IMAGE_CACHE_DATA);
                String fileName = "Taidii" + System.currentTimeMillis() + ".jpeg";
                String fileUri = Environment.getExternalStorageDirectory().getAbsolutePath() + fileName;
                File file = new File(fileUri);
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(data);
                    fos.flush();
                    fos.close();
                    System.out.println("处理前大小>" + file.length());
                    Message message = Message.obtain();
                    message.what = MSG_SHOW_IMAGE;
                    message.obj = fileUri;
                    handler.sendMessage(message);
                    updateListData(fileUri, fileName);
                    /**
                     * 三星手机拍照结果是被旋转了90°
                     */
                    //CommonUtils.rotateImage(fileUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateListData(String fileUri, String fileName) {
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    @Override
    public void onResume() {
        initCamera();
        super.onResume();
    }

    @Override
    public void onPause() {
        releaseCamera();
        handler.removeCallbacksAndMessages(null);
        super.onPause();
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PREVIEW_PHOTO:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
