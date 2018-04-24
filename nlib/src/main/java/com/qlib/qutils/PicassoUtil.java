package com.qlib.qutils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.qlib.R;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PicassoUtil {
	private Context mContext;
	private static PicassoUtil instance;

	public PicassoUtil(Context context) {
		mContext = context;
	}

	public static PicassoUtil getInstance(Context mContext) {
		if (instance == null) {
			instance = new PicassoUtil(mContext);
		}
		return instance;
	}

	/** 显示网络图片 */
	public void displayNetImage(ImageView imageView, String url) {
		if (TextUtils.isEmpty(url))
			return;
		Picasso.with(mContext).load(url).placeholder(R.drawable.bg_default).error(R.drawable.bg_default)
				.into(imageView);
	}

	/** 显示网络图片 */
	public void displayNetImage(ImageView imageView, String url, int width, int height) {
		if (TextUtils.isEmpty(url))
			return;
		Picasso.with(mContext).load(url).placeholder(R.drawable.bg_default).error(R.drawable.bg_default)
				.resize(width, height).centerCrop().into(imageView);
	}

	/** 显示SD卡上的图片 */
	public void displayLocalFileImage(ImageView imageView, String path) {
		Picasso.with(mContext).load(new File(path)).into(imageView);
	}

	/** 显示SD卡上的图片 */
	public void displayLocalFileImage(ImageView imageView, String path, int width, int height) {
		Picasso.with(mContext).load(new File(path)).resize(width, height).centerCrop().into(imageView);
	}

	/** 获取SD缓存文件的地址 */
	public String getCacheFilePath(String path) {
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File file = new File(path);
		if (file != null && file.exists()) {
			return file.getAbsolutePath();
		}
		return null;
	}
}
