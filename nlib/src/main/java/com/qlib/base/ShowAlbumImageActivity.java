package com.qlib.base;

import java.util.ArrayList;

import android.annotation.SuppressLint;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.qlib.R;
import com.qlib.components.photoview.HackyViewPager;
import com.qlib.components.photoview.PhotoView;
import com.qlib.qremote.QApp;

public class ShowAlbumImageActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {

	public final static int CHOOSE_IMAGE_FLAG = 101;
	private HackyViewPager mViewPager;
	private ImageViewPagerAdapter mAdapter;
	private TextView tv_right_bar;
	private CheckBox cb_choose;
	private RelativeLayout rll_choose;

	private ArrayList<String> mImageList;
	private int mIndex = 0;
	private boolean isChoose;
	private boolean isChecked = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_showalbum_image);
		showLeftBack();
		mViewPager = (HackyViewPager) findViewById(R.id.viewpager);
		mViewPager.setOnPageChangeListener(this);

		InitComponents();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public void InitComponents() {
		Bundle bundle = getIntent().getExtras();
		mImageList = bundle.getStringArrayList("images");
		isChoose = bundle.getBoolean("isChoose");
		if (mImageList == null) {
			setResultData(false);
		}

		tv_right_bar = (TextView) findViewById(R.id.tv_right_bar);
		tv_right_bar.setText("确定");
		tv_right_bar.setTextColor(R.color.black);
		tv_right_bar.setOnClickListener(this);
		tv_right_bar.setVisibility(View.VISIBLE);

		mIndex = bundle.getInt("position", 0);
		mAdapter = new ImageViewPagerAdapter(this, mImageList);
		mViewPager.setAdapter(mAdapter);

		setTitle("照片预览");
		mViewPager.setCurrentItem(mIndex);

		findViewById(R.id.rll_choose).setOnClickListener(this);

		cb_choose = (CheckBox) findViewById(R.id.cb_choose);
		cb_choose.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean flags) {
				isChecked = flags;
			}
		});

		rll_choose = (RelativeLayout) findViewById(R.id.rll_choose);
		rll_choose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				cb_choose.setChecked(!cb_choose.isChecked());
			}
		});
		
		cb_choose.setChecked(isChoose);
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
	public void onClick(View v) {
		if (v.getId() == R.id.tv_right_bar) { // 选择 返回
			setResultData(isChecked);
		}
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

	// 设置顶部条栏左返回按钮
	public void showLeftBack() {
		ImageView ivLeft = (ImageView) findViewById(R.id.iv_back);
		if (ivLeft == null) {
			return;
		}

		ivLeft.setVisibility(View.VISIBLE);
		ivLeft.setImageResource(R.drawable.ic_back);
		ivLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResultData(isChecked);
			}
		});
	}

	private void setResultData(boolean flag) {
		Intent it = getIntent();
		Bundle bdl = new Bundle();
		bdl.putBoolean("isChecked", flag);
		bdl.putString("imgPath", mImageList.get(0));
		it.putExtras(bdl);
		setResult(CHOOSE_IMAGE_FLAG, it);
		finish();
	}
}
