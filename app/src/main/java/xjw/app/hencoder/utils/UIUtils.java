package xjw.app.hencoder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import xjw.app.hencoder.base.App;


/**
 * Created by xjw on 2016/11/27.
 */

public class UIUtils {

    private static Toast toast;

    public static void showToast(Context context, String content) {
        if (toast == null)
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);

        toast.setText(content);
        toast.show();
    }

    public static void showToast(String content) {
        showToast(getContext(), content);
    }

    /**
     * 获取MyApplication上下文
     *
     * @return
     */
    public static Context getContext() {
        return App.getInstance();
    }

//    public static String getClearAdDivJs(Context context) {
//        String js = "javascript:";
//        Resources res = context.getResources();
//        String[] adDivs = res.getStringArray(R.array.adBlockDiv);
//        for (int i = 0; i < adDivs.length; i++) {
//            js += "var adDiv" + i + "= document.getElementById('" + adDivs[i] +
//                    "');if(adDiv" + i + " != null)adDiv" + i +
//                    ".parentNode.removeChild(adDiv" + i + ");";
//        }
//        return js;
//    }

    /**
     * 获取当前的网络状态 ：没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
     * 自定义
     *
     * @return
     */
    public static int getNetWorkState(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        //NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        //否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            //3G   联通的3G为UMTS或HSDPA 电信的3G为EVDO
            if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 4;
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS
                    || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 3;
                //2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
            } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS
                    || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                    || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                    && !telephonyManager.isNetworkRoaming()) {
                netType = 2;
            } else {
                netType = 2;
            }
        }
        return netType;
    }

    public static boolean isNetworkConnected() {
        return getNetWorkState(getContext()) == 0 ? false : true;
    }

    public static int getScreenWidth() {
        return App.getInstance().getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight() {
        return App.getInstance().getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
            return "can_not_find_version_name";
        }
    }

    public static String image2base64(String path) {
        String img = null;
        File file = new File(path);
        if (!file.exists()) {
            return img;
        }
        /** 1.获取图片原始大小 */
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不把图片读到内存 依然可以读取图片的宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;
        int width = options.outWidth;
        /** 2.计算压缩比例 */
        //1表示不压缩 2表示压缩为原来的1/2
        int inSampleSize = 1;
        int reqHeight = 800;
        int reqWidth = 480;
        if (height > reqHeight || width > reqWidth) {
            int heightRadio = Math.round((float) height / (float) reqHeight);
            int widthRadio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRadio < widthRadio ? heightRadio : widthRadio;
        }
        /** 3.缩放并压缩图片 */
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        ByteArrayOutputStream bys = new ByteArrayOutputStream();
        int mOptions = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, mOptions, bys);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        while (bys.toByteArray().length / 1024 > 70) {
            //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            bys.reset();//重置baos即清空baos
            bitmap.compress(Bitmap.CompressFormat.JPEG, mOptions, bys);//这里压缩options%，把压缩后的数据存放到baos中
            mOptions -= 10;//每次都减少10
        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
//        return bitmap;
        byte[] bytes = bys.toByteArray();
        img = Base64.encodeToString(bytes, Base64.DEFAULT);
        //返回Base64后de字节码
        return img;
    }

    public static int getInt4Intent(AppCompatActivity activity, String extraName) {
        int result = -1;
        Intent intent = activity.getIntent();
        if (intent == null) return result;
        result = intent.getIntExtra(extraName, -1);
        return result;
    }

    public static String getStr4Intent(AppCompatActivity activity, String extraName) {
        String result = "未知";
        Intent intent = activity.getIntent();
        if (intent == null) return result;
        result = intent.getStringExtra(extraName);
        return result;
    }

    public static void close(Closeable obj) {
        if (obj != null) {
            try {
                obj.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
//     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
//     * @param scale
     *            （DisplayMetrics类中属性density）
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
//     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
//     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
