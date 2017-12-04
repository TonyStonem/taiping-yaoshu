package cn.xjw.tdx_plugin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Activity mRemoteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("mFlag")
                && mRemoteActivity != null) {
            TextView tv = new TextView(mRemoteActivity);
            tv.setText("TDex-Plugin");
            mRemoteActivity.setContentView(tv);
        } else {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        }
    }

    public void setActivity(Activity activity) {
        this.mRemoteActivity = activity;
    }

}
