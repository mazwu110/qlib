package com.qlib.components;

import android.app.Dialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.qlib.R;

public class LoadingDialog extends Dialog {
	private int allSec = 540;
	private int step = 30; // 30毫秒调一次
	private ImageView iv_route;
	private TextView tv_message;
	private TextView tv_title;
	private boolean cancelTime = false; // true 实际关闭时间
	private int index = 0; // 开始的图片
	private int pointIndex = 0;
	private TimeCount dtime;

	public LoadingDialog(Context context) {
		super(context, R.style.act_dialog);
		init();
	}
	
	public LoadingDialog(Context context, String msgTitle) {
		super(context, R.style.act_dialog);
		init();
		tv_title.setText(msgTitle);
	}

	public LoadingDialog(Context context, boolean isTrans) {
		super(context, isTrans ? R.style.act_dialog : R.style.act_dialog);
		init();
	}

	private void init() {
		this.setCancelable(true); // 是否返回键可取消, true 可以
		this.setCanceledOnTouchOutside(false);

		if (dtime == null)
			dtime = new TimeCount(allSec, step);// 构造CountDownTimer对象

		setContentView(R.layout.loadingdialog);
		iv_route = (ImageView) findViewById(R.id.iv_route);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_title = (TextView) findViewById(R.id.tv_title);
	}

	@Override
	public void show() {
		index = 0;
		pointIndex = 0;
		cancelTime = false;
		dtime.start();
		super.show();
	}

	@Override
	public void cancel() {
		cancelTime = true;
		if (dtime != null)
			dtime.onFinish();

		super.dismiss();
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			int resId = R.drawable.loading_01;
			if (index == 1)
				resId = R.drawable.loading_01;
			else if (index == 2)
				resId = R.drawable.loading_02;
			else if (index == 3)
				resId = R.drawable.loading_03;
			else if (index == 4)
				resId = R.drawable.loading_04;
			else if (index == 5)
				resId = R.drawable.loading_05;
			else if (index == 6)
				resId = R.drawable.loading_06;
			else if (index == 7)
				resId = R.drawable.loading_07;
			else if (index == 8)
				resId = R.drawable.loading_08;
			else if (index == 9)
				resId = R.drawable.loading_09;
			else if (index == 10)
				resId = R.drawable.loading_10;
			else if (index == 11)
				resId = R.drawable.loading_11;
			else if (index == 12)
				resId = R.drawable.loading_12;
			else if (index == 13)
				resId = R.drawable.loading_13;
			else if (index == 14)
				resId = R.drawable.loading_14;
			else if (index == 15)
				resId = R.drawable.loading_15;
			else if (index == 16)
				resId = R.drawable.loading_16;
			else if (index == 17)
				resId = R.drawable.loading_17;
			else if (index == 18)
				resId = R.drawable.loading_18;

			if (pointIndex / 10 == 0)
				tv_message.setText(".");
			else if (pointIndex / 10 == 1)
				tv_message.setText("..");
			else if (pointIndex / 10 == 2)
				tv_message.setText("...");
			else if (pointIndex / 10 == 3)
				tv_message.setText("....");
			else if (pointIndex / 10 == 4)
				tv_message.setText(".....");
			else if (pointIndex / 10 == 5) {
				pointIndex = 0;
				tv_message.setText("......");
			}
				
			iv_route.setImageResource(resId);
		};
	};

	/* 定义一个倒计时的内部类 */
	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
		}

		@Override
		public void onFinish() {// 计时完毕时触发
			if (!cancelTime) {
				index = 0;
				dtime.start(); // 一直循环
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {// 计时过程显示
			index += 1;
			pointIndex += 1;
			handler.sendEmptyMessage(0);
		}
	}
}