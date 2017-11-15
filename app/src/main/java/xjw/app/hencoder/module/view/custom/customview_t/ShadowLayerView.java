package xjw.app.hencoder.module.view.custom.customview_t;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xjw on 2017/8/23.
 */

public class ShadowLayerView extends View {

    private Paint mPaint;
    private String textTest = "J神锋无影_XJW";
    private Path mPath;

    public ShadowLayerView(Context context) {
        this(context, null);
    }

    public ShadowLayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setTextSize(46);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setColor(Color.BLACK);

        mPaint.setShadowLayer(6, 3, 2, Color.RED);
        mPath = new Path();
        mPaint.getTextPath(textTest, 0, textTest.length(), 0, 0, mPath);
//        mPaint.getFillPath()
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawText(textTest, 0, 50, mPaint);
        canvas.translate(0, 100);
        mPaint.setStrokeWidth(2);
        canvas.drawPath(mPath, mPaint);


    }
}
