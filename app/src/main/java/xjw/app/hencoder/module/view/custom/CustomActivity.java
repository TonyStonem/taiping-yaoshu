package xjw.app.hencoder.module.view.custom;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;

public class CustomActivity extends BaseActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_custom_first;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        tvTitle.setText("HenCoder 自定义View");
        ivHead.setBackground(ContextCompat.getDrawable(this, R.mipmap.img_01));
    }

}
