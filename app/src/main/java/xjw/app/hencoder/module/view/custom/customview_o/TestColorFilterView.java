package xjw.app.hencoder.module.view.custom.customview_o;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

import xjw.app.hencoder.R;

/**
 * Created by xjw on 2017/8/16.
 */

public class TestColorFilterView extends View {

    private Paint mPaint;
    private Bitmap bitmap;

    public TestColorFilterView(Context context) {
        this(context, null);
    }

    public TestColorFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        LightingColorFilter filter = new LightingColorFilter(0x00ffff, 0x000000);
        mPaint.setColorFilter(filter);
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap, 0, 0, mPaint);
    }
}
