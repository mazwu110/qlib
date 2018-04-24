package com.qlib.components;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qlib.R;
import com.qlib.json.QJSON;
import com.qlib.libadapter.LibBaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class QDialogSelectorGrid {
	private AlertDialog myDialog = null;
	private Context mcontext;
	private String tip;
	private OnQGridClickListener dlgClick;
	private JSONArray inData; // 要显示的项，id,name,note 三个字段
	private JSONArray inTeachers; // 已选择的项

	public QDialogSelectorGrid(Context mcontext, String tip, JSONArray inData, JSONArray inTeachers,
			OnQGridClickListener dlgClick) {
		this.mcontext = mcontext;
		this.tip = tip;
		this.dlgClick = dlgClick;
		this.inData = inData;
		this.inTeachers = inTeachers;
	}

	public void show() {
		// 取得自定义View
		LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
		View mydlgView = layoutInflater.inflate(R.layout.qlib_dlg_grid, null);
		// mydlgView.setAlpha((float) 0.5);

		myDialog = new AlertDialog.Builder(mcontext).create();
		myDialog.setCancelable(false);

		myDialog.getWindow().setGravity(Gravity.CENTER);
		myDialog.show();

		myDialog.getWindow().setContentView(mydlgView);
		((TextView) myDialog.getWindow().findViewById(R.id.tv_tip)).setText(tip);

		QGridView gridview = (QGridView) myDialog.getWindow().findViewById(R.id.gridview);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
				320);
		LibAdapter myadt = new LibAdapter(mcontext);
		myadt.taskSuccess(inData, 1);
		gridview.setAdapter(myadt);
		// gridview.setLayoutParams(layoutParams);
		// 确定
		myDialog.getWindow().findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				dlgClick.onQGridViewSubmitClickListener(inTeachers);
			}
		});

		myDialog.getWindow().findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				dlgClick.onQGridViewCancelClickListener(inTeachers);
			}
		});
	}

	public interface OnQGridClickListener {
		public void onQGridViewSubmitClickListener(JSONArray arrData);

		public void onQGridViewCancelClickListener(JSONArray arrData);
	}

	public class LibAdapter extends LibBaseAdapter<JSONObject> {
		private boolean FlagCheck[];

		public LibAdapter(Context context) {
			super(context);
		}

		public void taskSuccess(JSONArray jsonArray, int page) {
			if (mRecordList != null && page == 1)
				mRecordList.clear();

			FlagCheck = new boolean[jsonArray.length()];
			InitFlag(jsonArray);
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

		private void InitFlag(JSONArray jsonArray) {
			JSONObject js = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					js = jsonArray.getJSONObject(i);
					if (checkSelected(js.getString("id")))
						FlagCheck[i] = true;
					else
						FlagCheck[i] = false;
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		private boolean checkSelected(String id) {
			boolean flag = false;
			JSONObject js = null;
			for (int i = 0; i < inTeachers.length(); i++) {
				try {
					js = inTeachers.getJSONObject(i);
					if (js.getString("id").equals(id)) {
						flag = true;
						break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return flag;
		}

		@Override
		public QHolder createHolder(int position, ViewGroup parent) {
			return new QHolder(LayoutInflater.from(mContext).inflate(R.layout.item_lib_dlg_grid, parent, false));
		}

		@Override
		public void bindData(final int position, Object h, JSONObject js) {
			try {
				QHolder holder = (QHolder) h;
				final String id = js.getString("id");
				final String name = js.getString("name");
				final String note = js.getString("note");

				holder.cb_name.setText(name);
				holder.cb_name.setChecked(FlagCheck[position]);
				holder.cb_name.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						boolean flag = !FlagCheck[position];
						try {
							if (flag) { // 选择
								JSONObject jsdata = new JSONObject();
								jsdata.put("id", id);
								jsdata.put("name", name);
								jsdata.put("note", note);
								inTeachers.put(jsdata);
							} else {
								inTeachers = QJSON.deleteJSONArray(inTeachers, "id", id);
							}

						} catch (JSONException e) {
							e.printStackTrace();
						}

						FlagCheck[position] = flag;
						notifyDataSetChanged();
					}
				});
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		private final class QHolder extends LibBaseAdapter.BaseHolder {
			public final CheckBox cb_name;

			public QHolder(View itemView) {
				super(itemView);
				cb_name = (CheckBox) itemView.findViewById(R.id.cb_name);
			}
		}
	}
}
