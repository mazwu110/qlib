package com.qlib.components.photoview;

import android.graphics.RectF;
import android.view.View;
import android.widget.ImageView;

public interface IPhotoView {
	/**
	 * 如果PhotoView允许放大图片则返回true
	 * 
	 * @return
	 */
	boolean canZoom();

	/**
	 * 获取当前显示图片资源的矩形区域。该矩形区域是相对于当前View，包括所有的缩放和平移操作。
	 * 
	 * @return
	 */
	RectF getDisplayRect();

	/**
	 * @return 获取当前缩小的最小等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType} .
	 */
	float getMinScale();

	/**
	 * @return 获取当前缩小的中等等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType} .
	 */
	float getMidScale();

	/**
	 * @return 获取当前缩小的最大等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType} .
	 */
	float getMaxScale();

	/**
	 * 获取当前缩放等级
	 * 
	 * @return
	 */
	float getScale();

	/**
	 * 获取当前ImageView的缩放类型
	 */
	ImageView.ScaleType getScaleType();

	/**
	 * 当图片位于横向边距时，是否允许ImageView的父控件截听触摸事件
	 */
	void setAllowParentInterceptOnEdge(boolean allow);

	/**
	 * 设置当前缩小的最小等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType}.
	 */
	void setMinScale(float minScale);

	/**
	 * 设置当前缩小的中等等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType}.
	 */
	void setMidScale(float midScale);

	/**
	 * 设置当前缩小的最大等级。该值依赖于当前的 {@link android.widget.ImageView.ScaleType}.
	 */
	void setMaxScale(float maxScale);

	/**
	 * 设置长按事件监听
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	void setOnLongClickListener(View.OnLongClickListener listener);

	/**
	 * 设置矩形区域变化监听，可用于绘制缩放的图片。
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	void setOnMatrixChangeListener(
			PhotoViewAttacher.OnMatrixChangedListener listener);

	/**
	 * 设置点击图片的事件监听
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	void setOnPhotoTapListener(PhotoViewAttacher.OnPhotoTapListener listener);

	/**
	 * 设置点击控件的事件监听
	 * 
	 * @param listener
	 *            - Listener to be registered.
	 */
	void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener listener);

	/**
	 * Controls how the image should be resized or moved to match the size of
	 * the ImageView. Any scaling or panning will happen within the confines of
	 * this {@link android.widget.ImageView.ScaleType}.
	 * 
	 * @param scaleType
	 *            - The desired scaling mode.
	 */
	void setScaleType(ImageView.ScaleType scaleType);

	/**
	 * 设置是否允许ImageView的放大缩小操作。如果允许相关操作，设置为FIT_CENTER
	 * 
	 * @param zoomable
	 *            - Whether the zoom functionality is enabled.
	 */
	void setZoomable(boolean zoomable);

	/**
	 * 根据给定的缩放坐标中心及缩放等级，进行缩放。
	 * 
	 * @param scale
	 *            - Scale to zoom to
	 * @param focalX
	 *            - X Focus Point
	 * @param focalY
	 *            - Y Focus Point
	 */
	void zoomTo(float scale, float focalX, float focalY);
}
