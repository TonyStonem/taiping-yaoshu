package xjw.app.hencoder.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {
    protected View contentView;
    protected Activity act;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        if (contentView == null) {
            int layoutId = getLayoutId();
            if (layoutId == 0) {
                throw new IllegalArgumentException("layout id is 0!");
            }
            contentView = inflater.inflate(layoutId, container, false);
            ButterKnife.bind(this, contentView);
            onBindFinish();
        }
        return contentView;
    }

    protected abstract int getLayoutId();

    protected void onBindFinish() {
    }
}
