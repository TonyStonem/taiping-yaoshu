package xjw.app.hencoder.module.view.ble.debug;

import android.bluetooth.BluetoothDevice;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import xjw.app.hencoder.R;

/**
 * Created by xjw on 2017/8/15.
 */

public class BlueRvAdapter extends BaseQuickAdapter<BluetoothDevice,BaseViewHolder>{

    public BlueRvAdapter(List<BluetoothDevice> devices) {
        super(R.layout.item_blue_rv_item, devices);
    }

    @Override
    protected void convert(BaseViewHolder helper, BluetoothDevice item) {
        helper.setText(R.id.tv_title, item.getName());
        helper.setText(R.id.tv_content, item.getAddress());
    }


}
