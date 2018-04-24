package com.qlib.libadapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public abstract class LibBaseAdapter<T> extends BaseAdapter {
	protected Context mContext;
	protected List<T> mRecordList = new ArrayList();

	public LibBaseAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final BaseHolder holder;
		if (convertView == null) {
			holder = (BaseHolder) createHolder(position, parent);
			convertView = holder.itemView;
		} else {
			holder = (BaseHolder) convertView.getTag();
		}
		bindData(position, holder, getItem(position));
		return convertView;
	}

	public abstract Object createHolder(int position, ViewGroup parent);

	public abstract void bindData(int position, Object holder, T data);

	public static abstract class BaseHolder {
		public final View itemView;

		public BaseHolder(View itemView) {
			this.itemView = itemView;
			itemView.setTag(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends View> T $(int resId) {
			return (T) itemView.findViewById(resId);
		}
	}
	
	//提供添加数据方法
	public abstract void addAll(T recordList);

	protected void ShowMessage() {
		Toast.makeText(mContext, "建设中...", Toast.LENGTH_SHORT).show();
	}

	@Override
	public int getCount() {
		if (mRecordList == null)
			return 0;

		return mRecordList.size();
	}

	//添加第一页数据
	public void addRecordList(List<T> datas) {
		mRecordList.clear();
		if (null != datas) {
			mRecordList.addAll(datas);
		}
		notifyDataSetChanged();
	}

	// 添加更多数据
	public void addMoreRecordList(List<T> datas) {
		if (null != datas) {
			mRecordList.addAll(datas);
		}
		notifyDataSetChanged();
	}

	public void clearDatas() {
		mRecordList.clear();
		notifyDataSetChanged();
	}

	@Override
	public T getItem(int position) {
		return mRecordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
