package xjw.app.hencoder.module.view.glide;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.http.AllUrl;

public class GlidePlayActivity extends BaseActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rv_content)
    RecyclerView rvContent;

    private String[] imgs = {AllUrl.GET_IMGS_01, AllUrl.GET_IMGS_02, AllUrl.GET_IMGS_03,
            AllUrl.GET_IMGS_04, AllUrl.GET_IMGS_05, AllUrl.GET_IMGS_01, AllUrl.GET_IMGS_02, AllUrl.GET_IMGS_03,
            AllUrl.GET_IMGS_04, AllUrl.GET_IMGS_05, AllUrl.GET_IMGS_01, AllUrl.GET_IMGS_02, AllUrl.GET_IMGS_03,
            AllUrl.GET_IMGS_04, AllUrl.GET_IMGS_05};
    private PlayAdapter mAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_glide_play;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        tvTitle.setText("Glide Play");
        ivHead.setBackground(ContextCompat.getDrawable(this, R.mipmap.img_05));
        mAdapter = new PlayAdapter(this, imgs);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(manager);
        rvContent.setAdapter(mAdapter);
    }

}
