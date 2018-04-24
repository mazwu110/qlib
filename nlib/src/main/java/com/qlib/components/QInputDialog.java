package com.qlib.components;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.qlib.R;
import com.qlib.base.BaseActivity;

public class QInputDialog {
	private AlertDialog myDialog = null;
	private Context mcontext;
	private String title;
	private String hint;
	private OnQInputClickListener dlgClick;

	public QInputDialog(Context mcontext, String tip, String hint,
			OnQInputClickListener dlgClick) {
		this.mcontext = mcontext;
		this.title = tip;
		this.hint = hint;
		this.dlgClick = dlgClick;
	}

	public void show() {
		// 取得自定义View
		LayoutInflater layoutInflater = LayoutInflater.from(mcontext);
		View mydlgView = layoutInflater.inflate(R.layout.qlib_inputdlg, null);
		// mydlgView.setAlpha((float) 0.5);

		myDialog = new AlertDialog.Builder(mcontext).create();
		myDialog.setCancelable(false);
		Window wd = myDialog.getWindow();
		wd.setGravity(Gravity.CENTER);
		myDialog.show();
		wd.setContentView(mydlgView);
		wd.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); //  不弹出软键盘解决方案
		((TextView) wd.findViewById(R.id.tv_title)).setText(title);
		final EditText tv_content = (EditText) wd.findViewById(R.id.tv_content);
		tv_content.setHint(hint);

		// 确定
		wd.findViewById(R.id.tv_submit).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String content = tv_content.getText().toString().trim();
						if (content.equals("")) {
							Toast.makeText(mcontext, hint, Toast.LENGTH_LONG)
									.show();
							return;
						}

						myDialog.dismiss();
						dlgClick.onQInputSubmitClickListener(content);
					}
				});
		// 取消
		wd.findViewById(R.id.tv_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						myDialog.dismiss();
						dlgClick.onQInputCancelClickListener();
					}
				});

	}

	public interface OnQInputClickListener {
		public void onQInputSubmitClickListener(String inpuntValue);

		public void onQInputCancelClickListener();
	}
}
