package xjw.app.hencoder.module.view.ble.server;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import xjw.app.hencoder.R;
import xjw.app.hencoder.base.BaseActivity;

public class ServerActivity extends BaseActivity {

    @BindView(R.id.et_msg)
    EditText etMsg;
    @BindView(R.id.rv_msg)
    RecyclerView rvMsg;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.iv_head)
    ImageView ivHead;
    @BindView(R.id.btn_send)
    Button btnSend;
    private AcceptThread acceptThread;

    private List<BluetoothSocket> socketList = new ArrayList<>();

    @Override
    protected int getLayoutID() {
        return R.layout.activity_server;
    }

    @Override
    protected void start(Bundle savedInstanceState) {
        acceptThread = new AcceptThread(new AcceptThread.onResponce() {
            @Override
            public void onSuccess(BluetoothSocket bluetoothSocket) {
                System.out.println("onSuccess >> "
                        + bluetoothSocket.getRemoteDevice().getName() + " -> "
                        + bluetoothSocket.getRemoteDevice().getAddress());
                socketList.add(bluetoothSocket);
            }
        });
        acceptThread.start();
    }


    @OnClick({R.id.iv_head, R.id.btn_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_head:

                break;
            case R.id.btn_send:

                break;
        }
    }

    @Override
    protected void onDestroy() {
        //TODO 结束线程
        if (acceptThread != null) {
            acceptThread.stop();
        }
        super.onDestroy();
    }
}
