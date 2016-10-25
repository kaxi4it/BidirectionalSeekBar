package com.easy.bidirectionalseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Bada on 2016/10/25.
 */
public class BidirectionalSeekBar extends View{




    public BidirectionalSeekBar(Context context) {
        super(context);
    }

    public BidirectionalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context,attrs);

    }

    private int withInColor,withOutColor;
    private int pb_height;
    private int leftSolidColor,rightSolidColor,leftStrokeColor,rightStrokeColor;
    private int radius_size,stroke_size;
    private int text_color,text_size;
    private String text_units;
    private TextPosition textPosition;
    private UnitsPosition unitPosition;
    private enum TextPosition{
        GONE, BELOW, ABOVE
    }
    private enum UnitsPosition{
        LEFT,RIGHT
    }

    private Paint withOutPaint;//整个背景画笔
    private Paint withInPaint;//选中范围画笔
    private Paint leftBallPaint;//左侧球
    private Paint rightBallPaint;//右侧球
    private Paint leftBallStrokePaint;//左侧球阴影
    private Paint rightBallStrokePaint;//右侧球阴影
    private Paint textPaint;
    private RectF withOutRectF;//整个背景方块
    private RectF withInRectF;//选中背景方块

    private int BallY;//球Y轴
    private int leftBallX;//左侧球X轴
    private int rightBallX;//右侧球X轴
    private int TextY;
    private int currentMovingType;//移动类型
    private OnDragFinishedListener dragFinishedListener;
    private int downX;

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.BidirectionalSeekBar);
        withInColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_pb_within_color, Color.parseColor("#111111"));
        withOutColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_pb_without_color, Color.parseColor("#AAAAAA"));
        pb_height=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_pb_height,5);
        leftSolidColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_left_solid_color, Color.parseColor("#118811"));
        rightSolidColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_right_solid_color, Color.parseColor("#118811"));
        leftStrokeColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_left_stroke_color, Color.parseColor("#777777"));
        rightStrokeColor=typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_right_stroke_color, Color.parseColor("#777777"));
        radius_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_radius_size,30);
        stroke_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_stroke_size,0);
        text_color=typedArray.getColor(R.styleable.BidirectionalSeekBar_text_color, Color.parseColor("#000000"));
        text_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_text_size,sp2px(12));
        text_units=typedArray.getString(R.styleable.BidirectionalSeekBar_text_units);
        typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_left_drawable,0);
        typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_right_drawable,0);
        int ordinal = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_position,textPosition.ordinal());
        textPosition=TextPosition.values()[ordinal];
        int ordinal2 = typedArray.getInt(R.styleable.BidirectionalSeekBar_unit_position,unitPosition.ordinal());
        unitPosition=UnitsPosition.values()[ordinal2];
        typedArray.recycle();

        currentMovingType = BallType.LEFT;
        textPaint = creatPaint(text_color, text_size, Paint.Style.FILL, 0);
        withOutPaint = creatPaint(withOutColor, 0, Paint.Style.FILL, 0);
        withInPaint = creatPaint(withInColor, 0, Paint.Style.FILL, 0);
        leftBallPaint = creatPaint(leftSolidColor, 0, Paint.Style.FILL, 0);
        rightBallPaint = creatPaint(rightSolidColor, 0, Paint.Style.FILL, 0);
        leftBallStrokePaint = creatPaint(leftStrokeColor, 0, Paint.Style.FILL, 0);
        leftBallStrokePaint.setShadowLayer(stroke_size, 1, 1, leftStrokeColor);
        rightBallStrokePaint = creatPaint(rightStrokeColor, 0, Paint.Style.FILL, 0);
        rightBallStrokePaint.setShadowLayer(stroke_size, 1, 1, rightStrokeColor);

    }

    public BidirectionalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int px2sp(float pxValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
    private int sp2px(float spValue) {
        final float fontScale = getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public int dpToPx(int dp)
    {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
    public int pxToDp(int px)
    {
        return (int) (px / getResources().getDisplayMetrics().density);
    }

    private Paint creatPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
        Paint paint = new Paint();
        paint.setColor(paintColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(lineWidth);
        paint.setDither(true);
        paint.setTextSize(textSize);
        paint.setStyle(style);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        return paint;
    }

    private int getMovingLeftOrRight(int actionX) {
        return Math.abs(leftBallX - actionX) - Math.abs(rightBallX - actionX) > 0 ? BallType.RIGHT : BallType.LEFT;
    }

    private static class BallType {
        private static final int LEFT = 0x10;
        private static final int RIGHT = 0x11;
    }

    public interface OnDragFinishedListener {
        void dragFinished(int leftPostion, int rightPostion);
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        invalidate();
    }
}
