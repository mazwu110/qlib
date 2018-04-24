package com.qlib.components;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.qlib.R;

public class QDialog {
	private AlertDialog myDialog = null;
	private Context mcontext;
	private String content;
	private OnQClickListener dlgClick;

	public QDialog(Context mcontext, String tip, OnQClickListener dlgClick) {
		this.mcontext = mcontext;
		this.content = tip;
		this.dlgClick = dlgClick;
	}

	public void show() {
		// 取得自定义View
		LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
		View mydlgView = layoutInflater.inflate(R.layout.qlib_dlg, null);
		//mydlgView.setAlpha((float) 0.5);

		myDialog = new AlertDialog.Builder(mcontext).create();
		// myDialog.setCancelable(false);

		myDialog.getWindow().setGravity(Gravity.CENTER);
		myDialog.show();

		myDialog.getWindow().setContentView(mydlgView);
		((TextView) myDialog.getWindow().findViewById(R.id.tv_content))
				.setText(content);
		// 确定
		myDialog.getWindow().findViewById(R.id.tv_submit)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
						dlgClick.onQSubmitClickListener();
					}
				});
		// 取消
		myDialog.getWindow().findViewById(R.id.tv_cancel)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
						dlgClick.onQCancelClickListener();
					}
				});
	}

	public interface OnQClickListener {
		public void onQSubmitClickListener();

		public void onQCancelClickListener();
	}
}
