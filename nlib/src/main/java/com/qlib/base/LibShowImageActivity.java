package com.qlib.base;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.qlib.R;
import com.qlib.components.photoview.HackyViewPager;
import com.qlib.components.photoview.PhotoView;
import com.qlib.qremote.QApp;

public class LibShowImageActivity extends BaseActivity implements OnClickListener,
		OnPageChangeListener {

	public static void startImageActivity(Context context,
			ArrayList<String> images, int position, boolean canDelete) {
		Intent intent = new Intent(context, LibShowImageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putStringArrayList("images", images);
		bundle.putInt("position", position);
		bundle.putBoolean("delete", canDelete);
		intent.putExtras(bundle);
		context.startActivity(intent);
	}

	private HackyViewPager mViewPager;
	private ImageViewPagerAdapter mAdapter;

	private ArrayList<String> mImageList;
	private int mIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image_view);
		showLeftBack();
		mViewPager = (HackyViewPager) findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(this);
		
		InitComponents();
	}

	@Override
	public void InitComponents() {
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			finish();
			return;
		}

		mImageList = bundle.getStringArrayList("images");
		if (mImageList == null) {
			finish();
			return;
		}

		mIndex = bundle.getInt("position", 0);
		mAdapter = new ImageViewPagerAdapter(this, mImageList);
		mViewPager.setAdapter(mAdapter);

		setTitle((mIndex + 1) + "/" + mImageList.size());
		mViewPager.setCurrentItem(mIndex);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		mIndex = position;
		if (mImageList != null) {
			setTitle((mIndex + 1) + "/" + mImageList.size());
		}

	}

	@Override
	public void onClick(View arg0) {
	}

	public class ImageViewPagerAdapter extends PagerAdapter {
		public Context mContext;
		private LayoutInflater inflater;
		private ArrayList<String> list;

		public ImageViewPagerAdapter(Context context, ArrayList<String> list) {
			mContext = context;
			inflater = LayoutInflater.from(context);
			this.list = list;
		}

		@Override
		public int getCount() {
			return list == null ? 0 : list.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View convertView = inflater.inflate(R.layout.item_image_viewpager,
					null);
			final PhotoView iv = (PhotoView) convertView
					.findViewById(R.id.image);
			String item = list.get(position);
			if (TextUtils.isEmpty(item)) {
				iv.setImageResource(R.drawable.bg_default);
			} else if (item.startsWith("/")) {
				QApp.mImageLoader.displayLocalFileImage(iv, item);
			} else {
				QApp.mImageLoader.displayNetImage(iv, item);
			}

			container.addView(convertView, 0);
			return convertView;
		}
	}

}
