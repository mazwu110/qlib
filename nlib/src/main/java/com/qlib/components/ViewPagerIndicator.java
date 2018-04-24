package com.qlib.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.qlib.R;

public class ViewPagerIndicator extends View {
	private int mCount;
	private int bmWidth;
	private float mSpace, mRadius;
	private Bitmap mNormalBm, mSeletedBm;
	private int mStyle = 0; // 0：精品效果图样式， 1：应用详情样式 没选中空心 >1:应用详情样式 没选中实心
	private int mSeleted = 0;
	private int mSeletedColor;
	private int mNormalColor;

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		// mNormalBm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.ic_indicator_normal);
		// mSeletedBm = BitmapFactory.decodeResource(context.getResources(),
		// R.drawable.ic_indicator_selected);
		mNormalBm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_dot_unsel2);
		mSeletedBm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.ic_dot_sel);
		// 选择 0 模式可以使用图片圆
		bmWidth = mNormalBm.getWidth();
		mRadius = mNormalBm.getHeight() / 2;
		mCount = 1;
		mSpace = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9,
				getContext().getResources().getDisplayMetrics());
	}

	public void setCount(int count) {
		this.mCount = count;
		mSeleted = 0;
		invalidate();
	}

	public int getCount() {
		return mCount;
	}

	public void next() {
		if (mSeleted < mCount - 1)
			mSeleted++;
		else
			mSeleted = 0;
		invalidate();
	}

	public void previous() {
		if (mSeleted > 0)
			mSeleted--;
		else
			mSeleted = mCount - 1;
		invalidate();
	}

	public int getSeletion() {
		return mSeleted;
	}

	public void setSeletion(int seleted) {
		seleted = Math.min(seleted, mCount - 1);
		seleted = Math.max(seleted, 0);
		this.mSeleted = seleted;
		invalidate();
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Paint paint = new Paint();
		paint.setAntiAlias(true);

		float width = (getWidth() - ((mRadius * 2 * mCount) + (mSpace * (mCount - 1)))) / 2.f;

		for (int i = 0; i < mCount; i++) {
			float left = width + getPaddingLeft() + i
					* (mSpace + mRadius + mRadius);
			float top = getPaddingTop();
			if (mStyle == 0) {
				if (i == mSeleted) {
					canvas.drawBitmap(mSeletedBm, left, top, paint);
				} else {
					canvas.drawBitmap(mNormalBm, left, top, paint);
				}
			} else {
				if (i == mSeleted) {
					paint.setColor(mSeletedColor);
				} else {
					paint.setColor(mNormalColor);
				}
				canvas.drawCircle(width + getPaddingLeft() + mRadius + i
						* (mSpace + mRadius + mRadius), getHeight() / 2,
						mRadius, paint);
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(measureWidth(widthMeasureSpec),
				measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (getPaddingLeft() + getPaddingRight()
					+ (mCount * bmWidth) + (mCount - 1) * mSpace + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
		if (specMode == MeasureSpec.EXACTLY) {
			result = specSize;
		} else {
			result = (int) (2 * mRadius + getPaddingTop() + getPaddingBottom() + 1);
			if (specMode == MeasureSpec.AT_MOST) {
				result = Math.min(result, specSize);
			}
		}
		return result;
	}

}
