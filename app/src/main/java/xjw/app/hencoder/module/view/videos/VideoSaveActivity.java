package xjw.app.hencoder.module.view.videos;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;

public class VideoSaveActivity extends BaseActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    private Fragment fragment;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_video_save;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        initView();
        int i = new Random().nextInt(2);
        System.out.println("i > " + i);
        if (i == 1) {
            fragment = new CameraFragment();
        }else {
            fragment = new RecordFragment();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fl_content, fragment)
                .commit();
    }

    private void initView() {
        tvTitle.setText("视频录制");
        ivHead.setBackground(ContextCompat.getDrawable(this, R.mipmap.img_06));
    }
}
