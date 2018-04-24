package com.qlib.libadapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qlib.R;
import com.qlib.base.LibShowImageActivity;
import com.qlib.qremote.QApp;
import com.qlib.qutils.QUtils;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

// 横幅滚动适配器，通用
public class LibBannerAdapter extends PagerAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private JSONArray jsonArray;
	private int height;
	private int slideimagecount = 10000;
	private ArrayList<String> mdata = new ArrayList<String>();

	public LibBannerAdapter(Context context, JSONArray jsonArray) {
		mContext = context;
		inflater = LayoutInflater.from(context);
		this.jsonArray = jsonArray;
		height = (int) (QUtils.getScreenWitdh(context) / 2);
		
		setShowListData();
	}
	
	private void setShowListData() {
		JSONObject js = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				js = jsonArray.getJSONObject(i);
				mdata.add(js.getString("img_url"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getCount() {
		if (jsonArray != null) {
			return slideimagecount;
		} else {
			return 0;
		}
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		position %= jsonArray.length();
		FrameLayout convertView = (FrameLayout) inflater.inflate(R.layout.item_banner_image, null);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		image.setScaleType(ScaleType.CENTER_CROP);
		JSONObject js;
		try {
			js = jsonArray.getJSONObject(position);
			QApp.mImageLoader.displayNetImage(image, js.getString("img_url"));
			final int mposition = position;
			image.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					LibShowImageActivity.startImageActivity(mContext, mdata, mposition, false);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		container.addView(convertView, new FrameLayout.LayoutParams(QUtils.getScreenWitdh(mContext), height));
		return convertView;
	}
}
