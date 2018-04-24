package com.qlib.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class BorderTextView extends TextView {

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.parseColor("#b4b4b4"));
		
		// 画TextView的4个边
		//canvas.drawLine(0, 0, this.getWidth() - 1, 0, paint);
		paint.setStrokeWidth((float) 5.0);              //设置线宽 
		canvas.drawLine(0, 0, 0, this.getHeight() + 2, paint);  //画左边线条
		paint.setStrokeWidth((float) 2.0);              //设置线宽 
		canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight() + 2, paint); //画右边线条
		//canvas.drawLine(0, this.getHeight() - 1, this.getWidth() - 1, this.getHeight() - 1, paint);
		super.onDraw(canvas);
	}

	public BorderTextView(Context context, AttributeSet attrs) {
		super(context, attrs);//this.setWillNotDraw(false);
	}
}
