package com.qlib.fileutils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class Utils {
	private final static String tag = "Utils";

	public static void parseXMLBySAX(String xmlStr, ContentHandler handler) {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		XMLReader reader;
		try {
			reader = factory.newSAXParser().getXMLReader();
			reader.setContentHandler(handler);
			// 开始解析文件
			reader.parse(new InputSource(new StringReader(xmlStr)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
	}

	public static String getMarkString(String Str, String LeftMark,
			String RightMark) {
		String tmpreturn = Str;
		if (tmpreturn.indexOf(LeftMark, 0) != -1) {
			int LeftMarkPoint = tmpreturn.indexOf(LeftMark, 0)
					+ LeftMark.length();
			int RightMarkPoint = tmpreturn.indexOf(RightMark, LeftMarkPoint);
			if (RightMarkPoint != -1) {
				int ValueLength = RightMarkPoint;
				tmpreturn = tmpreturn.substring(LeftMarkPoint, ValueLength);
			}
		}
		if (tmpreturn == Str) {
			return "";
		}

		return tmpreturn;
	}

	public static ArrayList getMarkStringList(String Str, String LeftMark,
			String RightMark) {
		ArrayList al = new ArrayList();
		String tempstr = new String(Str);
		String tempv = "";
		while (!(getMarkString(tempstr, LeftMark, RightMark).equals(""))) {
			tempv = getMarkString(tempstr, LeftMark, RightMark);
			if (tempv.equals(""))
				break;
			al.add(new String(tempv));
			tempstr = tempstr.replace(LeftMark + tempv + RightMark, "");
		}
		return al;
	}

	public static String UrlEncode(String code, String charset) {
		String rs = "";
		try {
			rs = URLEncoder.encode(code, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static String UrlDecode(String code, String charset) {
		String rs = "";
		try {
			rs = URLDecoder.decode(code, charset);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return rs;
	}

	/*
	 * Convert byte[] to hex
	 * string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	public static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	public static String GetEnoughLenStr(int num, String basestr) {
		String rs = basestr;
		int baseint = rs.length();
		if (baseint < num) {
			for (int i = baseint; i < num; i++) {
				rs = "0" + rs;
			}
		}
		return rs;
	}

	// 指定格式的字符串转成指定格式的日期
	public static Calendar getNewCalendar(String date, String Format) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(Format);

		try {
			if (date.equals("")) {
				date = sdf.format(cal.getTime());
			} else {
				cal.setTime(sdf.parse(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return cal;
	}

	public static String getBeforeAfterDate(String date, int n) {
		return getBeforeAfterDate(date, n, Calendar.DAY_OF_MONTH);
	}

	public static String getBeforeAfterMonth(String date, int n, String format) {
		Calendar cal = Calendar.getInstance();// 得到一个Calendar的实例
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (date.equals(""))
			date = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		cal.add(Calendar.MONTH, n); // 月份+-1
		Date newdate = cal.getTime(); // 结果
		df = new SimpleDateFormat(format);
		date = df.format(newdate);
		return date;
	}

	public static String getBeforeAfterYear(String date, int n, String format) {
		Calendar cal = Calendar.getInstance();// 得到一个Calendar的实例
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		if (date.equals(""))
			date = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		cal.add(Calendar.YEAR, n); // 年份+-1
		Date newdate = cal.getTime(); // 结果
		df = new SimpleDateFormat(format);
		date = df.format(newdate);
		return date;
	}

	public static String getUserTypeDate(String date, String srcType,
			String newType) {
		newType = newType.replaceAll("Y", "y");
		newType = newType.replaceAll("D", "d");
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(srcType);
		if (date.equals(""))
			date = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		df = new SimpleDateFormat(newType);
		date = df.format(cal.getTime());
		return date;
	}

	public static String getBeforeAfterDate(String date, int n, String type) {
		date = getBeforeAfterDate(date, n);
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(type);
		if (date.equals(""))
			date = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		date = df.format(cal.getTime());
		return date;
	}

	public static String getTimes(String date, String fmt) {
		String mydate = date;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (mydate.equals(""))
			mydate = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(mydate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		df = new SimpleDateFormat(fmt);
		mydate = df.format(cal.getTime());
		return mydate;
	}

	public static String getBeforeAfterDate(String date, int n, int field) {
		return getBeforeAfterDate(date, n, field, "yyyy-MM-dd HH:mm:ss");
	}

	public static String getBeforeAfterDate(String date, int n, int field,
			String type) {

		Calendar cal = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat(type);
		if (date.equals(""))
			date = df.format(cal.getTime());
		try {
			cal.setTime(df.parse(date));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cal.set(field, cal.get(field) + n);
		date = df.format(cal.getTime());
		return date;
	}

	// 计算两个时间差，到分
	public static String getRangeToTwoTimes(String DATE1, String DATE2,
			String fmt) {
		DateFormat df = new SimpleDateFormat(fmt);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			long diff = dt1.getTime() - dt2.getTime();// 这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			return String.valueOf((int) hours) + "小时" + (int) minutes + "分钟";
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return "0分钟";
	}

	public static String getRangeToTwoTimes2(String DATE1, String DATE2,
			String fmt) {
		DateFormat df = new SimpleDateFormat(fmt);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			long diff = dt1.getTime() - dt2.getTime();// 这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long mins = (diff - hours * (1000 * 60 * 60 * 24)) / (1000 * 60);

			if (hours <= 0 && days <= 0) {
				return String.valueOf(mins) + "分钟前";
			} else if (hours <= 24 && days <= 0) {
				return String.valueOf(hours) + "小时前";
			} else {
				if (days <= 30)
					return days + "天前";
				else if (days <= 360)
					return days / 30 + "个月前";
				else
					return DATE2; // 大于一年的返回原日期
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return "0分钟";
	}

	// 计算两个时间差，到分
	public static String getRangeToTwoTimes3(String DATE1, String DATE2,
			String fmt) {
		String tps = "";
		DateFormat df = new SimpleDateFormat(fmt);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			long diff = dt1.getTime() - dt2.getTime();// 这样得到的差值是微秒级别
			long days = diff / (1000 * 60 * 60 * 24);
			long hours = (diff - days * (1000 * 60 * 60 * 24))
					/ (1000 * 60 * 60);
			long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours
					* (1000 * 60 * 60))
					/ (1000 * 60);
			tps = days + "天" + hours + "小时";
			if (hours == 0)
				tps = days + "天";
			if (days == 0)
				tps = hours + "小时";
			return tps;
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return "0小时";
	}

	public static int compareDate(String DATE1, String DATE2, String fmt) {
		DateFormat df = new SimpleDateFormat(fmt);
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	public static int compareDate(Date dt1, Date dt2) {
		try {
			if (dt1.getTime() > dt2.getTime()) {
				return 1;
			} else if (dt1.getTime() < dt2.getTime()) {
				return -1;
			} else {
				return 0;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	// 核对日期是否超过当前日期
	public static boolean CheckDate(String newdate) {
		String crtdate = Utils.getCurrentTime("yyyy-MM-dd");
		if (Utils.compareDate(newdate, crtdate, "yyyy-MM-dd") == 1) {
			return true;
		}

		return false;
	}

	public static String getLastDayOfMonth(String year, String month) {
		int mYear = Integer.valueOf(year);
		int mMonth = Integer.valueOf(month);
		Calendar cal = Calendar.getInstance(); // 设置年份
		cal.set(Calendar.YEAR, mYear); // 设置月份
		cal.set(Calendar.MONTH, mMonth - 1); // 获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH); // 设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay); // 格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String lastDayOfMonth = sdf.format(cal.getTime());
		return lastDayOfMonth;
	}

	/**
	 * 获取当前时间
	 * 
	 * @param type
	 *            yyyy MM dd ww hh mm ss
	 * @return
	 */
	public static String getCurrentTime(String format) {
		String rsStr = format;
		Calendar ca = Calendar.getInstance();
		String year = Utils.GetEnoughLenStr(4, "" + ca.get(Calendar.YEAR));// 获取年份
		String month = Utils.GetEnoughLenStr(2, ""
				+ (ca.get(Calendar.MONTH) + 1));// 获取月份
		String day = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.DATE));// 获取日
		String minute = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.MINUTE));// 分
		String hour = Utils.GetEnoughLenStr(2,
				"" + ca.get(Calendar.HOUR_OF_DAY));// 小时
		String second = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.SECOND));// 秒
		String WeekOfYear = Utils.GetEnoughLenStr(2,
				"" + ca.get(Calendar.DAY_OF_WEEK));
		rsStr = rsStr.replace("yyyy", year + "");
		rsStr = rsStr.replace("MM", month + "");
		rsStr = rsStr.replace("dd", day + "");
		rsStr = rsStr.replace("ww", WeekOfYear + "");
		rsStr = rsStr.replace("hh", hour + "");
		rsStr = rsStr.replace("HH", hour + "");
		rsStr = rsStr.replace("mm", minute + "");
		rsStr = rsStr.replace("ss", second + "");
		return rsStr;
	}

	public static String getMyFormatDate(String date, String format) {
		String rsStr = format;
		Calendar ca = getNewCalendar(date, format);
		String year = Utils.GetEnoughLenStr(4, "" + ca.get(Calendar.YEAR));// 获取年份
		String month = Utils.GetEnoughLenStr(2, ""
				+ (ca.get(Calendar.MONTH) + 1));// 获取月份
		String day = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.DATE));// 获取日
		String minute = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.MINUTE));// 分
		String hour = Utils.GetEnoughLenStr(2,
				"" + ca.get(Calendar.HOUR_OF_DAY));// 小时
		String second = Utils.GetEnoughLenStr(2, "" + ca.get(Calendar.SECOND));// 秒
		String WeekOfYear = Utils.GetEnoughLenStr(2,
				"" + ca.get(Calendar.DAY_OF_WEEK));
		rsStr = rsStr.replace("yyyy", year + "");
		rsStr = rsStr.replace("MM", month + "");
		rsStr = rsStr.replace("dd", day + "");
		rsStr = rsStr.replace("ww", WeekOfYear + "");
		rsStr = rsStr.replace("hh", hour + "");
		rsStr = rsStr.replace("HH", hour + "");
		rsStr = rsStr.replace("mm", minute + "");
		rsStr = rsStr.replace("ss", second + "");

		return rsStr;
	}

	// 获取当前日期年月日时分秒
	public static String getCurrentTime() {
		return getCurrentTime("yyyy-MM-dd  HH:mm:ss");
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToInt（）配套使用
	 * 
	 * @param value
	 *            要转换的int值
	 * @return byte数组
	 */
	public static byte[] intToBytes(int value) {
		byte[] src = new byte[4];
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] intToBytes2(int value) {
		byte[] src = new byte[4];
		src[0] = (byte) ((value >> 24) & 0xFF);
		src[1] = (byte) ((value >> 16) & 0xFF);
		src[2] = (byte) ((value >> 8) & 0xFF);
		src[3] = (byte) (value & 0xFF);
		return src;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在前，高位在后)的顺序，和和intToBytes（）配套使用
	 * 
	 * @param src
	 *            byte数组
	 * @param offset
	 *            从数组的第offset位开始
	 * @return int数值
	 */
	public static int bytesToInt(byte[] src, int offset) {
		int value;
		value = (int) ((src[offset] & 0xFF) | ((src[offset + 1] & 0xFF) << 8)
				| ((src[offset + 2] & 0xFF) << 16) | ((src[offset + 3] & 0xFF) << 24));
		return value;
	}

	public static long bytesToLong(byte[] src, int offset) {
		long value;
		value = (long) ((long) (src[offset] & 0xFF)
				| (long) ((src[offset + 1] & 0xFF) << 8)
				| (long) ((src[offset + 2] & 0xFF) << 16)
				| (long) ((src[offset + 3] & 0xFF) << 24)
				| (long) ((src[offset + 4] & 0xFF) << 32)
				| (long) ((src[offset + 5] & 0xFF) << 40)
				| (long) ((src[offset + 6] & 0xFF) << 48) | (long) ((src[offset + 7] & 0xFF) << 56));
		return value;
	}

	/**
	 * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序。和intToBytes2（）配套使用
	 */
	public static int bytesToInt2(byte[] src, int offset) {
		int value;
		value = (int) (((src[offset] & 0xFF) << 24)
				| ((src[offset + 1] & 0xFF) << 16)
				| ((src[offset + 2] & 0xFF) << 8) | (src[offset + 3] & 0xFF));
		return value;
	}

	public static long bytesToLong2(byte[] src, int offset) {
		long value;
		value = (long) ((long) ((src[offset] & 0xFF) << 56)
				| (long) ((src[offset + 1] & 0xFF) << 48)
				| (long) ((src[offset + 2] & 0xFF) << 40)
				| (long) ((src[offset + 3] & 0xFF) << 32)
				| (long) ((src[offset + 4] & 0xFF) << 24)
				| (long) ((src[offset + 5] & 0xFF) << 16)
				| (long) ((src[offset + 6] & 0xFF) << 8) | (long) (src[offset + 7] & 0xFF));
		return value;
	}

	/**
	 * 将Long数值转换为占四个字节的byte数组，本方法适用于(低位在前，高位在后)的顺序。 和bytesToLong（）配套使用
	 * 
	 * @param value
	 *            要转换的Long值
	 * @return byte数组
	 */
	public static byte[] longToBytes(Long value) {
		byte[] src = new byte[8];
		src[7] = (byte) ((value >> 56) & 0xFF);
		src[6] = (byte) ((value >> 48) & 0xFF);
		src[5] = (byte) ((value >> 40) & 0xFF);
		src[4] = (byte) ((value >> 32) & 0xFF);
		src[3] = (byte) ((value >> 24) & 0xFF);
		src[2] = (byte) ((value >> 16) & 0xFF);
		src[1] = (byte) ((value >> 8) & 0xFF);
		src[0] = (byte) (value & 0xFF);
		return src;
	}

	public static byte[] inputStreamToBtye(InputStream inputstream, int size)
			throws IOException {
		ByteArrayOutputStream bais;
		byte[] b;
		int len;
		b = new byte[size];
		len = inputstream.read(b);
		// Log.d(tag,"b="+Arrays.toString(b));
		bais = new ByteArrayOutputStream();
		bais.write(b, 0, len);
		bais.flush();
		b = bais.toByteArray();
		bais.close();
		return b;
	}

	public static String byteTohex(byte[] buffer) {
		String h = "";

		for (int i = 0; i < buffer.length; i++) {
			String temp = Integer.toHexString(buffer[i] & 0xFF);
			if (temp.length() == 1) {
				temp = "0" + temp;
			}
			h = h + " " + temp;
		}

		return h;

	}

	public static void deleteFile(File file) {
		if (file.isFile()) {
			file.delete();
			return;
		}

		if (file.isDirectory()) {
			File[] childFiles = file.listFiles();
			if (childFiles == null || childFiles.length == 0) {
				file.delete();
				return;
			}

			for (int i = 0; i < childFiles.length; i++) {
				deleteFile(childFiles[i]);
			}
			file.delete();
		}
	}

	public static String addZeroForNumLeft(String str, int strLength) {
		int strLen = str.length();
		if (strLen > strLength)
			return str.substring(0, strLength);
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}

	public static String addZeroForNumRight(String str, int strLength) {
		int strLen = str.length();
		if (strLen > strLength)
			return str.substring(0, strLength);
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				// sb.append("0").append(str);// 左补0
				sb.append(str).append("0");// 右补0
				str = sb.toString();
				strLen = str.length();
			}
		}

		return str;
	}
}
