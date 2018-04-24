package com.qlib.components;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.qlib.R;
import com.qlib.qutils.QUtils;

public class PopListWindow extends PopupWindow {

	private View view;
	private Context mContext;
	private ListView mListView;
	private ListView msubListView;

	public static final int DISTANCE_FIX = 35;

	public PopListWindow(Context context, BaseAdapter adapter,
			BaseAdapter subadapter, OnItemClickListener onItemClick,
			OnItemClickListener onItemSubClick) {
		super(context);
		mContext = context;
		view = LayoutInflater.from(mContext).inflate(R.layout.pop_window_list,
				null);
		mListView = (ListView) view.findViewById(R.id.listview);
		msubListView = (ListView) view.findViewById(R.id.sublistview);
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(onItemClick);
		if (subadapter == null) {
			msubListView.setVisibility(View.GONE);
		} else {
			msubListView.setVisibility(View.VISIBLE);
			msubListView.setAdapter(subadapter);
			msubListView.setOnItemClickListener(onItemSubClick);
		}
		setWidth(QUtils.getScreenWitdh(context));
		setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		setContentView(view);

		ColorDrawable cd = new ColorDrawable(0x80000000);
		setBackgroundDrawable(cd);

		update();
		setTouchable(true);
		setOutsideTouchable(true);
		setFocusable(true);// true 按返回键，popupwindow消失
		setTouchInterceptor(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					setFocusable(false);
					update();
					dismiss();
				} else {
					setFocusable(true);
					update();
				}
				return false;
			}
		});
	}

	public void onSeleted(int position) {
	}

	public ListView getListView() {
		return mListView;
	}

	@Override
	public View getContentView() {
		return super.getContentView();
	}
}
