package xjw.app.hencoder.whyhttp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import xjw.app.hencoder.utils.ThreadUtils;

/**
 * Created by xjw on 2017/11/27 17:52
 * Email 1521975316@qq.com
 * <p>
 * TODO 异常处理机制
 * TODO 缓存机制
 * TODO 完善的API(请求头,参数,编码,拦截器等)与调试模式
 * TODO HTTPS
 */

public class WhyHttp {

    public interface OnResponse {
        void onSuccess(String result);
    }

    private WhyHttp() {

    }

    private static final int CODE_SUCCESS = 200;
    private static WhyHttp mInstence;

    public static WhyHttp create() {
        if (mInstence == null) {
            synchronized (WhyHttp.class) {
                if (mInstence == null) {
                    mInstence = new WhyHttp();
                }
            }
        }
        return mInstence;
    }

    public void get(final String url, final OnResponse response) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                String result = get(url);
                response.onSuccess(result);
            }
        });
    }

    public String get(String url) {
        String result = null;
        HttpURLConnection con = null;
        try {
            URL net = new URL(url);
            con = (HttpURLConnection) net.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(15 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoInput(true);

            int code = con.getResponseCode();
            if (code == CODE_SUCCESS) {
                InputStream in = con.getInputStream();
                result = getString4In(in);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return result;
    }

    public void post(final String url, final String data, final OnResponse response) {
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                String result = post(url, data);
                response.onSuccess(result);
            }
        });
    }

    public String post(String url, String data) {
        String result = null;
        HttpURLConnection con = null;
        try {
            URL net = new URL(url);
            con = (HttpURLConnection) net.openConnection();
            con.setRequestMethod("POST");
            con.setConnectTimeout(15 * 1000);
            con.setReadTimeout(15 * 1000);
            con.setDoOutput(true);
            con.setDoInput(true);

            OutputStream out = con.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            out.close();

            int code = con.getResponseCode();
            if (code == CODE_SUCCESS) {
                InputStream in = con.getInputStream();
                result = getString4In(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        return result;
    }

    private String getString4In(InputStream in) throws IOException {
        String result = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buff = new byte[1024 * 1];
        int len = 0;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        result = out.toString();
        in.close();
        out.close();
        return result;
    }


}
