package xjw.app.hencoder.utils;

import android.view.View;

/**
 * Created by xjw on 2017/4/5.
 */
public class MeasureUtils {
    public static int measure(int spec, int defultSize) {
        int mode = View.MeasureSpec.getMode(spec);
        int size = View.MeasureSpec.getSize(spec);
        int result = defultSize;
        switch (mode) {
            case View.MeasureSpec.EXACTLY:
                result = size;
                break;
            case View.MeasureSpec.AT_MOST:
                result = Math.min(defultSize, size);
                break;
            case View.MeasureSpec.UNSPECIFIED:
                result = defultSize;
                break;
            default:
                result = defultSize;
                break;
        }
        return result;
    }
}
