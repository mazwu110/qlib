package com.qlib.components;

import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import com.qlib.R;
import com.qlib.qutils.QUtils;

// auth qiancao 2015-7-2 date and time components
public class QDateTimePicker extends AlertDialog implements OnClickListener,
		OnDateChangedListener, OnTimeChangedListener {

	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";

	private final DatePicker mDatePicker;
	private final TimePicker mTimePicker;
	private final OnDateSetListener mCallBack;

	private Context mContext;

	public interface OnDateSetListener {
		void onDateSet(DatePicker datePicker, int Year, int MonthOfYear,
				int DayOfMonth);

		void onTimeSet(TimePicker TimePicker, int hour, int minute);
	}

	public QDateTimePicker(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth, int Hour, int Minute) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth, Hour, Minute);
		this.mContext = context;
	}

	public QDateTimePicker(Context context, int theme,
			OnDateSetListener callBack, int year, int monthOfYear,
			int dayOfMonth, int Hour, int Minute) {
		super(context, theme);
		this.mContext = context;

		mCallBack = callBack;

		Context themeContext = getContext();
		setButton(BUTTON_POSITIVE, "确 定", this);
		setButton(BUTTON_NEGATIVE, "取 消", this);
		setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.qcdatetimepicker, null);
		setView(view);
		mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);
		resizePikcer(mDatePicker);

		mTimePicker = (TimePicker) view.findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		resizePikcer(mTimePicker);

		mDatePicker.init(year, monthOfYear, dayOfMonth, this);
		mTimePicker.setCurrentHour(Hour);
		mTimePicker.setCurrentMinute(Minute);
	}

	public void onClick(DialogInterface dialog, int which) {
		// 如果是“取 消”按钮，则返回，如果是“确 定”按钮，则往下执行
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker datepicker, int year, int month,
			int day) {
		if (datepicker.getId() == R.id.datePicker) {
			mDatePicker.init(year, month, day, this);
		}
	}

	@Override
	public void onTimeChanged(TimePicker timepicker, int hour, int minute) {
		if (timepicker.getId() == R.id.timePicker) {
			mTimePicker.setCurrentHour(hour);
			mTimePicker.setCurrentMinute(minute);
		}
	}

	/**
	 * 获得开始日期的DatePicker
	 * 
	 * @return The calendar view.
	 */
	public DatePicker getDatePickerStart() {
		return mDatePicker;
	}

	/**
	 * 获得时分秒TimePicker
	 * 
	 * @return The calendar view.
	 */
	public TimePicker getTimePicker() {
		return mTimePicker;
	}

	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
	}

	public void updateTimePicker(int hour, int minute) {
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
	}

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePicker.clearFocus();
			mCallBack.onDateSet(mDatePicker, mDatePicker.getYear(),
					mDatePicker.getMonth(), mDatePicker.getDayOfMonth());

			mTimePicker.clearFocus();
			mCallBack.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
					mTimePicker.getCurrentMinute());
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

		state.putInt(HOUR, mTimePicker.getCurrentHour());
		state.putInt(MINUTE, mTimePicker.getCurrentMinute());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int year = savedInstanceState.getInt(YEAR);
		int month = savedInstanceState.getInt(MONTH);
		int day = savedInstanceState.getInt(DAY);
		int hour = savedInstanceState.getInt(HOUR);
		;
		int minute = savedInstanceState.getInt(MINUTE);
		;

		mDatePicker.init(year, month, day, this);
		mTimePicker.setCurrentHour(hour);
		mTimePicker.setCurrentMinute(minute);
	}

	/**
	 * 调整FrameLayout大小
	 * 
	 * @param tp
	 */
	private void resizePikcer(FrameLayout tp) {
		List<NumberPicker> npList = findNumberPicker(tp);
		for (NumberPicker np : npList) {
			resizeNumberPicker(np);
		}
	}

	/**
	 * 得到viewGroup里面的numberpicker组件
	 * 
	 * @param viewGroup
	 * @return
	 */
	private List<NumberPicker> findNumberPicker(ViewGroup viewGroup) {
		List<NumberPicker> npList = new ArrayList<NumberPicker>();
		View child = null;
		if (null != viewGroup) {
			for (int i = 0; i < viewGroup.getChildCount(); i++) {
				child = viewGroup.getChildAt(i);
				if (child instanceof NumberPicker) {
					npList.add((NumberPicker) child);
				} else if (child instanceof LinearLayout) {
					List<NumberPicker> result = findNumberPicker((ViewGroup) child);
					if (result.size() > 0) {
						return result;
					}
				}
			}
		}

		return npList;
	}

	/*
	 * 调整numberpicker大小
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void resizeNumberPicker(NumberPicker np) {
		int scwitdh = (QUtils.getScreenWitdh(mContext) / 8);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				scwitdh, LayoutParams.WRAP_CONTENT);
		params.setMargins(10, 0, 10, 0);
		np.setLayoutParams(params);
	}
}