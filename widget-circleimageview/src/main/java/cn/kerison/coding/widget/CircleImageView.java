package cn.kerison.coding.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CircleImageView extends ImageView {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private static final int DEFAULT_BORDER_WIDTH = 0;
    private static final int DEFAULT_BORDER_COLOR = Color.BLACK;
    private static final boolean DEFAULT_BORDER_OVERLAY = false;

    private final RectF mDrawableRect = new RectF();
    private final RectF mBorderRect = new RectF();

    private final Matrix mShaderMatrix = new Matrix();
    private final Paint mBitmapPaint = new Paint();
    private final Paint mBorderPaint = new Paint();

    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private int mBorderWidth = DEFAULT_BORDER_WIDTH;
    private boolean mBorderOverlay = DEFAULT_BORDER_OVERLAY;

    private Bitmap mBitmap;
    private BitmapShader mBitmapShader;

    private ColorFilter mColorFilter;

    public CircleImageView(Context context) {
        this(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyle, 0);

            mBorderWidth = a.getDimensionPixelSize(R.styleable.CircleImageView_civ_border_width, DEFAULT_BORDER_WIDTH);
            mBorderColor = a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR);
            mBorderOverlay = a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY);

            a.recycle();
        }

        super.setScaleType(ScaleType.CENTER_CROP);
        refreshConfig();
    }

    @Override
    public ScaleType getScaleType() {
        return ScaleType.CENTER_CROP;
    }

    @Override
    public void setScaleType(ScaleType scaleType) {
        if (scaleType != ScaleType.CENTER_CROP) {
            throw new IllegalArgumentException(String.format("ScaleType %s not supported.", scaleType));
        }
    }

    @Override
    public void setAdjustViewBounds(boolean adjustViewBounds) {
        if (adjustViewBounds) {
            throw new IllegalArgumentException("adjustViewBounds not supported.");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isInEditMode()) {
            return;
        }

        if (mBitmap == null) {
            return;
        }

        canvas.drawCircle(mDrawableRect.centerX(), mDrawableRect.centerY(), mDrawableRect.width() / 2, mBitmapPaint);
        if (mBorderWidth > 0) {
            canvas.drawCircle(mBorderRect.centerX(), mBorderRect.centerY(), (mBorderRect.width() - mBorderWidth) / 2, mBorderPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        refreshConfig();
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        refreshConfig();
    }

    @Override
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        refreshConfig();
    }

    /**
     * 获取边色
     * @return
     */
    public int getBorderColor() {
        return mBorderColor;
    }

    /**
     * 设置边色
     * @param borderColor
     */
    public void setBorderColor(@ColorInt int borderColor) {
        if (borderColor != mBorderColor) {
            mBorderColor = borderColor;
            mBorderPaint.setColor(mBorderColor);
            invalidate();
        }
    }

    /**
     * 获取边宽
     * @return
     */
    public int getBorderWidth() {
        return mBorderWidth;
    }

    /**
     * 设置边宽
     * @param borderWidth
     */
    public void setBorderWidth(int borderWidth) {
        if (borderWidth != mBorderWidth) {
            mBorderWidth = borderWidth;
            refreshConfig();
        }
    }

    /**
     * 是否是覆盖型边框
     * @return
     */
    public boolean isBorderOverlay() {
        return mBorderOverlay;
    }

    /**
     * 设置边类型
     * @param borderOverlay true 覆盖型  false 压缩型
     */
    public void setBorderOverlay(boolean borderOverlay) {
        if (borderOverlay != mBorderOverlay) {
            mBorderOverlay = borderOverlay;
            refreshConfig();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        onBitmapChanged();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        onBitmapChanged();
    }

    @Override
    public void setImageResource(@DrawableRes int resId) {
        super.setImageResource(resId);
        onBitmapChanged();
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        onBitmapChanged();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter) {
            return;
        }

        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    @Override
    public ColorFilter getColorFilter() {
        return mColorFilter;
    }


    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;

            if (drawable instanceof ColorDrawable) {
                //生成一个2*2的小图
                bitmap = Bitmap.createBitmap(2, 2, BITMAP_CONFIG);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void onBitmapChanged() {
        mBitmap = getBitmapFromDrawable(getDrawable());
        refreshConfig();
    }

    private synchronized void refreshConfig() {

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }

        if (mBitmap == null) {
            invalidate();
            return;
        }

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);

        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setShader(mBitmapShader);

        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setStrokeWidth(mBorderWidth);

        mBorderRect.set(calculateBounds());
        mDrawableRect.set(mBorderRect);
        if (!mBorderOverlay && mBorderWidth > 0) {
            mDrawableRect.inset(mBorderWidth - 1.0f, mBorderWidth - 1.0f);
        }

        updateShaderMatrix(mBitmap.getWidth(), mBitmap.getHeight());
        invalidate();
    }

    /**
     * 计算实际内容范围
     */
    private RectF calculateBounds() {
        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        int sideLength = Math.min(availableWidth, availableHeight);

        float left = getPaddingLeft() + (availableWidth - sideLength) / 2f;
        float top = getPaddingTop() + (availableHeight - sideLength) / 2f;

        return new RectF(left, top, left + sideLength, top + sideLength);
    }

    /**
     * 计算Shader的缩放情况
     */
    private void updateShaderMatrix(int bmWidth, int bmHeight) {
        float scale;
        float dx = 0;
        float dy = 0;

        mShaderMatrix.reset();

        if (bmWidth > bmHeight) {
            scale = mDrawableRect.height() / (float) bmHeight;
            dx = (mDrawableRect.width() - bmWidth * scale) * 0.5f;
        } else {
            scale = mDrawableRect.width() / (float) bmWidth;
            dy = (mDrawableRect.height() - bmHeight * scale) * 0.5f;
        }

        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate((int) (dx + 0.5f) + mDrawableRect.left, (int) (dy + 0.5f) + mDrawableRect.top);

        mBitmapShader.setLocalMatrix(mShaderMatrix);
    }

}
