package com.drawer.test.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.drawer.test.R;


public class DrawingView extends View {

    // Keeps track of the path of the drawing.
    private Path mDrawPath;

    // Keeps track of the color of the brush and canvas.
    private Paint mDrawPaint, mCanvasPaint;

    // Set the default color.
    private int mPaintColor = 0xFF70A65F;

    // Set the default color.
    private int mBackgroundColor = 0xFFF0F0F5;

    // The canvas to draw on.
    private Canvas mDrawCanvas;

    // Bitmap of the canvas
    private Bitmap mCanvasBitmap;

    // Keeping track of the brush size.
    private float mBrushSize, mLastBrushSize;

    // Print Background Color
    private boolean mBackgroundPrint;

    public DrawingView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
        setupDrawing();
    }

    private void setupDrawing() {
        /// <summary>
        /// This is used to setup the initial drawing/editing page.
        /// </summary>

        mDrawPath = new Path();
        mDrawPaint = new Paint();

        // Set the path to the default color.
        mDrawPaint.setColor(mPaintColor);

        // This is to smooth and round edges of the drawing.
        mDrawPaint.setAntiAlias(true);
        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeJoin(Paint.Join.ROUND);
        mDrawPaint.setStrokeCap(Paint.Cap.ROUND);
        mCanvasPaint = new Paint(Paint.DITHER_FLAG);

        // Set the default brush size to medium.
        mBrushSize = getResources().getInteger(R.integer.medium_size);
        mLastBrushSize = mBrushSize;
        mDrawPaint.setStrokeWidth(mBrushSize);
        mBackgroundPrint = true;
    }

    public void setBrushSize(float newSize) {
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize,
                getResources().getDisplayMetrics());
        mBrushSize = pixelAmount;
        mDrawPaint.setStrokeWidth(mBrushSize);
    }

    public void setLastBrushSize(float lastSize) {
        mLastBrushSize = lastSize;
    }

    public float getLastBrushSize() {
        return mLastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mDrawCanvas = new Canvas(mCanvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mBackgroundPrint) {
            canvas.drawColor(mBackgroundColor);
        }
        canvas.drawBitmap(mCanvasBitmap, 0, 0, mCanvasPaint);
        canvas.drawPath(mDrawPath, mDrawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /// <summary>
        /// Register the touch user actions to enable drawing.
        /// </summary>

        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mDrawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                mDrawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                mDrawCanvas.drawPath(mDrawPath, mDrawPaint);
                mDrawPath.reset();
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor) {
        mPaintColor = Color.parseColor(newColor);
        mDrawPaint.setColor(mPaintColor);
        invalidate();
    }

    public void setBackgroundColor(String newColor) {
        mBackgroundColor = Color.parseColor(newColor);
        invalidate();
    }

    public void setColor(int newColor) {
        mPaintColor = newColor;
        mDrawPaint.setColor(mPaintColor);
        invalidate();
    }

    public void setBackgroundColor(int newColor) {
        mBackgroundColor = newColor;
        invalidate();
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }


    public void setBackgroundPrint(boolean mBackgroundPrint) {
        this.mBackgroundPrint = mBackgroundPrint;
        this.invalidate();
    }


    public boolean isBackgroundPrint() {
        return mBackgroundPrint;
    }
}
