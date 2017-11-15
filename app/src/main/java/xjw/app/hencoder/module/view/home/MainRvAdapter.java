package xjw.app.hencoder.module.view.home;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import xjw.app.hencoder.R;
import xjw.app.hencoder.base.App;

/**
 * Created by xjw on 2017/8/15.
 */

public class MainRvAdapter extends RecyclerView.Adapter<MainRvAdapter.MyHolder> {

    private final List<MainRvBean> beanData;
    private final View.OnClickListener onClickListener;

    public MainRvAdapter(List<MainRvBean> beanData, View.OnClickListener onClickListener) {
        this.beanData = beanData;
        this.onClickListener = onClickListener;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(App.getInstance()).inflate(R.layout.item_main_rv_cardview, parent, false);
        return new MyHolder(contentView);
    }

    @Override
    public int getItemCount() {
        return beanData.size();
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        MainRvBean bean = beanData.get(position);
        holder.tvTitle.setText(bean.title);
        holder.tvCount.setText(bean.content);
        holder.tvNum.setText(bean.time);
        holder.view.setTag(position);
        holder.view.setOnClickListener(onClickListener);
        holder.ivHead.setBackground(ContextCompat.getDrawable(App.getInstance(),
                bean.imgResID));
    }

    class MyHolder extends RecyclerView.ViewHolder {

        ImageView ivHead;
        TextView tvTitle;
        TextView tvCount;
        TextView tvNum;
        View view;

        public MyHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            tvCount = (TextView) itemView.findViewById(R.id.tv_content);
            tvNum = (TextView) itemView.findViewById(R.id.tv_num);
            ivHead = (ImageView) itemView.findViewById(R.id.iv_head);
            view = itemView.findViewById(R.id.ll_content);
        }

    }

}
