package cn.xjw.tdex;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xjw on 2017/12/4 17:48
 * Email 1521975316@qq.com
 */

public class ShowToastImpl implements IShowToast {
    @Override
    public int showToast(Context context, String msg) {
        Toast.makeText(context, "我来自另外一个 dex 文件.", Toast.LENGTH_SHORT).show();
        return 200;
    }
}
