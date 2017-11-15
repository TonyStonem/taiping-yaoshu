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

public class RectsView extends View {

    private Paint mPaint;
    private Path mPath;

    private String[] strs = {"Java", "AD", "KT", "C", "Py"};
    private float mTextX = 0;
    private float mTextY = 525f;

    public RectsView(Context context) {
        this(context, null);
        init();
    }

    public RectsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(2);
        mPaint.setStrokeCap(Paint.Cap.BUTT);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(20);

        mPath = new Path();
        mPath.moveTo(100, 500);
        mPath.addRect(100, 100, 150, 498, Path.Direction.CCW);

        mPath.moveTo(200, 500);
        mPath.addRect(200, 350, 250, 498, Path.Direction.CCW);

        mPath.moveTo(300, 500);
        mPath.addRect(300, 400, 350, 498, Path.Direction.CCW);

        mPath.moveTo(400, 500);
        mPath.addRect(400, 498, 450, 498, Path.Direction.CCW);

        mPath.moveTo(500, 500);
        mPath.addRect(500, 200, 550, 498, Path.Direction.CCW);
        mPath.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.GRAY);
        canvas.drawLine(50, 25, 50, 500, mPaint);
        canvas.drawLine(50, 500, 650, 500, mPaint);
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawPath(mPath, mPaint);

        mTextX = 0;
        for (int i = 0; i < strs.length; i++) {
            mTextX += 100f;
            canvas.drawText(strs[i], mTextX, mTextY, mPaint);
            System.out.println("m > " + mTextX);
        }

    }
}
