package xjw.app.hencoder.module.view.custom.customview_t;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import xjw.app.hencoder.R;

/**
 * Created by xjw on 2017/8/23.
 */

public class ShaderLGView extends View {

    private Paint mPaint;

    public ShaderLGView(Context context) {
        this(context, null);
    }

    public ShaderLGView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.BLACK);
        LinearGradient lgShader = new/* 线性渐变 */
                LinearGradient(0, 0, 300, 300, Color.RED, Color.YELLOW,
//                Shader.TileMode.CLAMP,Shader.TileMode.REPEAT
                Shader.TileMode.MIRROR);

        RadialGradient rgShader = new /* 辐射渐变 */
                RadialGradient(150, 150, 150, Color.RED,
                Color.YELLOW, Shader.TileMode.CLAMP);

        SweepGradient sgShader = new /* 扫描渐变 */
                SweepGradient(150, 150, Color.RED, Color.YELLOW);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test_m);
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.exit);

        BitmapShader mapShader = new /* bitmap着色器 */
                BitmapShader(bitmap,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        BitmapShader mapShader2 = new /* bitmap着色器 */
                BitmapShader(bitmap2,
                Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        ComposeShader cShader = new /* 混合着色器 */
                ComposeShader(mapShader, mapShader2, PorterDuff.Mode.SRC_OVER);

        // 注意：上面这段代码中我使用了两个 BitmapShader
        // 来作为 ComposeShader() 的参数，而 ComposeShader()
        // 在硬件加速下是不支持两个相同类型的 Shader 的，
        // 所以这里也需要关闭硬件加速才能看到效果。

        mPaint.setShader(mapShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(150, 150, 150, mPaint);

    }
}
