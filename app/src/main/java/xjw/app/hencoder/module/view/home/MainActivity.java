package xjw.app.hencoder.module.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.module.view.ble.BleActivity;
import xjw.app.hencoder.module.view.custom.CustomActivity;
import xjw.app.hencoder.module.view.videos.VideoSaveActivity;

public class MainActivity extends BaseActivity {

    private View.OnClickListener myRvItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            check(view.getId(), (int) view.getTag());
        }
    };

    @BindView(R.id.rv_content)
    RecyclerView rvContent;
    private List<MainRvBean> beanData = new ArrayList<>();
    private MainRvAdapter mAdapter;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_main;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        initData();
        initView();
    }

    private void initView() {
        mAdapter = new MainRvAdapter(beanData, myRvItemListener);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rvContent.setLayoutManager(manager);
        rvContent.setAdapter(mAdapter);
    }

    private void initData() {
        beanData.add(new MainRvBean("自定义View",
                "自定义view的学习笔记",
                "2017-8-15 11:27:25", "持续:1天",R.mipmap.img_01));
        beanData.add(new MainRvBean("j2ee_note",
                "J2ee的一个学习笔记",
                "2017-8-17 10:51:41", "持续:1天",R.mipmap.img_04));
        beanData.add(new MainRvBean("蓝牙模块",
                "蓝牙模块,扫描搜索周围蓝牙进行连接及数据交互",
                "2017-11-14 10:28:05", "持续:1天",R.mipmap.img_05));
        beanData.add(new MainRvBean("视频录制",
                "视频录制模块,录制视频存储到本地",
                "2017-11-15 09:53:04", "持续:1天",R.mipmap.img_06));
    }

    private void check(int id, int tag) {
        switch (tag) {
            case 0://自定义view
                startActivity(new Intent(MainActivity.this, CustomActivity.class));
                break;
            case 2://蓝牙
                startActivity(new Intent(MainActivity.this, BleActivity.class));
                break;
            case 3://视频录制
                startActivity(new Intent(MainActivity.this, VideoSaveActivity.class));
                break;
        }
    }

}
