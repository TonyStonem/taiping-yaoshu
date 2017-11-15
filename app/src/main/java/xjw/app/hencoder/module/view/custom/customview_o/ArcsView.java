package xjw.app.hencoder.module.view.custom.customview_o;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by xjw on 2017/8/16.
 */

public class ArcsView extends View {

    private Paint mPaint;
    private Path mPath;

    public ArcsView(Context context) {
        super(context);
        init();
    }

    public ArcsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.GREEN);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPath = new Path();
        mPath.addArc(150, 150, 350, 350, 135, 225);
        mPath.arcTo(350, 150, 550, 350, 180, 225, false);
        mPath.lineTo(350, 500);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawPath(mPath, mPaint);
    }
}
