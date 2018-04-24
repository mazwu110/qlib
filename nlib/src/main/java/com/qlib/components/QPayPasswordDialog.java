package com.qlib.components;

import java.util.Timer;
import java.util.TimerTask;

import com.qlib.R;
import com.qlib.base.BaseActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class QPayPasswordDialog {
	private AlertDialog myDialog = null;
	private GridPasswordView pwdView;
	private Context mContext;
	private OnQPayPasswordClickListener dlgClick;
	private String myPassword = "";

	public QPayPasswordDialog(Context mcontext, OnQPayPasswordClickListener dlgClick) {
		this.mContext = mcontext;
		this.dlgClick = dlgClick;
	}

	public void show() {
		// 取得自定义View
		LayoutInflater layoutInflater = LayoutInflater.from(mContext);
		View mydlgView = layoutInflater.inflate(R.layout.qlib_dlg_pay_password, null);
		// mydlgView.setAlpha((float) 0.5);

		myDialog = new AlertDialog.Builder(mContext).create();
		// myDialog.setCancelable(false);
		myDialog.setCanceledOnTouchOutside(false);
		myDialog.getWindow().setGravity(Gravity.CENTER);
		myDialog.show();

		myDialog.getWindow().setContentView(mydlgView);
		Window wd = myDialog.getWindow();
		wd.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); // 不弹出软键盘解决方案
		pwdView = (GridPasswordView) wd.findViewById(R.id.gpv_normal);

		// 确定
		myDialog.getWindow().findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (myPassword.equals("")) {
					Toast.makeText(mContext, "请输入支付密码", Toast.LENGTH_LONG).show();
					return;
				}

				myDialog.dismiss();
				dlgClick.onQPaySubmitClickListener(myPassword);
			}
		});
		// 取消
		myDialog.getWindow().findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
				dlgClick.onQPayCancelClickListener();
			}
		});

		pwdView.setOnPasswordChangedListener(new GridPasswordView.OnPasswordChangedListener() {
			@Override
			public void onTextChanged(String psw) {
				myPassword = psw;
				if (psw.length() == 6) {
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(BaseActivity.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(pwdView.getWindowToken(), 0);
				}
			}

			@Override
			public void onInputFinish(String psw) {
			}
		});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				ctrHandler.sendEmptyMessage(0);
			}
		}, 300);
	}

	private Handler ctrHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			pwdView.getTextView0().setFocusable(true);
			pwdView.getTextView0().setFocusableInTouchMode(true);
			pwdView.getTextView0().requestFocus();
			InputMethodManager inputManager = (InputMethodManager) pwdView.getTextView0().getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManager.showSoftInput(pwdView.getTextView0(), 0);
		}
	};

	public interface OnQPayPasswordClickListener {
		public void onQPaySubmitClickListener(String password);

		public void onQPayCancelClickListener();
	}
}
