package com.qlib.base;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.qlib.components.ViewPagerIndicator;
import com.qlib.libadapter.LibBannerAdapter;
import com.qlib.qinterface.QInterface;
import com.qlib.qutils.QUtils;

import org.json.JSONArray;

// 有横幅滚动的界面使用
public class BaseBannerActivity extends BaseActivity implements QInterface,
		OnClickListener {
	protected LinearLayout id_banner;
	protected ViewPager mViewPager;
	protected ViewPagerIndicator indicator;
	private boolean isdragging;
	private Handler handler = new Handler();
	private Runnable mRunnable;
	private int slideimagecount = 10000;
	private boolean isstart;
	protected JSONArray bannerData = null;

	@Override
	public void InitComponents() {
		int tpwt = QUtils.getScreenWitdh(act_instance);
		int tpwt2 = (int) ((tpwt / 4) * 2);
		mViewPager.setLayoutParams(new FrameLayout.LayoutParams(tpwt, tpwt));
		mViewPager.setOnPageChangeListener(mOnPageChangeListener);
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(slideimagecount / 2);

		if (!isstart) {
			scrolloneself();
		}
	}

	protected void setBanner() {
		LibBannerAdapter bnAdapter = new LibBannerAdapter(act_instance,
				bannerData);
		mViewPager.setAdapter(bnAdapter);
		indicator.setCount(bannerData.length());
	}

	// 隐藏底部的滑动小图点
	public void setIndicatorVisibility() {
		indicator.setVisibility(View.GONE);
	}

	private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageScrollStateChanged(int arg0) {
			switch (arg0) {
			case ViewPager.SCROLL_STATE_DRAGGING:
				isdragging = true;
				break;
			case ViewPager.SCROLL_STATE_IDLE:
				isdragging = false;
				break;
			case ViewPager.SCROLL_STATE_SETTLING:
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int position) {
			if (bannerData != null && bannerData.length() > 0) {
				position %= bannerData.length();
				indicator.setSeletion(position);
			}
		}
	};

	private void scrolloneself() {
		isstart = true;
		mRunnable = new Runnable() {
			@Override
			public void run() {
				if (!isdragging) {
					int currentItem = mViewPager.getCurrentItem() + 1;
					if (currentItem >= slideimagecount) {
						currentItem = 0;
					}
					mViewPager.setCurrentItem(currentItem);
				}
				handler.postDelayed(this, 3000);
			}
		};
		handler.postDelayed(mRunnable, 3000);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(mRunnable);
	}

	@Override
	public void onClick(View v) {
	}
}
