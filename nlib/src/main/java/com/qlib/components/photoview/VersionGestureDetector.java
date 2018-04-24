package com.qlib.components.photoview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;

public abstract class VersionGestureDetector {
	OnGestureListener mListener;

	public static VersionGestureDetector newInstance(Context context,
			OnGestureListener listener) {
		final int sdkVersion = Build.VERSION.SDK_INT;
		VersionGestureDetector detector = null;

		if (sdkVersion < Build.VERSION_CODES.ECLAIR) {
			detector = new CupcakeDetector(context);
		} else if (sdkVersion < Build.VERSION_CODES.FROYO) {
			detector = new EclairDetector(context);
		} else {
			detector = new FroyoDetector(context);
		}

		detector.mListener = listener;

		return detector;
	}

	public abstract boolean onTouchEvent(MotionEvent ev);

	public abstract boolean isScaling();

	private static class CupcakeDetector extends VersionGestureDetector {
		float mLastTouchX;
		float mLastTouchY;
		/** 最小滑动标志距离 */
		final float mTouchSlop;
		/** 最小甩动标志距离 */
		final float mMinimumVelocity;

		private VelocityTracker mVelocityTracker;
		/** 拖动标志 */
		private boolean mIsDragging;

		public CupcakeDetector(Context context) {
			final ViewConfiguration configuration = ViewConfiguration
					.get(context);
			mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
			mTouchSlop = configuration.getScaledTouchSlop();
		}

		float getActiveX(MotionEvent ev) {
			return ev.getX();
		}

		float getActiveY(MotionEvent ev) {
			return ev.getY();
		}

		public boolean isScaling() {
			return false;
		}

		@SuppressLint("FloatMath")
		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN: {
				// 获取跟踪触摸屏事件的实例
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(ev);

				mLastTouchX = getActiveX(ev);
				mLastTouchY = getActiveY(ev);
				mIsDragging = false;
				break;
			}

			case MotionEvent.ACTION_MOVE: {
				final float x = getActiveX(ev);
				final float y = getActiveY(ev);
				final float dx = x - mLastTouchX;
				final float dy = y - mLastTouchY;

				if (!mIsDragging) {
					// 计算是否超过移动的最小距离
					mIsDragging = Math.sqrt((dx * dx) + (dy * dy)) >= mTouchSlop;
				}

				if (mIsDragging) {
					mListener.onDrag(dx, dy);
					mLastTouchX = x;
					mLastTouchY = y;

					if (null != mVelocityTracker) {
						mVelocityTracker.addMovement(ev);
					}
				}
				break;
			}

			case MotionEvent.ACTION_CANCEL: {
				// 回收
				if (null != mVelocityTracker) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
			}

			case MotionEvent.ACTION_UP: {
				if (mIsDragging) {
					if (null != mVelocityTracker) {
						mLastTouchX = getActiveX(ev);
						mLastTouchY = getActiveY(ev);

						// 获取最后1000ms内的移动量
						mVelocityTracker.addMovement(ev);
						mVelocityTracker.computeCurrentVelocity(1000);
						final float vX = mVelocityTracker.getXVelocity(), vY = mVelocityTracker
								.getYVelocity();
						// 判断移动量是否构成甩动操作
						if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
							mListener.onFling(mLastTouchX, mLastTouchY, -vX,
									-vY);
						}
					}
				}

				// 回收
				if (null != mVelocityTracker) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				break;
			}
			}

			return true;
		}

	}

	private static class EclairDetector extends CupcakeDetector {
		private static final int INVALID_POINTER_ID = -1;
		private int mActivePointerId = INVALID_POINTER_ID;
		private int mActivePointerIndex = 0;

		public EclairDetector(Context context) {
			super(context);
		}

		@Override
		float getActiveX(MotionEvent ev) {
			// 多点触摸
			try {
				return ev.getX(mActivePointerIndex);
			} catch (Exception e) {
				return ev.getX();
			}
		}

		@Override
		float getActiveY(MotionEvent ev) {
			// 多点触摸
			try {
				return ev.getY(mActivePointerIndex);
			} catch (Exception e) {
				return ev.getY();
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			final int action = ev.getAction();
			switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				mActivePointerId = ev.getPointerId(0);
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				mActivePointerId = INVALID_POINTER_ID;
				break;
			case MotionEvent.ACTION_POINTER_UP:// 有手指抬起
				// 获取抬起手指所在的点
				final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = ev.getPointerId(pointerIndex);
				if (pointerId == mActivePointerId) {
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					mActivePointerId = ev.getPointerId(newPointerIndex);
					mLastTouchX = ev.getX(newPointerIndex);
					mLastTouchY = ev.getY(newPointerIndex);
				}
				break;
			}

			mActivePointerIndex = ev
					.findPointerIndex(mActivePointerId != INVALID_POINTER_ID ? mActivePointerId
							: 0);
			return super.onTouchEvent(ev);
		}

	}

	private static class FroyoDetector extends EclairDetector {
		/** 缩放监听 */
		private final ScaleGestureDetector mDetector;

		private final OnScaleGestureListener mScaleListener = new OnScaleGestureListener() {

			@Override
			public boolean onScale(ScaleGestureDetector detector) {
				mListener.onScale(detector.getScaleFactor(),
						detector.getFocusX(), detector.getFocusY());
				return true;
			}

			@Override
			public boolean onScaleBegin(ScaleGestureDetector detector) {
				return true;
			}

			@Override
			public void onScaleEnd(ScaleGestureDetector detector) {
				// NO-OP
			}
		};

		public FroyoDetector(Context context) {
			super(context);
			mDetector = new ScaleGestureDetector(context, mScaleListener);
		}

		@Override
		public boolean isScaling() {
			return mDetector.isInProgress();
		}

		@Override
		public boolean onTouchEvent(MotionEvent ev) {
			mDetector.onTouchEvent(ev);
			return super.onTouchEvent(ev);
		}

	}

	public interface OnGestureListener {
		void onDrag(float dx, float dy);

		void onFling(float startX, float startY, float velocityX,
					 float velocityY);

		void onScale(float scaleFactor, float focusX, float focusY);
	}
}
