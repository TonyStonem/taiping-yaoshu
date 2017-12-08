package xjw.app.hencoder.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import xjw.app.hencoder.BuildConfig;
import xjw.app.hencoder.R;

/**
 * Created by xjw on 2017/12/8 10:24
 * Email 1521975316@qq.com
 */

public class JHttpManager {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public static abstract class OnResponse {

        private final Class<?> cls;

        public OnResponse(Class<?> cls) {
            this.cls = cls;
        }

        abstract void onSuccess(Object obj);

        void onStart() {
        }

        ;

        void onEnd() {
        }

        ;

        abstract void onFailure(String s);
    }

    private static JHttpManager ins;
    private static OkHttpClient client;

    private static final int M_GET = 0;
    private static final int M_POST = 1;

    public static String BASE_URL;
    private Dialog dia;
    private View loadDialogContentView;

    private JHttpManager() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static JHttpManager open() {
        if (ins == null) {
            synchronized (JHttpManager.class) {
                if (ins == null) {
                    ins = new JHttpManager();
                }
            }
        }
        return ins;
    }

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, String baseUrl) {
        BASE_URL = baseUrl;
        createDiaView(context);
    }

    private void createDiaView(Context context) {
        loadDialogContentView = LayoutInflater
                .from(context).inflate(R.layout.layout_load_dialog, null);
        ProgressBar progressBar = (ProgressBar) loadDialogContentView
                .findViewById(R.id.progress_load_dialog);
        int progressColor = ContextCompat.getColor(context, R.color.colorPrimary);
        progressBar.getIndeterminateDrawable()
                .setColorFilter(progressColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void get(String url, AppCompatActivity tag, OnResponse callBack) {
        get(url, false, tag, callBack);
    }

    public void get(String url, boolean showDia, AppCompatActivity context, final OnResponse callBack) {
        callBack.onStart();
        Request req = buildRequest(M_GET, url, showDia, context, null);
        doRequest(req, callBack);
    }

    private String getUrl(boolean b, String url) {
        return !b ? url : BASE_URL + url;
    }

    private boolean checkParams(String url, boolean showDia, AppCompatActivity context) {
        boolean b = checkUrl(url);
        if (!b) {
            throw new IllegalArgumentException(" ==> base url is exist( JHttpManager.open().init(String) ). <== ");
        }
        if (showDia) {
            showLoadDia(context);
        }
        return b;
    }

    private void showLoadDia(AppCompatActivity context) {
        if (dia != null) {
            dia.cancel();
            dia = null;
        }
        if (loadDialogContentView == null) {
            createDiaView(context);
        }
        dia = new Dialog(context, R.style.LoadDia);
        dia.setCanceledOnTouchOutside(BuildConfig.DEBUG);
        dia.setCancelable(true);
        dia.setContentView(loadDialogContentView);
        dia.show();
    }

    private void cancelLoadDia() {
        if (dia != null) {
            dia.cancel();
        }
    }

    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(BASE_URL)) {
            return url.startsWith("http://");
        } else {
            return true;
        }
    }

    public void postJson(String url, String json, boolean show, AppCompatActivity context, OnResponse callBack) {
        boolean b = checkParams(url, show, context);
        RequestBody body = RequestBody.create(MediaType.parse(""), json);
        Request req = buildRequest(M_POST, url, show, context, body);
        doRequest(req, callBack);
    }

    public void postMap(String url, Map<String, String> params, boolean show, AppCompatActivity context, OnResponse callBack) {
        boolean b = checkParams(url, show, context);
        FormBody.Builder body = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> e :
                    params.entrySet()) {
                body.add(e.getKey(), e.getValue());
            }
        }
        Request req = buildRequest(M_POST, url, show, context, body.build());
        doRequest(req, callBack);
    }

    private Request buildRequest(int m, String url, boolean showDia
            , AppCompatActivity context, RequestBody body) {
        Request.Builder builder = new Request.Builder();
        boolean b = checkParams(url, showDia, context);
        builder.url(getUrl(b, url));
        builder.tag(context.getClass().getName());
        if (m == M_GET) {
            builder.get();
        } else if (m == M_POST) {
            builder.post(body);
        }
        return builder.build();
    }

    private void doRequest(Request req, final OnResponse callBack) {
        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onFailure("error : " + e.getMessage());
                        callBack.onEnd();
                        cancelLoadDia();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                final Object obj = new Gson().fromJson(result, callBack.cls);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onSuccess(obj);
                        callBack.onEnd();
                        cancelLoadDia();
                    }
                });

            }
        });
    }

    public void cancel() {
        cancel(null);
    }

    public void cancel(Object tag) {
        if (dia != null) {
            dia.cancel();
        }
        if (tag == null) {
            client.dispatcher().cancelAll();
        }
        List<Call> calls = client.dispatcher().queuedCalls();
        for (int i = calls.size() - 1; i >= 0; i--) {
            Call call = calls.get(i);
            if (call.request().tag().equals(tag.getClass().getName())) {
                call.cancel();
            }
        }
    }

}
