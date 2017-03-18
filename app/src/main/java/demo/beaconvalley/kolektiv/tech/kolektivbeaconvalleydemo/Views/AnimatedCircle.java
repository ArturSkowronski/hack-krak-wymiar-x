package demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.Views;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import demo.beaconvalley.kolektiv.tech.kolektivbeaconvalleydemo.R;


public class AnimatedCircle extends View {



    private float strokeWidth = 4;
    private float progress = 0;
    private int min = 0;
    private int max = 100;
    private int circleColor;
    private ObjectAnimator objectAnimator ;


    private int startAngle = -90;
    private int color = Color.DKGRAY;
    private RectF rectF;
    private Paint strokeBackGround;
    private Paint arcPaint;
    private Paint circlePaint;

    public AnimatedCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
        strokeBackGround.setStrokeWidth(strokeWidth);
        arcPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();//Because it should recalculate its bounds
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
        invalidate();
    }



    private void init(Context context, AttributeSet attrs) {
        rectF = new RectF();
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.AnimatedCircle,
                0, 0);
        try {
            strokeWidth = typedArray.getDimension(R.styleable.AnimatedCircle_strokeThickness, strokeWidth);
            progress = typedArray.getFloat(R.styleable.AnimatedCircle_strokeProgress, progress);
            color = typedArray.getColor(R.styleable.AnimatedCircle_strokeColor, color);
            min = typedArray.getInt(R.styleable.AnimatedCircle_min, min);
            max = typedArray.getInt(R.styleable.AnimatedCircle_max, max);
            circleColor =  typedArray.getColor(R.styleable.AnimatedCircle_circleColor, color);
        } finally {
            typedArray.recycle();
        }

        strokeBackGround = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokeBackGround.setColor(circleColor);
        strokeBackGround.setStyle(Paint.Style.STROKE);
        strokeBackGround.setStrokeWidth(strokeWidth);

        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(circleColor);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(strokeWidth);
       //
       // circlePaint.setShader(new LinearGradient(0, 0, 0, getHeight(), Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

        arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arcPaint.setColor(lightenColor(color,0.90f));
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(rectF.centerX(), rectF.centerY(),( rectF.width() + strokeWidth) / 2, circlePaint);
        canvas.drawOval(rectF, strokeBackGround);
        float angle = 360 * progress / max;
        canvas.drawArc(rectF, startAngle, angle, false, arcPaint);



    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        setMeasuredDimension(min, min);
        rectF.set(0 + strokeWidth / 2, 0 + strokeWidth / 2, min - strokeWidth / 2, min - strokeWidth / 2);
    }


    public int lightenColor(int color, float factor) {
        float r = Color.red(color) * factor;
        float g = Color.green(color) * factor;
        float b = Color.blue(color) * factor;
        int ir = Math.min(255, (int) r);
        int ig = Math.min(255, (int) g);
        int ib = Math.min(255, (int) b);
        int ia = Color.alpha(color);
        return (Color.argb(ia, ir, ig, ib));
    }


    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }



    public void  stopAnimation () {
        if(objectAnimator != null && objectAnimator.isRunning()){
            objectAnimator.end();
            setProgress(0);
        }
    }

    public void animateTo(float progress , long duration) {
        if(objectAnimator != null && objectAnimator.isRunning()){
            objectAnimator.end();
        }
        objectAnimator = ObjectAnimator.ofFloat(this, "progress", progress);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new DecelerateInterpolator());
        objectAnimator.start();
    }
}
