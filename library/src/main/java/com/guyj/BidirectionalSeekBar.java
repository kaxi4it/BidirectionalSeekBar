package com.guyj;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Bada on 2016/10/25.
 */
public class BidirectionalSeekBar extends View {

    public BidirectionalSeekBar(Context context) {
        super(context);
    }

    public BidirectionalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(context, attrs);
    }

    public BidirectionalSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private int offset;
    private int withInColor, withOutColor;
    private int pb_height;
    private int leftSolidColor, rightSolidColor, leftStrokeColor, rightStrokeColor;
    private int ball_radius_size, ball_stroke_size;
    private int text_color, text_size;
    private int text_left_num, text_right_num, text_min_unit, totalLength;
    private String text_units;
    private TextPosition textPosition;
    private UnitsPosition unitPosition;
    private int ordinalTextPosition, ordinalUnitsPosition;
    private int leftBitmapResID,rightBitmapResID;


    private enum TextPosition {
        GONE, BELOW, ABOVE
    }

    private enum UnitsPosition {
        GONE, LEFT, RIGHT
    }

    private Paint withOutPaint;//整个背景画笔
    private Paint withInPaint;//选中范围画笔
    private Paint leftBallPaint;//左侧球
    private Paint rightBallPaint;//右侧球
    private Paint leftBallStrokePaint;//左侧球阴影
    private Paint rightBallStrokePaint;//右侧球阴影
    private Paint leftTextPaint, rightTextPaint;
    private RectF withOutRectF;//整个背景方块
    private RectF withInRectF;//选中背景方块
    private Rect leftTextRect;
    private Rect rightTextRect;
    private Rect leftSrcRect,rightSrcRect,leftDstRect,rightDstRect;
    private Paint bitmapPaint;


    private int ballY;//球Y轴
    private int leftBallX;//左侧球X轴
    private int rightBallX;//右侧球X轴
    private int textY;
    private int currentMovingType;//移动类型
    private OnSeekBarChangeListener seekBarChangeListener;
    private int downX;
    private boolean isBitmap;

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BidirectionalSeekBar);
        withInColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_pb_within_color, Color.parseColor("#111111"));
        withOutColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_pb_without_color, Color.parseColor("#AAAAAA"));
        pb_height = typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_pb_height, 5);
        leftBitmapResID = typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_left_drawable,0);
        rightBitmapResID = typedArray.getResourceId(R.styleable.BidirectionalSeekBar_ball_right_drawable,0);
        if (leftBitmapResID!=0&&rightBitmapResID!=0){
            isBitmap=true;

        }else{
            leftSolidColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_left_solid_color, Color.parseColor("#118811"));
            rightSolidColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_right_solid_color, Color.parseColor("#118811"));
            leftStrokeColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_left_stroke_color, Color.parseColor("#777777"));
            rightStrokeColor = typedArray.getColor(R.styleable.BidirectionalSeekBar_ball_right_stroke_color, Color.parseColor("#777777"));
            ball_radius_size = typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_radius_size, 30);
            ball_stroke_size = typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_ball_stroke_size, 0);

        }
        text_left_num = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_left_num, 0);
        text_right_num = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_right_num, 100);
        text_min_unit = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_min_unit, 1);
//        text_color = typedArray.getColor(R.styleable.BidirectionalSeekBar_text_color, Color.parseColor("#000000"));
//        text_size = typedArray.getDimensionPixelOffset(R.styleable.BidirectionalSeekBar_text_size, 0);
//        text_units = typedArray.getString(R.styleable.BidirectionalSeekBar_text_units);
//        textPosition = TextPosition.GONE;
//        unitPosition = UnitsPosition.GONE;
//        ordinalTextPosition = typedArray.getInt(R.styleable.BidirectionalSeekBar_text_position, textPosition.ordinal());
//        textPosition = TextPosition.values()[ordinalTextPosition];
//        ordinalUnitsPosition = typedArray.getInt(R.styleable.BidirectionalSeekBar_unit_position, unitPosition.ordinal());
//        unitPosition = UnitsPosition.values()[ordinalUnitsPosition];
        typedArray.recycle();
        currentMovingType = MovingBall.LEFT;
        offset = 1;
//        leftTextPaint = createPaint(text_color, text_size, Paint.Style.FILL, 0);
//        rightTextPaint = createPaint(text_color, text_size, Paint.Style.FILL, 0);
        withOutPaint = createPaint(withOutColor, 0, Paint.Style.FILL, 0);
        withInPaint = createPaint(withInColor, 0, Paint.Style.FILL, 0);
        if (!isBitmap){
            leftBallPaint = createPaint(leftSolidColor, 0, Paint.Style.FILL, 0);
            rightBallPaint = createPaint(rightSolidColor, 0, Paint.Style.FILL, 0);
            leftBallStrokePaint = createPaint(leftStrokeColor, 0, Paint.Style.FILL, 0);
            leftBallStrokePaint.setShadowLayer(offset, offset, offset, Color.parseColor("#777777"));
            rightBallStrokePaint = createPaint(rightStrokeColor, 0, Paint.Style.FILL, 0);
            rightBallStrokePaint.setShadowLayer(offset, offset, offset, Color.parseColor("#777777"));
        }else{
            bitmapPaint = createBitmapPaint();
        }
    }

    private Paint createBitmapPaint() {
        Paint mPaint=new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setDither(true);
        return mPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawWithOut(canvas);
        drawWithIn(canvas);
        drawLeftCircle(canvas);
        drawRightCircle(canvas);

    }
    private Bitmap leftBitmap,rightBitmap;
    private int leftBitmapL,leftBitmapT,leftBitmapR,leftBitmapB;
    private int rightBitmapL,rightBitmapT,rightBitmapR,rightBitmapB;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isBitmap){
            leftBitmap= BitmapFactory.decodeResource(getResources(),leftBitmapResID);
            rightBitmap= BitmapFactory.decodeResource(getResources(),rightBitmapResID);
            leftSrcRect= new Rect(0,0,leftBitmap.getWidth(),leftBitmap.getHeight());
            rightSrcRect= new Rect(0,0,rightBitmap.getWidth(),rightBitmap.getHeight());
            leftDstRect= new Rect(0,0,leftBitmap.getWidth(),leftBitmap.getHeight());
            rightDstRect= new Rect(getMeasuredWidth()-rightBitmap.getWidth(),0,getMeasuredWidth(),rightBitmap.getHeight());
            leftBitmapL=0;
            leftBitmapR=leftBitmap.getWidth();
            leftBitmapT=rightBitmapT=0;
            leftBitmapB=rightBitmapB=leftBitmap.getHeight();
            rightBitmapL=getMeasuredWidth()-rightBitmap.getWidth();
            rightBitmapR=getMeasuredWidth();
            withOutRectF = new RectF(leftBitmapL+leftBitmap.getWidth()/2, leftBitmap.getHeight()/2 - pb_height / 2.F, getMeasuredWidth()-rightBitmap.getWidth()/2, leftBitmap.getHeight()/2 + pb_height / 2.F);
            setWithInRectF();
            totalLength = getMeasuredWidth() - leftBitmap.getWidth()/2-rightBitmap.getWidth()/2;
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {//wrap
                int mHeight = resolveSize(Math.max(leftBitmap.getHeight(), pb_height ), heightMeasureSpec);
                setMeasuredDimension(getMeasuredWidth(), mHeight);
            }
        }else {
            leftBallX = ball_radius_size;
            rightBallX = getMeasuredWidth() - ball_radius_size - offset;
            ballY = ball_radius_size;
//            leftTextRect = new Rect();
//            leftTextPaint.getTextBounds(text_left_num + "", 0, (text_left_num + "").length(), leftTextRect);
//            rightTextRect = new Rect();
//            rightTextPaint.getTextBounds(text_right_num + "", 0, (text_right_num + "").length(), rightTextRect);
            withOutRectF = new RectF(ball_radius_size, ball_radius_size - pb_height / 2.F, getMeasuredWidth() - ball_radius_size, ball_radius_size + pb_height / 2.F);
            setWithInRectF();
            totalLength = getMeasuredWidth() - ball_radius_size * 2;
            if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {//wrap
                int mHeight = resolveSize(Math.max(ball_radius_size * 2 + offset, pb_height + offset), heightMeasureSpec);
                setMeasuredDimension(getMeasuredWidth(), mHeight);
            }
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

    public int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    public int pxToDp(int px) {
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
        if (isBitmap){
            canvas.drawBitmap(rightBitmap,rightSrcRect,rightDstRect,bitmapPaint);
        }else{
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            canvas.drawCircle(rightBallX, ballY, ball_radius_size, rightBallStrokePaint);
            canvas.drawCircle(rightBallX, ballY, ball_radius_size - ball_stroke_size, rightBallPaint);
        }

    }

    private void drawLeftCircle(Canvas canvas) {
        if (isBitmap){
            canvas.drawBitmap(leftBitmap,leftSrcRect,leftDstRect,bitmapPaint);
        }else {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
            canvas.drawCircle(leftBallX, ballY, ball_radius_size, leftBallStrokePaint);
            canvas.drawCircle(leftBallX, ballY, ball_radius_size - ball_stroke_size, leftBallPaint);
        }
    }

    private int lastX;
    int leftProgress;
    int rightProgress;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                currentMovingType = getMovingLeftOrRight(downX);
                if (MovingBall.LEFT == currentMovingType) {
                    if(isBitmap){
                        leftBitmapL=downX<leftBitmap.getWidth()/2?0:downX-leftBitmap.getWidth()/2;
                        leftBitmapR=downX<leftBitmap.getWidth()/2?leftBitmap.getWidth()/2:downX+leftBitmap.getWidth()/2;
                        leftDstRect.set(leftBitmapL,leftBitmapT,leftBitmapR,leftBitmapB);
                    }else{
                        leftBallX = downX < ball_radius_size ? ball_radius_size : downX;
                    }
                } else if (MovingBall.RIGHT == currentMovingType) {
                    if (isBitmap){
                        rightBitmapL=downX> getMeasuredWidth()-rightBitmap.getWidth()/2?getMeasuredWidth()-rightBitmap.getWidth()/2:downX-rightBitmap.getWidth()/2;
                        rightBitmapR=downX>getMeasuredWidth()-rightBitmap.getWidth()/2?getMeasuredWidth():downX+rightBitmap.getWidth()/2;
                        rightDstRect.set(rightBitmapL,rightBitmapT,rightBitmapR,rightBitmapB);
                    }else {
                        rightBallX = downX > getMeasuredWidth() - ball_radius_size ? getMeasuredWidth() - ball_radius_size - offset : downX;
                    }
                }
//                setWithInRectF();
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                boolean flag;
                if (isBitmap){
                    flag=leftBitmapL+leftBitmap.getWidth()/2==rightBitmapL+rightBitmap.getWidth()/2;
                }else{
                    flag=leftBallX == rightBallX;
                }
                if (flag) {
                    if (moveX - lastX > 0) {
                        currentMovingType = MovingBall.RIGHT;
                        if(isBitmap){
                            rightBitmapL=moveX-rightBitmap.getWidth()/2;
                            rightBitmapR=moveX+rightBitmap.getWidth()/2;
                            rightDstRect.set(rightBitmapL,rightBitmapT,rightBitmapR,rightBitmapB);
                        }else{rightBallX = moveX;}

                        lastX = moveX;
                    } else {
                        currentMovingType = MovingBall.LEFT;
                        if(isBitmap){
                            leftBitmapL=moveX-leftBitmap.getWidth()/2;
                            leftBitmapR=moveX+leftBitmap.getWidth()/2;
                            leftDstRect.set(leftBitmapL,leftBitmapT,leftBitmapR,leftBitmapB);
                        }else{leftBallX = moveX;}
                        lastX = moveX;
                    }
                } else {
                    lastX = moveX;
                    if (MovingBall.LEFT == currentMovingType) {
                        if(isBitmap){
                            leftBitmapL=leftBitmapL-rightBitmapL>=0?rightBitmapL:moveX<leftBitmap.getWidth()/2?0:moveX-leftBitmap.getWidth()/2;
                            leftBitmapR=leftBitmapL-rightBitmapL>=0?rightBitmapR:moveX<leftBitmap.getWidth()/2?leftBitmap.getWidth():moveX+leftBitmap.getWidth()/2;

                        }else{
                            leftBallX = leftBallX - rightBallX >= 0 ? rightBallX : moveX < ball_radius_size ? ball_radius_size : moveX;
                        }
                    } else if (MovingBall.RIGHT == currentMovingType) {
                        if (isBitmap){
                            rightBitmapL=leftBitmapL-rightBitmapL>=0?leftBitmapL:moveX>getMeasuredWidth()-rightBitmap.getWidth()/2?getMeasuredWidth()-rightBitmap.getWidth():moveX-rightBitmap.getWidth()/2;
                            rightBitmapR=leftBitmapL-rightBitmapL>=0?leftBitmapR:moveX>getMeasuredWidth()-rightBitmap.getWidth()/2?getMeasuredWidth():moveX+rightBitmap.getWidth()/2;

                        }else{
                            rightBallX = rightBallX - leftBallX <= 0 ? leftBallX : moveX > getMeasuredWidth() - ball_radius_size ? getMeasuredWidth() - ball_radius_size - offset : moveX;
                        }
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        if (isBitmap){
            if (leftBitmapL<=0){
                leftBitmapL=0;
                leftBitmapR=leftBitmap.getWidth();
            }
            if (rightBitmapL>getMeasuredWidth()-rightBitmap.getWidth()){
                rightBitmapL=getMeasuredWidth()-rightBitmap.getWidth();
                rightBitmapR=getMeasuredWidth();
            }
            if (leftBitmapL>getMeasuredWidth()-leftBitmap.getWidth()){
                leftBitmapL=getMeasuredWidth()-leftBitmap.getWidth();
                leftBitmapR=getMeasuredWidth();
            }
            if (rightBitmapL<=0){
                rightBitmapL=0;
                rightBitmapR=rightBitmap.getWidth();
            }
            leftDstRect.set(leftBitmapL,leftBitmapT,leftBitmapR,leftBitmapB);
            rightDstRect.set(rightBitmapL,rightBitmapT,rightBitmapR,rightBitmapB);
        }else{
            if (leftBallX<ball_radius_size){
                leftBallX=ball_radius_size;
            }
            if (rightBallX<ball_radius_size){
                rightBallX=ball_radius_size;
            }
            if (leftBallX>getMeasuredWidth() - ball_radius_size){
                leftBallX=getMeasuredWidth() - ball_radius_size;
            }
            if (rightBallX>getMeasuredWidth() - ball_radius_size){
                rightBallX=getMeasuredWidth() - ball_radius_size;
            }
        }
        setWithInRectF();
        if (isBitmap){
            leftProgress = getProgressNum(leftBitmapL+leftBitmap.getWidth()/2);
            rightProgress = getProgressNum(rightBitmapL+rightBitmap.getWidth()/2);
        }else {
            leftProgress = getProgressNum(leftBallX);
            rightProgress = getProgressNum(rightBallX);
        }

        if (null != seekBarChangeListener) {
            seekBarChangeListener.onProgressChanged(leftProgress, rightProgress);
        }
        invalidate();
        return true;
    }

    private void setWithInRectF() {
        if (isBitmap){
            withInRectF = new RectF(leftBitmapL+leftBitmap.getWidth()/2, leftBitmap.getHeight()/2 - pb_height / 2.F, rightBitmapL+rightBitmap.getWidth()/2, leftBitmap.getHeight()/2 + pb_height / 2.F);
        }else{
            withInRectF = new RectF(leftBallX, ball_radius_size - pb_height / 2.F, rightBallX, ball_radius_size + pb_height / 2.F);
        }
    }

    private int getProgressNum(int ballX) {
        int d;
        if (isBitmap){
            d=leftBitmap.getWidth()/2;
        }else{
            d=ball_radius_size;
        }
        int x = ballX - d;
        float a = (text_right_num - text_left_num) / text_min_unit;
        float b = totalLength / a;
        int c = Math.round(x / b);
        return c * text_min_unit + text_left_num;
    }

    private int getMovingLeftOrRight(int actionX) {
        if (isBitmap){
            return Math.abs(leftBitmapL - actionX) - Math.abs(rightBitmapR - actionX) > 0 ? MovingBall.RIGHT : MovingBall.LEFT;
        }else{
            return Math.abs(leftBallX - actionX) - Math.abs(rightBallX - actionX) > 0 ? MovingBall.RIGHT : MovingBall.LEFT;
        }
    }

    private static class MovingBall {
        private static final int LEFT = 0x10;
        private static final int RIGHT = 0x11;
    }

    public interface OnSeekBarChangeListener {
        void onProgressChanged(int leftProgress, int rightProgress);
    }

    public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
        seekBarChangeListener = listener;
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        invalidate();
    }
}
