package xjw.app.hencoder.module.view.greendao;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.App;
import xjw.app.hencoder.db.User;
import xjw.app.hencoder.db.gen.UserDao;

public class GreenActivity extends AppCompatActivity {

    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green);
        ButterKnife.bind(this);
        UserDao uDao = ((App) getApplicationContext()).getDS().getUserDao();
    }

    @OnClick({R.id.iv_head})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_head:
                break;
        }
    }
}
