package com.qlib.libadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.qlib.R;
import com.qlib.base.LibShowImageActivity;
import com.qlib.qremote.QApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LibShowImageAdapter extends LibBaseAdapter<JSONObject> {
	private ArrayList<String> mdata = new ArrayList<String>();

	public LibShowImageAdapter(Context context) {
		super(context);
	}

	public void taskSuccess(JSONArray jsonArray, int page) {
		if (mRecordList != null && page == 1)
			mRecordList.clear();

		setShowListData(jsonArray);
		for (int i = 0; i < jsonArray.length(); i++)
			try {
				addAll(jsonArray.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}

		notifyDataSetChanged();
	}

	@Override
	public void addAll(JSONObject recordList) {
		mRecordList.add(recordList);
	}

	private void setShowListData(JSONArray jsonArray) {
		JSONObject js = null;
		for (int i = 0; i < jsonArray.length(); i++) {
			try {
				js = jsonArray.getJSONObject(i);
				mdata.add(js.getString("filepath"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public QHolder createHolder(int position, ViewGroup parent) {
		return new QHolder(LayoutInflater.from(mContext).inflate(R.layout.item_only_photo_small, parent, false));
	}

	@Override
	public void bindData(final int position, Object h, JSONObject js) {
		try {
			QHolder holder = (QHolder) h;
			holder.image.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					LibShowImageActivity.startImageActivity(mContext, mdata, position, false);
				}
			});

			String filepath = js.getString("sfilepath");// filepath
			if (TextUtils.isEmpty(filepath))
				filepath = js.getString("filepath"); // 缩略图没有的话取真实图

			if (!TextUtils.isEmpty(filepath)) {
				if (!filepath.equals(holder.image.getTag())) {
					QApp.mImageLoader.displayNetImage(holder.image, filepath);
					holder.image.setTag(filepath);
				}
			} else {
				holder.image.setImageResource(R.drawable.bg_default);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private final class QHolder extends LibBaseAdapter.BaseHolder {
		public final ImageView image;

		public QHolder(View itemView) {
			super(itemView);
			image = $(R.id.image);
		}
	}
}
