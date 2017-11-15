package xjw.app.hencoder.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import xjw.app.hencoder.BuildConfig;
import xjw.app.hencoder.utils.ActivityUtils;

public abstract class BaseActivity extends AppCompatActivity
        implements BaseContract.View{

    private AppCompatActivity activity;
    private Dialog loadDialog;
    private int loadDialogCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        activity = this;
        ButterKnife.bind(activity);
        ActivityUtils.get().add(activity);
        start(savedInstanceState);
    }

    @Override
    public void showLoadDias() {
        if (loadDialog == null) {
            loadDialog = new ProgressDialog.Builder(this).create();
            loadDialog.setCanceledOnTouchOutside(BuildConfig.DEBUG);
            loadDialog.setCancelable(true);
            loadDialogCount = 0;
        }
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
        loadDialogCount++;
    }

    @Override
    public void cancelLoadDialogs() {
        cancelLoadDia(false);
    }

    @Override
    public void showSnacks(String msg, int actionResId, final Runnable action) {
        View view = findViewById(android.R.id.content);
        Snackbar.make(view, msg, Snackbar.LENGTH_SHORT).setAction(actionResId, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                action.run();
            }
        }).show();
    }

    @Override
    public void closes() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        cancelLoadDia(true);
        ActivityUtils.get().remove(activity);
        super.onDestroy();
    }

    protected abstract int getLayoutID();

    protected abstract void start(Bundle savedInstanceState);

    public void cancelLoadDia(boolean force) {
        if (force) loadDialogCount = 0;
        loadDialogCount--;
        if (loadDialogCount <= 0 && loadDialog != null) loadDialog.cancel();
    }


}
