package com.qlib.libadapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.qlib.R;
import com.qlib.qinterface.OnAddImgListener;
import com.qlib.qremote.QApp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LibAddImgAdapter extends LibBaseAdapter<JSONObject> {
	private OnAddImgListener mListener;

	public LibAddImgAdapter(Context context, OnAddImgListener mListener) {
		super(context);
		this.mListener = mListener;
	}

	public void taskSuccess(JSONArray jsonArray, int page) {
		if (mRecordList != null && page == 1)
			mRecordList.clear();

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

	@Override
	public QHolder createHolder(int position, ViewGroup parent) {
		return new QHolder(LayoutInflater.from(mContext).inflate(R.layout.item_add_pic, parent, false));
	}

	@Override
	public void bindData(final int position, Object h, JSONObject js) {
		try {
			QHolder holder = (QHolder) h;
			holder.qlibdelete.setVisibility(View.VISIBLE);
			holder.image.setScaleType(ScaleType.CENTER_CROP);
			final String filePath = js.getString("filePath");
			if (position == mRecordList.size() - 1) {
				holder.qlibdelete.setVisibility(View.GONE);
				holder.image.setImageResource(R.drawable.addticket_background);
				holder.image.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mListener.onAddImgPost(-1, "");
					}
				});
			} else {

				QApp.mImageLoader.displayLocalFileImage(holder.image, filePath);
			}
			holder.qlibdelete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					mListener.onAddImgPost(position, filePath);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private final class QHolder extends LibBaseAdapter.BaseHolder {
		public final ImageView image;
		public final ImageView qlibdelete;

		public QHolder(View itemView) {
			super(itemView);
			image = $(R.id.image);
			qlibdelete = $(R.id.qlibdelete);
		}
	}
}
