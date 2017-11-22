package xjw.app.hencoder.module.view.videos;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseFragment;

/**
 * Created by Q on 2015/9/2.
 */
public class RecordFragment extends BaseFragment {
    // 存放录制录制的视频的路径
    private final static File videoFile = new File(Environment.getExternalStorageDirectory(),
            "/rec/rec.mp4");
    private final static long MAX_RECORDER_TIME = 20;// 视频最长录制时间
    @BindView(R.id.surface_recorder)
    SurfaceView surfaceRecorder;
    @BindView(R.id.text_record_time)
    TextView textRecordTime;
    @BindView(R.id.img_record)
    ImageView imgRecord;
    @BindView(R.id.rel_recorder_video)
    RelativeLayout relRecorderVideo;
    private Camera camera;
    private MediaRecorder recorder;
    private volatile boolean isRecording = false;// 标识MediaRecorder是否正处于录制状态
    private volatile boolean recorderIsRelease = false;// 标识MediaRecorder是否已经被release
    private long startTime = 0;
    private long stopTime = 0;
    private Handler recorderHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isRecording) {
                return;
            }
            textRecordTime.setText(msg.what + "s");
            if (msg.what < MAX_RECORDER_TIME) {
                sendEmptyMessageDelayed(++msg.what, 1000);
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_record;
    }

    @Override
    protected void onBindFinish() {
        if (!videoFile.exists()) {
            File dir = videoFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        initSurfaceView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCamera();
        initRecorder();
    }

    @Override
    public void onPause() {
        super.onPause();
        recorderHandler.removeCallbacksAndMessages(null);
        releaseCamera();
        releaseRecorder();
    }

    private void releaseRecorder() {
        if (recorder != null && isRecording) {
            if (checkRecorderTime()) {
                recorder.stop();
            }
            recorder.reset();
            recorder.release();
            isRecording = false;
            recorderIsRelease = true;
        }
    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            try {
                camera.unlock();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initSurfaceView() {
        surfaceRecorder.setKeepScreenOn(true);
        SurfaceHolder surfaceHolder = surfaceRecorder.getHolder();
        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (recorder != null) {
                    recorder.setPreviewDisplay(holder.getSurface());
                }
                camera.lock();
                camera.startPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (holder.getSurface() == null) {
                    return;
                }
                camera.stopPreview();
                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (recorder != null) {
                    recorder.setPreviewDisplay(holder.getSurface());
                }
                camera.startPreview();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                /*releaseCamera();
                releaseRecorder();*/
            }
        });
    }

    Comparator<Camera.Size> comparator = new Comparator<Camera.Size>() {
        @Override
        public int compare(Camera.Size o1, Camera.Size o2) {
            return o2.width - o1.width;
        }
    };

    private void initCamera() {
        camera = Camera.open();
        Camera.Parameters parameters = camera.getParameters();
        List<String> focusModes = parameters.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        }
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        if (sizes != null && sizes.size() > 0) {
            Collections.sort(sizes, comparator);
            Camera.Size size = sizes.get(Math.max(0, sizes.size() / 5));
            surfaceRecorder.getHolder().setFixedSize(size.width, size.height);
        }
        /*List<Camera.Size> videoSizes = parameters.getSupportedVideoSizes();
        if (videoSizes != null && videoSizes.size() > 0) {
            Collections.sort(videoSizes, comparator);
            Camera.Size size = videoSizes.get(Math.max(0, sizes.size() / 5));
            parameters.setPreviewSize(size.width, size.height);
        }*/
        /*List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        if (pictureSizes != null && pictureSizes.size() > 0) {
            Collections.sort(pictureSizes, comparator);
            Camera.Size size = pictureSizes.get(Math.max(0, pictureSizes.size() / 5));
            parameters.setPictureSize(size.width, size.height);
        }*/
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
        camera.unlock();
    }

    private void initRecorder() {
        textRecordTime.setText("0s");
        recorder = new MediaRecorder();
        recorder.setCamera(camera);
        recorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                if (mr != null) {
                    mr.reset();
                }
            }
        });
        recorder.setOrientationHint(90);
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncodingBitRate(44100);    // 声音比特率
        recorder.setAudioSamplingRate(16000);       // 声音采样率
        // 设置声音编码的格式
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        // 设置图像编码的格式
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        recorder.setVideoEncodingBitRate(3000000);
        recorder.setVideoSize(1280, 720);
        recorder.setVideoFrameRate(30);
        recorder.setOutputFile(videoFile.getAbsolutePath());
        recorderIsRelease = false;
    }

    @OnTouch({R.id.img_record})
    boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isRecording) {
                    return true;
                }
                if (recorderIsRelease) {
                    initRecorder();
                }
                try {
                    camera.lock();
                    camera.unlock();
                    recorder.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                startTime = System.currentTimeMillis();
                isRecording = true;
                recorderHandler.sendEmptyMessage(0);
                recorder.start();
                break;
            case MotionEvent.ACTION_UP:
                stopRecorder();
                break;
        }
        return true;
    }

    private void stopRecorder() {
        stopTime = System.currentTimeMillis();
        releaseRecorder();
    }

    private boolean checkRecorderTime() {
        return stopTime - startTime > 1500;
    }

    @OnClick({R.id.img_back, R.id.img_send})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                break;
            case R.id.img_send:
                break;
        }
    }
}
