package com.qlib.components;

import java.lang.reflect.Field;

import com.qlib.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;


//auth qiancao 2015-7-2 date components
public class QDatePicker extends AlertDialog implements OnClickListener,
		OnDateChangedListener {

	private static final String YEAR = "start_year";
	private static final String MONTH = "start_month";
	private static final String DAY = "start_day";

	private final DatePicker mDatePicker;
	private final OnDateSetListener mCallBack;

	private Context mContext;

	public interface OnDateSetListener {
		void onDateSet(DatePicker startDatePicker, int startYear,
				int startMonthOfYear, int startDayOfMonth);
	}

	public QDatePicker(Context context, OnDateSetListener callBack, int year,
			int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
		this.mContext = context;
	}

	public QDatePicker(Context context, int theme, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth, true);
		this.mContext = context;
	}

	public QDatePicker(Context context, int theme, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth, boolean isDayVisible) {
		super(context, theme);
		this.mContext = context;

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, "确 定", this);
		setButton(BUTTON_NEGATIVE, "取 消", this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.qcdatepicker, null);
		setView(view);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		mDatePicker.init(year, monthOfYear, dayOfMonth, this);

		// 如果要隐藏当前日期，则使用下面方法。
		if (!isDayVisible) {
			hidDay(mDatePicker);
		}
	}

	/**
	 * 隐藏DatePicker中的日期显示
	 * 
	 * @param mDatePicker
	 */
	private void hidDay(DatePicker mDatePicker) {
		Field[] datePickerfFields = mDatePicker.getClass().getDeclaredFields();
		for (Field datePickerField : datePickerfFields) {
			if ("mDaySpinner".equals(datePickerField.getName())) {
				datePickerField.setAccessible(true);
				Object dayPicker = new Object();
				try {
					dayPicker = datePickerField.get(mDatePicker);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}

				((View) dayPicker).setVisibility(View.GONE);
			}
		}
	}

	public void onClick(DialogInterface dialog, int which) {
		// 如果是“取 消”按钮，则返回，如果是“确 定”按钮，则往下执行
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePicker)
			mDatePicker.init(year, month, day, this);
	}

	public DatePicker getDatePickerStart() {
		return mDatePicker;
	}

	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(YEAR, mDatePicker.getYear());
		state.putInt(MONTH, mDatePicker.getMonth());
		state.putInt(DAY, mDatePicker.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(YEAR);
		int start_month = savedInstanceState.getInt(MONTH);
		int start_day = savedInstanceState.getInt(DAY);
		mDatePicker.init(start_year, start_month, start_day, this);
	}
}