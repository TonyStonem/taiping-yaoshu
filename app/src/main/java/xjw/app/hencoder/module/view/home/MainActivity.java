package xjw.app.hencoder.module.view.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;
import xjw.app.hencoder.http.AllUrl;
import xjw.app.hencoder.module.view.ble.BleActivity;
import xjw.app.hencoder.module.view.custom.CustomActivity;
import xjw.app.hencoder.module.view.glide.GlidePlayActivity;
import xjw.app.hencoder.module.view.laif.LaiFActivity;
import xjw.app.hencoder.module.view.videos.VideoSaveActivity;
import xjw.app.hencoder.utils.OkHttpManager;
import xjw.app.hencoder.utils.UIUtils;
import xjw.app.hencoder.whyhttp.WhyHttp;

public class MainActivity extends BaseActivity {

    public static final String USERNAME = "xjwStudio";
    public static final String PASSWORD = "admin";
    public static final String mSignIn = AllUrl.GET_SIGN_IN + USERNAME + "&pswd=" + PASSWORD;

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
        //TODO 更换自己的网络框架
//        signIn();
        signIn4WhyHttp();
        initData();
        initView();
    }

    private void signIn4WhyHttp() {
        String json = WhyHttp.create().get(mSignIn);
        WhyHttp.create().get(mSignIn, new WhyHttp.OnResponse() {

            @Override
            public void onSuccess(String result) {

            }
        });
    }

    private void signIn() {
        showLoadDias();
        OkHttpManager.get(mSignIn
                , this, new OkHttpManager.OnResponse<String>() {
                    @Override
                    public String analyseResult(String result) {
                        cancelLoadDialogs();
                        return result;
                    }

                    @Override
                    public void onFailed(int code, String msg, String url) {
                        super.onFailed(code, msg, url);
                        cancelLoadDialogs();
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (TextUtils.isEmpty(s)) {
                            UIUtils.showToast("登录失败");
                            return;
                        }
                        UIUtils.showToast(s);
                        System.out.println(s);
                    }
                });
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
                "2017-8-15 11:27:25", "持续:1天", R.mipmap.img_01));
        beanData.add(new MainRvBean("j2ee_note",
                "J2ee的一个学习笔记",
                "2017-8-17 10:51:41", "持续:1天", R.mipmap.img_04));
        beanData.add(new MainRvBean("蓝牙模块",
                "蓝牙模块,扫描搜索周围蓝牙进行连接及数据交互",
                "2017-11-14 10:28:05", "持续:1天", R.mipmap.img_05));
        beanData.add(new MainRvBean("视频录制",
                "视频录制模块,录制视频存储到本地",
                "2017-11-15 09:53:04", "持续:1天", R.mipmap.img_06));
        beanData.add(new MainRvBean("Glide Play",
                "模仿商城图片列表,使用Glide加载",
                "2017-11-17 09:37:34", "持续:1天", R.mipmap.img_06));
        beanData.add(new MainRvBean("xjwLive",
                "搞个直播玩玩,喵喵喵.",
                "2017-11-20 10:02:08", "持续:1天", R.mipmap.img_06));
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
            case 4://Glide
                startActivity(new Intent(MainActivity.this, GlidePlayActivity.class));
                break;
            case 5://直播
                startActivity(new Intent(MainActivity.this, LaiFActivity.class));
                break;
        }
    }

}
