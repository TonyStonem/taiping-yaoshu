package xjw.app.hencoder.module.view.glide;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.Arrays;

import xjw.app.hencoder.R;

/**
 * Created by xjw on 2017/8/15.
 */

public class PlayAdapter extends BaseQuickAdapter<String,BaseViewHolder>{

    private final Context context;

    public PlayAdapter(Context context, String[] datas) {
        super(R.layout.item_play_rv_item, Arrays.asList(datas));
        this.context = context;
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        ImageView head = helper.getView(R.id.iv_head);
        Glide.with(context)
                .load(item)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.test)
                .into(head);
    }


}
