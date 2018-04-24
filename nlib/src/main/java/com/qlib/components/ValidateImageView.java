package com.qlib.components;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class ValidateImageView extends View {
	private static final String[] CHARS = { "0", "1", "2", "3", "4", "5", "6",
			"7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "j", "k",
			"l", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y",
			"z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
			"M", "N", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

	private String mybitMapStr; // 返回的验证码
	private String TAG = "ValidateImageView";
	private Paint paint = new Paint();
	/*
	 * 验证码内容
	 */
	private String[] content = null;
	/*
	 * 验证码图片
	 */
	private Bitmap bitmap = null;

	public ValidateImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public String getbitMapStr() {
		return mybitMapStr;
	}

	@Override
	public void draw(Canvas canvas) {
		int width = getWidth(); // 获取对应宽度
		int height = getHeight();// 获取对应高度

		// TODO Auto-generated method stub
		if (bitmap != null) {
			canvas.drawBitmap(bitmap, 0, 0, paint);
		} else {
			paint.setColor(Color.GRAY);
			paint.setTextSize(height * 0.4f);
			canvas.drawText("点击换一换", 10, height / 2, paint);
		}

		super.draw(canvas);
	}

	/**
	 * 得到验证码；设置图片
	 * 
	 * @param strContent
	 *            验证码的字符串数组
	 * @return
	 */
	public String[] getValidataAndSetImage(int width, int height) {
		content = CHARS;
		// 产生随机数，并返回
		String[] strRes = generageRadom(content);
		mybitMapStr = strRes[0] + strRes[1] + strRes[2] + strRes[3];
		Log.i(TAG, "generate validate code: " + strRes[0] + strRes[1]
				+ strRes[2] + strRes[3]);
		// String strRes = generageRadomStr(strContent);
		// 传随机串和随机数
		bitmap = generateValidate(content, strRes, width, height);
		invalidate();

		return strRes;
	}

	private Bitmap generateValidate(String[] strContent, String[] strRes,
			int width, int height) {
		// int width = 120, height = 50;
		int isRes = isStrContent(strContent);
		if (isRes == 0) {
			return null;
		}

		Bitmap sourceBitmap = Bitmap.createBitmap(width, height,
				Config.ARGB_8888);
		Canvas canvas = new Canvas(sourceBitmap);
		Paint p = new Paint();
		p.setTextSize(height * 0.6f);
		p.setFakeBoldText(true);

		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(strRes[0], width / 10, height / 2 + 10, p);
		Matrix m1 = new Matrix();
		m1.setRotate(15);
		canvas.setMatrix(m1);

		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(strRes[1], (width * 2) / 10, height / 2, p);
		m1.setRotate(10);
		canvas.setMatrix(m1);

		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(strRes[2], (width * 3) /10, height / 2 - 10, p);
		m1.setRotate(15);
		canvas.setMatrix(m1);

		p.setColor(getRandColor(200, 230, 170));
		canvas.drawText(strRes[3], (width * 4) / 10, height / 2 - 15, p);
		m1.setRotate(20);
		canvas.setMatrix(m1);

		// 障碍设置
		int startX = 0, startY = 0, stopX = 0, stopY = 0;
		for (int i = 0; i < 55; i++) {
			startX = pointRadom(width);
			startY = pointRadom(height);
			stopX = pointRadom(15);
			stopY = pointRadom(15);
			p.setColor(getRandColor(200, 230, 220));
			canvas.drawLine(startX, startY - 20, startX + stopX, startY + stopY
					- 20, p);
		}

		canvas.save();
		return sourceBitmap;
	}

	private int isStrContent(String[] strContent) {
		if (strContent == null || strContent.length <= 0) {
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * 从指定数组中随机取出4个字符(数组)
	 * 
	 * @param strContent
	 * @return
	 */
	private String[] generageRadom(String[] strContent) {
		String[] str = new String[4];
		// 随机串的个数
		int count = strContent.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird = random.nextInt(count);
		int randomResFourth = random.nextInt(count);

		str[0] = strContent[randomResFirst].toString().trim();
		str[1] = strContent[randomResSecond].toString().trim();
		str[2] = strContent[randomResThird].toString().trim();
		str[3] = strContent[randomResFourth].toString().trim();
		return str;
	}

	/**
	 * 从指定数组中随机取出4个字符(字符串)
	 * 
	 * @param strContent
	 * @return
	 */
	private String generageRadomStr(String[] strContent) {
		StringBuilder str = new StringBuilder();
		// 随机串的个数
		int count = strContent.length;
		// 生成4个随机数
		Random random = new Random();
		int randomResFirst = random.nextInt(count);
		int randomResSecond = random.nextInt(count);
		int randomResThird = random.nextInt(count);
		int randomResFourth = random.nextInt(count);

		str.append(strContent[randomResFirst].toString().trim());
		str.append(strContent[randomResSecond].toString().trim());
		str.append(strContent[randomResThird].toString().trim());
		str.append(strContent[randomResFourth].toString().trim());
		return str.toString();
	}

	private int pointRadom(int n) {
		Random r = new Random();
		return r.nextInt(n);
	}

	/**
	 * 给定范围获得随机颜色
	 * 
	 * @param rc
	 *            0-255
	 * @param gc
	 *            0-255
	 * @param bc
	 *            0-255
	 * @return colorValue 颜色值，使用setColor(colorValue)
	 */
	public int getRandColor(int rc, int gc, int bc) {
		Random random = new Random();
		if (rc > 255)
			rc = 255;
		if (gc > 255)
			gc = 255;
		if (bc > 255)
			bc = 255;
		int r = rc + random.nextInt(rc);
		int g = gc + random.nextInt(gc);
		int b = bc + random.nextInt(bc);
		return Color.rgb(r, g, b);
	}

}
