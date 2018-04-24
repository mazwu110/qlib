package com.qlib.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qlib.fileutils.Utils;

public class QJSON {
	public static JSONArray getJsonArray(JSONObject src, String arr_key) {
		JSONArray arr = new JSONArray();
		Object obj;
		try {
			obj = src.get(arr_key);
			if (obj instanceof JSONArray)
				arr = src.getJSONArray(arr_key);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return arr;
	}

	public static JSONArray getNewJsonArray(JSONArray arr_src) {
		JSONArray arr = new JSONArray();
		for (int i = 0; i < arr_src.length(); i++) {
			try {
				JSONObject js = arr_src.getJSONObject(i);
				arr.put(js);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return arr;
	}

	// 字符串转JSON数组
	public static JSONArray str2JSONArray(String src) {
		JSONArray arr = null;
		try {
			arr = new JSONArray(src);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return arr;
	}

	// 字符串转json对象
	public static JSONObject str2JSONObject(String src) {
		JSONObject arr = null;
		try {
			arr = new JSONObject(src);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return arr;
	}

	public static String getNullString(JSONObject src, String key) {
		if (src == null)
			return "";

		String tp = "";
		try {
			if (src.has(key)) {
				tp = src.getString(key);
			}
			return tp;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tp;
	}

	public static JSONObject getNullJSONobject(JSONObject src, String key) {
		JSONObject tp = null;
		try {
			tp = src.getJSONObject(key);
			return tp;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tp;
	}

	public static JSONArray getNullJSONArray(JSONObject src, String key) {
		JSONArray tp = null;
		try {
			tp = src.getJSONArray(key);
			return tp;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return tp;
	}

	// 删除指定下标的JSON数组，返回一个删除后的新JSON数组
	public static JSONArray deleteJSONArray(JSONArray arra, int delIndex) {
		int len = arra.length();
		JSONArray tparr = new JSONArray();
		for (int i = 0; i < len; i++) {
			try {
				JSONObject js = arra.getJSONObject(i);
				if (i != delIndex) {
					tparr.put(js);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return tparr;
	}

	// 删除指定字段值的数组，返回一个删除后的新JSON数组
	public static JSONArray deleteJSONArray(JSONArray arra, String fieldName,
			String deletValue) {
		int len = arra.length();
		JSONArray tparr = new JSONArray();
		JSONObject js = null;
		for (int i = 0; i < len; i++) {
			try {
				js = arra.getJSONObject(i);
				if (!js.getString(fieldName).equals(deletValue)) {
					tparr.put(js);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return tparr;
	}

	// 两个数组相加
	public static JSONArray joinJSONArray(JSONArray arra, JSONArray arrb) {
		JSONArray tparr = arra;
		if (arra == null) {
			return arrb;
		}

		if (arrb == null) {
			return arra;
		}

		if (arra == null && arrb == null) {
			return null;
		}

		int lena = arra.length();
		int lenb = arrb.length();

		for (int i = lena; i < lena + lenb; i++) {
			try {
				tparr.put(i, arrb.getJSONObject(i - lena));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return tparr;
	}

	// 根据时间分组
	public static JSONArray groupJsonArrayByDate(JSONArray src) {
		JSONArray alldata = new JSONArray(); // 所有项

		Date dta = new Date();
		Date dtb = new Date();
		int len = src.length();
		int[] index = new int[len];
		for (int i = 0; i < len; i++) {
			index[i] = 0;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		JSONObject jsa = null;
		JSONObject jsb = null;
		String datea = "";
		String dateb = "";
		for (int i = 0; i < src.length(); i++) {
			JSONObject jsmain = new JSONObject();
			JSONObject jschda = new JSONObject();
			JSONObject jschdb = new JSONObject();
			JSONArray arrchd = new JSONArray(); // 子项

			try {
				if (index[i] == 1) { // 已经取过数据了
					continue;
				}

				jsa = src.getJSONObject(i);
				datea = jsa.getString("otDate");
				datea = Utils.getBeforeAfterDate(datea, 0, "yyyy-MM-dd");
				dta = sdf.parse(datea + " 00:00:00");

				jschda.put("headUrl", jsa.getString("userImage"));
				jschda.put(
						"title",
						jsa.getString("zValue")
								+ " "
								+ Utils.getTimes(jsa.getString("otDate"),
										"HH:mm:ss") + " "
								+ jsa.getString("comeFrom"));
				jschda.put("contents", jsa.getString("value"));
				jschda.put("otDate", jsa.getString("otDate"));
				jschda.put("detail", jsa.getString("bValue"));
				jschda.put("bType", jsa.getString("bType"));
				jschda.put("bId", jsa.getString("bId"));
				jschda.put("msgId", jsa.getString("msgId"));
				jschda.put("userName", jsa.getString("userName"));
				jschda.put("zId", jsa.getString("zId"));

				arrchd.put(jschda);
				jsmain.put("date", datea);

				index[i] = 1;
				for (int j = i + 1; j < src.length(); j++) {
					jsb = src.getJSONObject(j);
					dateb = jsb.getString("otDate");
					dateb = Utils.getBeforeAfterDate(dateb, 0, "yyyy-MM-dd");
					dtb = sdf.parse(dateb + " 00:00:00");
					if (dta.getTime() == dtb.getTime()) { // 日期等才处理
						index[j] = 1; // 标记已经取到过数据
						// 取A的数据
						jschdb.put("headUrl", jsb.getString("userImage"));
						jschdb.put(
								"title",
								jsb.getString("zValue")
										+ " "
										+ Utils.getTimes(
												jsb.getString("otDate"),
												"HH:mm:ss") + " "
										+ jsb.getString("comeFrom"));
						jschdb.put("contents", jsb.getString("value"));
						jschdb.put("otDate", jsb.getString("otDate"));
						jschdb.put("detail", jsb.getString("bValue"));
						jschdb.put("bType", jsb.getString("bType"));
						jschdb.put("bId", jsb.getString("bId"));
						jschdb.put("msgId", jsb.getString("msgId"));
						jschdb.put("userName", jsb.getString("userName"));
						jschdb.put("zId", jsb.getString("zId"));

						arrchd.put(jschdb);
					}
				}

				jsmain.put("detail", arrchd);
				alldata.put(jsmain); // 装载总数据
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		return alldata;
	}

	// JSONArray 按JSOBJ中的时间字段排序 src 数据源 fieldname 所要排序的字段, sortType 排序类型 DESC
	// 时间排练
	public static JSONArray changeTimeElements(JSONArray src, String fieldname,
			String sortType) {
		if (src.length() <= 0) {
			return src;
		}

		JSONArray result = src;
		JSONObject jsa = null;
		JSONObject jsb = null;
		JSONObject tempjs = null;

		Date dta = new Date();
		Date dtb = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		String tm1 = "";
		String tm2 = "";

		for (int i = 0; i < result.length(); i++) {
			try {
				for (int j = 0; j <= result.length() - i - 1; j++) {
					jsa = result.getJSONObject(j);
					tm1 = jsa.getString(fieldname);

					jsb = result.getJSONObject(j + 1);
					tm2 = jsb.getString(fieldname);

					if (tm1 == null || tm1.equals("")) {
						continue;
					}
					dta = sdf.parse(tm1);

					if (tm2 == null || tm2.equals("")) {
						continue;
					}

					dtb = sdf.parse(tm2);

					// 降序排练
					if (sortType.equals("DESC")) {
						if (dta.getTime() < dtb.getTime()) {
							result.put(j, jsb);
							result.put(j + 1, jsa);
						}
					} else {
						if (dta.getTime() > dtb.getTime()) {
							result.put(j, jsb);
							result.put(j + 1, jsa);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	// JSONArray 按JSOBJ中的字段排序 src 数据源 fieldname 所要排序的字段, sortType 排序类型 DESC
	// 数字排序
	public static JSONArray changeNumberElements(JSONArray src,
			String fieldname, String sortType) {
		if (src.length() <= 0) {
			return src;
		}

		JSONArray result = src;
		JSONObject jsa = null;
		JSONObject jsb = null;

		int dta = 0;
		int dtb = 0;

		for (int i = 0; i < result.length(); i++) {
			try {
				for (int j = 0; j <= result.length() - i - 1; j++) {
					jsa = result.getJSONObject(j);
					dta = jsa.getInt(fieldname);

					jsb = result.getJSONObject(j + 1);
					dtb = jsb.getInt(fieldname);

					// 降序排练
					if (sortType.equals("DESC")) {
						if (dta < dtb) {
							result.put(j, jsb);
							result.put(j + 1, jsa);
						}
					} else {
						if (dta > dtb) {
							result.put(j, jsb);
							result.put(j + 1, jsa);
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static JSONArray changeNumberElements(JSONObject src) {
		if (src.length() <= 0) {
			return null;
		}

		Iterator<String> iter = src.keys();
		String key = null;
		JSONArray arrdest = new JSONArray();
		while (iter.hasNext()) {
			key = iter.next();
			try {
				arrdest.put(src.getJSONObject(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return arrdest;
	}

	// 修改JSON数组下标对应的JS串
	public static JSONArray updateJSONObjectElements(JSONArray srcarr,
			JSONObject srcjs, int updateIndex) {
		JSONArray temparr = new JSONArray();
		for (int i = 0; i < srcarr.length(); i++) {
			if (i == updateIndex - 1) {
				temparr.put(srcjs);
			} else {
				try {
					temparr.put(srcarr.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return temparr;
	}

	// 修改JSON数组下标对应的JS串的某一个KEY的值
	public static JSONArray updateJSONArrayElements(JSONArray srcarr,
			String keyName, String changeValue, int updateIndex) {
		JSONArray temparr = new JSONArray();
		JSONObject js = null;
		try {
			for (int i = 0; i < srcarr.length(); i++) {
				js = srcarr.getJSONObject(i);// 获取要修改的对象
				if (i == updateIndex) {
					js.put(keyName, changeValue);
					temparr.put(js);
				} else {
					temparr.put(js);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return temparr;
	}

	// 给JSON数组指定ID下的JSON对象增加一个数据
	// insertId数组中的id, campareID 用来做对比的ID， add_keyname在JSON对象中增加的KEY，
	// 增加KEY的对应值add_value
	public static JSONArray addValue2jsonarray(JSONArray srcarr,
			String insertId, String campareID, String add_keyname,
			String add_value) {
		JSONArray temparr = new JSONArray();
		for (int i = 0; i < srcarr.length(); i++) {
			try {
				JSONObject js = srcarr.getJSONObject(i);
				if (js.getString(insertId).equals(campareID)) {
					js.put(add_keyname, add_value);
				}

				temparr.put(srcarr.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return temparr;
	}
}
