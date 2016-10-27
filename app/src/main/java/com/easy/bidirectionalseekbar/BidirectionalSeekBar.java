package com.easy.bidirectionalseekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
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

    public BidirectionalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context,attrs);
    }
    private int offset;
    private int withInColor,withOutColor;
    private int pb_height;
    private int leftSolidColor,rightSolidColor,leftStrokeColor,rightStrokeColor;
    private int ball_radius_size,ball_stroke_size;
    private int text_color,text_size;
    private int text_left_int,text_right_int;
    private String text_units;
    private TextPosition textPosition;
    private UnitsPosition unitPosition;
    private int ordinalTextPosition,ordinalUnitsPosition;
    private enum TextPosition{
        GONE, BELOW, ABOVE
    }
    private enum UnitsPosition{
        GONE,LEFT,RIGHT
    }

    private Paint withOutPaint;//整个背景画笔
    private Paint withInPaint;//选中范围画笔
    private Paint leftBallPaint;//左侧球
    private Paint rightBallPaint;//右侧球
    private Paint leftBallStrokePaint;//左侧球阴影
    private Paint rightBallStrokePaint;//右侧球阴影
    private Paint leftTextPaint,rightTextPaint;
    private RectF withOutRectF;//整个背景方块
    private RectF withInRectF;//选中背景方块
    private Rect leftTextRect;
    private Rect rightTextRect;

    private int ballY;//球Y轴
    private int leftBallX;//左侧球X轴
    private int rightBallX;//右侧球X轴
    private int textY;
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
        ball_radius_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_radius_size,30);
        ball_stroke_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_stroke_size,0);
        text_color=typedArray.getColor(R.styleable.BidirectionalSeekBar_text_color, Color.parseColor("#000000"));
        text_size=typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_text_size,0);
        text_units=typedArray.getString(R.styleable.BidirectionalSeekBar_text_units);
        typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_left_drawable,0);
        typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_right_drawable,0);
        textPosition=TextPosition.GONE;
        unitPosition=UnitsPosition.GONE;
        ordinalTextPosition = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_position,textPosition.ordinal());
        textPosition=TextPosition.values()[ordinalTextPosition];
        ordinalUnitsPosition = typedArray.getInt(R.styleable.BidirectionalSeekBar_unit_position,unitPosition.ordinal());
        unitPosition=UnitsPosition.values()[ordinalUnitsPosition];
        typedArray.recycle();

        currentMovingType = BallType.LEFT;
        leftTextPaint = createPaint(text_color, text_size, Paint.Style.FILL, 0);
        rightTextPaint = createPaint(text_color, text_size, Paint.Style.FILL, 0);
        withOutPaint = createPaint(withOutColor, 0, Paint.Style.FILL, 0);
        withInPaint = createPaint(withInColor, 0, Paint.Style.FILL, 0);
        leftBallPaint = createPaint(leftSolidColor, 0, Paint.Style.FILL, 0);
        rightBallPaint = createPaint(rightSolidColor, 0, Paint.Style.FILL, 0);
        leftBallStrokePaint = createPaint(leftStrokeColor, 0, Paint.Style.FILL, 0);
        leftBallStrokePaint.setShadowLayer(5, 2, 2, leftStrokeColor);
        rightBallStrokePaint = createPaint(rightStrokeColor, 0, Paint.Style.FILL, 0);
        rightBallStrokePaint.setShadowLayer(5, 2, 2, rightStrokeColor);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWithOut(canvas);
        drawWithIn(canvas);
        drawLeftCircle(canvas);
        drawRightCircle(canvas);

    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        leftTextRect = new Rect();
        leftTextPaint.getTextBounds(text_left_int+"", 0, text_left_int+"".length(), leftTextRect);
        rightTextRect = new Rect();
        rightTextPaint.getTextBounds(text_right_int+"", 0, text_right_int+"".length(), rightTextRect);
        withOutRectF = new RectF(ball_radius_size,ball_radius_size-pb_height/2.F,getMeasuredWidth()-ball_radius_size,ball_radius_size+pb_height/2.F);
        withInRectF = new RectF(ball_radius_size+50,ball_radius_size-pb_height/2.F,getMeasuredWidth()-ball_radius_size-50,ball_radius_size+pb_height/2.F);
        leftBallX=ball_radius_size;
        rightBallX=getMeasuredWidth()-ball_radius_size;
        ballY=ball_radius_size;
        if(MeasureSpec.getMode(heightMeasureSpec)==MeasureSpec.AT_MOST){//wrap
            int mHeight=resolveSize(Math.max(ball_radius_size*2,pb_height),heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(),mHeight);
        }

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

    private Paint createPaint(int paintColor, int textSize, Paint.Style style, int lineWidth) {
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
    private void drawWithOut(Canvas canvas) {
        canvas.drawRect(withOutRectF, withOutPaint);
    }
    private void drawWithIn(Canvas canvas) {
        canvas.drawRect(withInRectF, withInPaint);
    }
    private void drawRightCircle(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(rightBallX, ballY, ball_radius_size, rightBallStrokePaint);
        canvas.drawCircle(rightBallX, ballY, ball_radius_size - ball_stroke_size, rightBallPaint);
    }

    private void drawLeftCircle(Canvas canvas) {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        canvas.drawCircle(leftBallX,ballY, ball_radius_size, leftBallStrokePaint);
        canvas.drawCircle(leftBallX, ballY, ball_radius_size - ball_stroke_size, leftBallPaint);
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
