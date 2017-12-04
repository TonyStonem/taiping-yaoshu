package cn.xjw.tdex;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener tvClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //TODO 注释
            loadApk();


        }
    };

    private static final String M_SUI = "cn.xjw.mApp";
    private RelativeLayout rlMain;
    private TextView tv;
    private String apkPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rlMain = (RelativeLayout) findViewById(R.id.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        tv.setOnClickListener(tvClickListener);
        tDex("dex_toast");
        tPApk();
        apkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "tdexapk-plugin.apk";
        tDApk(apkPath, "dex_apkplugin", "test");
    }

    private void tDex(String dir) {
        File dexToast = getDir(dir, Context.MODE_PRIVATE);
        String jarPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator + "output.jar";
        DexClassLoader loader = new DexClassLoader(jarPath, dexToast.getAbsolutePath()
                , null, getClassLoader());
        try {
            Class<?> cls = loader.loadClass("cn.xjw.tdex.ShowToastImpl");
            Method showToast = cls.getDeclaredMethod("showToast",
                    Context.class, String.class);
            showToast.invoke(cls.newInstance(),
                    getApplication(), "喵喵!!!");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void tPApk() {
        List<PluginBean> pbs = findPlugin4SUI(M_SUI);
        if (pbs == null || pbs.isEmpty()) return;
        for (PluginBean pb :
                pbs) {
            try {
                Context context = createPackageContext(pb.pn,
                        CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
                int resID = dynamicLocalAPK("test_m", pb.pn, context);
                rlMain.setBackgroundDrawable(context.getResources().getDrawable(resID));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tDApk(String path, String dir, String fieldName) {
        try {
            String[] result = findUNAPKInfo(path);
            File f = getDir(dir, MODE_PRIVATE);
            DexClassLoader loader = new DexClassLoader(path, f.getAbsolutePath()
                    , null, ClassLoader.getSystemClassLoader());
            Class<?> cls = loader.loadClass(result[0] + ".R$mipmap");
            Drawable drawable = getUNAPKRes(result[0], path).getDrawable(
                    cls.getDeclaredField(fieldName).getInt(R.id.class));
            tv.setBackgroundDrawable(drawable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过 ShareUserID 获取所有的插件 apk 集合
     *
     * @param mSui ShareUserID
     * @return 插件 apk 集合
     */
    private List<PluginBean> findPlugin4SUI(String mSui) {
        List<PluginBean> result = null;
        PackageManager pm = getPackageManager();
        //查找所有已安装的 apk 文件
        List<PackageInfo> pis = pm
                .getInstalledPackages(PackageManager.MATCH_UNINSTALLED_PACKAGES);
        if (pis == null || pis.isEmpty()) return null;
        for (PackageInfo pi : pis) {
            String pn = pi.packageName;
            String sui = pi.sharedUserId;
            if (!TextUtils.isEmpty(sui) && sui.equals(mSui) &&
                    !TextUtils.isEmpty(pn) && !pn.equals(getPackageName())) {
                //插件 apk 的名
                String label = pm.getApplicationLabel(pi.applicationInfo).toString();
                PluginBean bean = new PluginBean(pn, label);
                if (result == null) {
                    result = new ArrayList<>();
                }
                result.add(bean);
            }
        }
        return result;
    }

    /**
     * 加载已安装的apk
     *
     * @param fieldName     字段名
     * @param pn            插件 apk 的包名
     * @param pluginContext 对应插件中的上下文,获取方式 : Context plugnContext =
     *                      this.createPackageContext(packageName,
     *                      CONTEXT_IGNORE_SECURITY | CONTEXT_INCLUDE_CODE);
     * @return 插件 apk 中的图片资源 ID
     * @throws Exception
     */
    private int dynamicLocalAPK(String fieldName, String pn, Context pluginContext) throws Exception {
        PathClassLoader pcLoader = new PathClassLoader(pluginContext.getPackageResourcePath()
                , ClassLoader.getSystemClassLoader());
        // 通过使用自身的加载器反射出mipmap类进而使用该类的功能
        // Class<?> clazz = pathClassLoader.loadClass(pn + ".R$mipmap");
        Class<?> cls = Class.forName(pn + ".R$mipmap", true, pcLoader);
        return cls.getDeclaredField(fieldName).getInt(R.mipmap.class);
    }

    /**
     * 获取未安装插件 apk 的信息
     *
     * @param path 插件 apk 路径
     * @return 插件 apk 的 packagename 和 label
     */
    private String[] findUNAPKInfo(String path) {
        PackageManager pm = getPackageManager();
        PackageInfo pi = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (pi != null) {
            ApplicationInfo ai = pi.applicationInfo;
            String[] result = new String[2];
            result[0] = ai.packageName;
            result[1] = pm.getApplicationLabel(ai).toString();
            return result;
        }
        return null;
    }

    /**
     * 获取一个未安装的插件 apk 的 Resources
     *
     * @param pn   插件 apk 的包名
     * @param path 插件 apk 的路径
     * @return 插件 apk 的 Resources
     */
    private Resources getUNAPKRes(String pn, String path) {
        try {
            AssetManager am = AssetManager.class.newInstance();
            Method m = am.getClass().getMethod("addAssetPath", String.class);
            m.invoke(am, path);
            Resources res = getResources();
            Resources mRes = new Resources(am, res.getDisplayMetrics(), res.getConfiguration());
            return mRes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void loadApk() {
        try {
            String[] info = findUNAPKInfo(apkPath);
            File dir = getDir("dex_apkload", MODE_PRIVATE);
            DexClassLoader loader = new DexClassLoader(apkPath, dir.getAbsolutePath(),
                    null, ClassLoader.getSystemClassLoader());
            PackageInfo pi = getPackageManager().getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (pi != null && pi.activities != null && pi.activities.length > 0) {
                Class<?> cls = loader.loadClass(pi.activities[0].name);
                Object instance = cls.newInstance();
                Method mSet = cls.getDeclaredMethod("setActivity", Activity.class);
                mSet.setAccessible(true);
                mSet.invoke(instance, this);
                Bundle bundle = new Bundle();
                bundle.putBoolean("mFlag", true);
                Method mOnCreate = cls.getDeclaredMethod("onCreate", Bundle.class);
                mOnCreate.setAccessible(true);
                mOnCreate.invoke(instance, bundle);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}

