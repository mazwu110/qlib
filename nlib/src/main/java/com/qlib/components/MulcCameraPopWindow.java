package com.qlib.components;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.qlib.R;
import com.qlib.qremote.QApp;
import com.qlib.qutils.BitmapCompressor;
import com.qlib.qutils.FileDirHandler;
import com.qlib.qutils.QUtils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.PopupWindow;
import android.widget.Toast;
import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ImageLoader;
import cn.finalteam.galleryfinal.PauseOnScrollListener;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.finalteam.galleryfinal.listener.GlidePauseOnScrollListener;
import cn.finalteam.galleryfinal.model.PhotoInfo;
import cn.finalteam.toolsfinal.DateUtils;

public class MulcCameraPopWindow extends PopupWindow {
	private static final int picMaxSize = 200; // kb
	public static final int ALBUM_BIG_PICTURE = 2;
	public static final int CROP_BIG_PICTURE = 3;
	public static final int PERMISSIONS_REQUEST_CAMERA = 4;
	private static Context mContext;
	private File TEMP_PATH;
	private String mFileName;
	private Uri mTakePhotoUri;

	private OnMulcBitmapListener mOnBitmapListener;
	private boolean enableCrop = false; // true 支持裁剪
	private boolean mutiSelect = true; // true 支持多选, false 单选， 单选一般只能用在选择头像的时候

	public MulcCameraPopWindow(Context context, OnMulcBitmapListener listener) {
		super(context);
		this.mContext = context;
		onCreate();
		TEMP_PATH = FileDirHandler.getCacheDir(context, "camera/");
		if (!TEMP_PATH.exists()) {
			TEMP_PATH.mkdirs();
		}
		mOnBitmapListener = listener;
	}

	public void setEnableCrop(boolean enableCrop, boolean mutiSelect) {
		this.enableCrop = enableCrop;
		this.mutiSelect = mutiSelect;
		if (mutiSelect) { // 多选，不支持裁剪
			this.enableCrop = false;
		}
	}

	public MulcCameraPopWindow(Fragment fragment, OnMulcBitmapListener listener) {
		super(fragment.getActivity());
		mContext = fragment.getActivity();
		onCreate();
		TEMP_PATH = FileDirHandler.getCacheDir(mContext, "camera");
		if (!TEMP_PATH.exists()) {
			TEMP_PATH.mkdirs();
		}
		mOnBitmapListener = listener;
	}

	public File getFilePath() {
		if (mFileName != null) {
			return new File(mFileName);
		}
		return null;
	}

	public void setFilePath(File filePath) {
		if (filePath != null) {
			mFileName = filePath.toString();
		}
	}

	protected void onCreate() {
		View view = LayoutInflater.from(mContext).inflate(R.layout.pop_camera, null);
		view.setMinimumWidth(QUtils.getScreenWitdh(mContext));
		view.setMinimumHeight(QUtils.getScreenHeight(mContext));

		view.findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				if (ContextCompat.checkSelfPermission(mContext,
						Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
					ActivityCompat.requestPermissions((Activity)mContext, new String[] {
							Manifest.permission.CAMERA,
							Manifest.permission.WRITE_EXTERNAL_STORAGE },
							PERMISSIONS_REQUEST_CAMERA);
				} else {
					openCamera();
				}
			}
		});
		view.findViewById(R.id.tv_album).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				openAlbum();
			}
		});
		view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		setContentView(view);

		// 解决自带虚拟导航栏的手机，底部被挡住的问题
		// setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		// mContext.getWindow().getDecorView().setSystemUiVisibility(
		// View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);// 华为手机会突然隐藏掉虚拟键盘，闪烁，不能用此代码

		setWidth(QUtils.getScreenWitdh(mContext));
		setHeight(QUtils.getScreenHeight(mContext));
		ColorDrawable cd = new ColorDrawable(-0000);
		setBackgroundDrawable(cd);
		update();
		// setTouchable(true);
		// setOutsideTouchable(false); // 点击外面消失
		setFocusable(true);// true 按返回键，popupwindow消失
		setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					setFocusable(false);
					update();
					// dismiss();
				} else {
					setFocusable(true);
					update();
				}
				return false;
			}
		});
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		super.showAtLocation(parent, gravity, x, y);
		mFileName = "temp_" + System.currentTimeMillis() + ".jpg";
	}

	/**
	 * 计算缩放大小
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			} else {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			}

			final float totalPixels = width * height;
			final float totalReqPixelsCap = reqWidth * reqHeight;
			while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
				inSampleSize++;
			}
		}
		return inSampleSize;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	private FunctionConfig createGalleryFinalConfig() {
		List<PhotoInfo> mPhotoList = new ArrayList<>();
		FunctionConfig.Builder functionConfigBuilder = new FunctionConfig.Builder();
		functionConfigBuilder.setMutiSelectMaxSize(QApp.maxSizeImg);
		functionConfigBuilder.setSelected(mPhotoList);// 添加过滤集合
		functionConfigBuilder.setEnableCrop(enableCrop); // 裁剪
		// functionConfigBuilder.setCropReplaceSource(true); // 裁剪替换原文件
		functionConfigBuilder.setCropSquare(enableCrop); // 四方形裁剪
		functionConfigBuilder.setEnableCamera(true); // 显示照相机
		functionConfigBuilder.setEnablePreview(true); // 显示预览

		FunctionConfig functionConfig = functionConfigBuilder.build();
		ThemeConfig themeConfig = ThemeConfig.DEFAULT;
		ImageLoader imageLoader = new com.qlib.imageloader.GlideImageLoader();
		PauseOnScrollListener pauseOnScrollListener = new GlidePauseOnScrollListener(false, true);

		CoreConfig coreConfig = new CoreConfig.Builder(mContext.getApplicationContext(), imageLoader, themeConfig)
				.setFunctionConfig(functionConfig).setPauseOnScrollListener(pauseOnScrollListener)
				.setNoAnimcation(false).build();
		GalleryFinal.init(coreConfig);

		return functionConfig;
	}
	
	// 拍照
	private void openCamera() {
		GalleryFinal.openCamera(ALBUM_BIG_PICTURE, createGalleryFinalConfig(), mOnHanlderResultCallback);
	}

	// 打开相册
	private void openAlbum() {
		if (mutiSelect) // 多选
			GalleryFinal.openGalleryMuti(ALBUM_BIG_PICTURE, createGalleryFinalConfig(), mOnHanlderResultCallback);
		else // 单选
			GalleryFinal.openGallerySingle(ALBUM_BIG_PICTURE, createGalleryFinalConfig(), mOnHanlderResultCallback);
	}

	private GalleryFinal.OnHanlderResultCallback mOnHanlderResultCallback = new GalleryFinal.OnHanlderResultCallback() {
		@Override
		public void onHanlderSuccess(int reqeustCode, List<PhotoInfo> resultList) { // 返回图片
			if (resultList != null) {
				List<String> filePaths = new ArrayList();
				for (int i = 0; i < resultList.size(); i++) {
					String filePath = resultList.get(i).getPhotoPath();
					String fileName = filePath;
					fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
					qcAlbumResult(filePath, fileName, 100); // 压缩图片,压缩20%就填80
				}
			}
		}

		@Override
		public void onHanlderFailure(int requestCode, String errorMsg) {
			Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
		}
	};

	private void qcAlbumResult(String filePath, String fileName, int rate) {
		// 第一步 压缩图片
		int reqWidth = QUtils.getScreenWitdh(mContext);
		int reqHeight = QUtils.getScreenHeight(mContext);

		File file = new File(filePath);
		File outfile = new File(TEMP_PATH, fileName);
		if (!file.exists()) {
			return;
		}

		FileInputStream is = null;
		OutputStream out = null;
		try {
			Bitmap bitmap;
			is = new FileInputStream(file);
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
			options.inPurgeable = true;
			BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
			options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 计算压缩比例
			double radio = (double) options.outHeight / options.outWidth;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, options);
			Bitmap result = BitmapCompressor.compressBitmap(bitmap, picMaxSize);

			out = new FileOutputStream(outfile);
			result.compress(Bitmap.CompressFormat.JPEG, rate, out);
			bitmap.recycle();
			result.recycle();
		} catch (Exception e) {
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		if (mOnBitmapListener != null) {
			if (mutiSelect) {// 多选
				mOnBitmapListener.mulcbitmapResult(outfile.getAbsolutePath(), mutiSelect);
			} else {
				cropPhoto(outfile); // 单选要裁剪
			}
		}
	}

	/** 裁剪图片 */
	private void cropPhoto(File file) {
		String outputX = (int) (QUtils.getScreenHeight(mContext) * 0.8) + "";
		String outputY = outputX;
		Uri uri = Uri.fromFile(file);
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);

		File toFile = new File(TEMP_PATH,  DateUtils.format(new Date(), "yyyyMMddHHmmss") + ".jpg");
		mTakePhotoUri = Uri.fromFile(toFile);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mTakePhotoUri);
		((Activity) mContext).startActivityForResult(intent, CROP_BIG_PICTURE);
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case CROP_BIG_PICTURE:
				if (mOnBitmapListener != null) {
					if (mTakePhotoUri != null) {
						mOnBitmapListener.mulcbitmapResult(mTakePhotoUri.getPath(), mutiSelect);
					}
				}
				return true;
			}
		}

		return false;
	}

	public String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaStore.Images.Media.DATA };
		ContentResolver ctr = mContext.getContentResolver();
		Cursor cursor = ctr.query(contentUri, proj, null, null, null);
		try {
			if (cursor.moveToFirst()) {
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				res = cursor.getString(column_index);
			}
		} catch (Exception E) {
			QApp.showToast(mContext, "获取图片失败，请重试");
		} finally {
			cursor.close();
		}
		return res;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}

	public interface OnMulcBitmapListener {
		void mulcbitmapResult(String filePath, boolean mutiSelect);
	}
	
	public boolean onRequestPermissionsResult(int requestCode,
			String[] permissions, int[] grantResults) {
		if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
			if (grantResults[0] == PackageManager.PERMISSION_GRANTED
					&& grantResults[1] == PackageManager.PERMISSION_GRANTED) {
				openCamera();
			} else {
				Toast.makeText(mContext, "请先开启相机和读写手机存储权限", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		}

		return false;
	}
}
