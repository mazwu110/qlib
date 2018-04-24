package com.qjsonlib;

import com.qjsonlib.converter.JsonArrayConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonWriter;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * JSON数组
 *
 * @author mzw
 *
 */
public final class JsonArray implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/** 参数集合 */
	private final List<Object> mList = new ArrayList<Object>(10);

	/**
	 * 构造方法
	 */
	public JsonArray() {
	}

	/**
	 * 构造方法
	 *
	 * @param list
	 *            参数集合
	 */
	public JsonArray(List<Object> list) {
		mList.addAll(list);
	}

	/**
	 * 构造方法
	 *
	 * @param json
	 *            JSON数据
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonArray(String json) throws JsonException {
		JsonArray jsonArray = JSON.parseObject(json, JsonArray.class);
		mList.addAll(jsonArray.mList);
	}

	/**
	 * 构造方法
	 *
	 * @param reader
	 *            JSON数据读取流
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonArray(Reader reader) throws JsonException {
		JsonArray jsonArray = JSON.parseObject(reader, JsonArray.class);
		mList.addAll(jsonArray.mList);
	}

	/**
	 * 构造方法
	 *
	 * @param jsonReader
	 *            JSON读取流
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonArray(JsonReader jsonReader) throws JsonException {
		JsonArray jsonArray = JSON.parseObject(jsonReader, JsonArray.class);
		mList.addAll(jsonArray.mList);
	}

	/**
	 * 获取数组长度
	 *
	 * @return 数组长度
	 */
	public int length() {
		return mList.size();
	}

	/**
	 * 获取指定位置的值
	 *
	 * @param index
	 *            指定位置
	 * @return 对象值
	 */
	public Object get(int index) {
		return mList.get(index);
	}

	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index
	 *            指定位置
	 * @return JsonObject对象值
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonObject getJsonObject(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof JsonObject) {
			return (JsonObject) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName()
				+ " cannot be converted to JsonObject");
	}

	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index
	 *            指定位置
	 * @return JsonArray对象值
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonArray getJsonArray(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof JsonArray) {
			return (JsonArray) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName()
				+ " cannot be converted to JsonArray");
	}

	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index
	 *            指定位置
	 * @return Boolean值
	 * @throws JsonException
	 *             错误信息
	 */
	public boolean getBoolean(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof Boolean) {
			return (Boolean) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName() + " cannot be converted to boolean");
	}

	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index
	 *            指定位置
	 * @return Integer值
	 * @throws JsonException
	 *             错误信息
	 */
	public int getInt(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof Number) {
			return ((Number) item).intValue();
		}

		if (item instanceof Integer) {
			return (Integer) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName() + " cannot be converted to int");
	}

	/**
	 * 获取指定位置的Long值
	 *
	 * @param index
	 *            指定位置
	 * @return Long值
	 * @throws JsonException
	 *             错误信息
	 */
	public long getLong(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof Number) {
			return ((Number) item).longValue();
		}

		if (item instanceof Long) {
			return (Long) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName() + " cannot be converted to long");
	}

	/**
	 * 获取指定位置的Float值
	 *
	 * @param index
	 *            指定位置
	 * @return Float值
	 * @throws JsonException
	 *             错误信息
	 */
	public float getFloat(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof Number) {
			return ((Number) item).floatValue();
		}

		if (item instanceof Float) {
			return (Float) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName() + " cannot be converted to float");
	}

	/**
	 * 获取指定位置的Double值
	 *
	 * @param index
	 *            指定位置
	 * @return Double值
	 * @throws JsonException
	 *             错误信息
	 */
	public double getDouble(int index) throws JsonException {
		Object item = get(index);

		if (item instanceof Number) {
			return ((Number) item).doubleValue();
		}

		if (item instanceof Double) {
			return (Double) item;
		}

		throw new JsonException("Value " + item + " at a of type "
				+ item.getClass().getName() + " cannot be converted to double");
	}

	/**
	 * 获取指定位置的String值
	 *
	 * @param index
	 *            指定位置
	 * @return String值
	 */
	public String getString(int index) {
		Object item = get(index);

		if (item instanceof String) {
			return (String) item;
		}

		return String.valueOf(item);
	}

	/**
	 * 获取指定位置的对象值
	 *
	 * @param index
	 *            指定位置
	 * @return 对象值
	 */
	public Object opt(int index) {
		return get(index);
	}

	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index
	 *            指定位置
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(int index) {
		try {
			return getJsonObject(index);
		} catch (JsonException e) {
			return null;
		}
	}

	/**
	 * 获取指定位置的JsonObject值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(int index, JsonObject fallback) {
		try {
			return getJsonObject(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index
	 *            指定位置
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(int index) {
		try {
			return getJsonArray(index);
		} catch (JsonException e) {
			return null;
		}
	}

	/**
	 * 获取指定位置的JsonArray值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(int index, JsonArray fallback) {
		try {
			return getJsonArray(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index
	 *            指定位置
	 * @return Boolean值
	 */
	public boolean optBoolean(int index) {
		try {
			return getBoolean(index);
		} catch (JsonException e) {
			return false;
		}
	}

	/**
	 * 获取指定位置的Boolean值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return Boolean值
	 */
	public boolean optBoolean(int index, boolean fallback) {
		try {
			return getBoolean(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index
	 *            指定位置
	 * @return Integer值
	 */
	public int optInt(int index) {
		try {
			return getInt(index);
		} catch (JsonException e) {
			return 0;
		}
	}

	/**
	 * 获取指定位置的Integer值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return Integer值
	 */
	public int optInt(int index, int fallback) {
		try {
			return getInt(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的Long值
	 *
	 * @param index
	 *            指定位置
	 * @return Long值
	 */
	public long optLong(int index) {
		try {
			return getLong(index);
		} catch (JsonException e) {
			return 0L;
		}
	}

	/**
	 * 获取指定位置的Long值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return Long值
	 */
	public long optLong(int index, long fallback) {
		try {
			return getLong(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的Float值
	 *
	 * @param index
	 *            指定位置
	 * @return Float值
	 */
	public double optFloat(int index) {
		try {
			return getFloat(index);
		} catch (JsonException e) {
			return 0D;
		}
	}

	/**
	 * 获取指定位置的Float值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return Float值
	 */
	public float optFloat(int index, float fallback) {
		try {
			return getFloat(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的Double值
	 *
	 * @param index
	 *            指定位置
	 * @return Double值
	 */
	public double optDouble(int index) {
		try {
			return getDouble(index);
		} catch (JsonException e) {
			return 0D;
		}
	}

	/**
	 * 获取指定位置的Double值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return Double值
	 */
	public double optDouble(int index, double fallback) {
		try {
			return getDouble(index);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取指定位置的String值
	 *
	 * @param index
	 *            指定位置
	 * @return String值
	 */
	public String optString(int index) {
		Object value = get(index);

		if (value instanceof String) {
			return (String) value;
		}

		return null;
	}

	/**
	 * 获取指定位置的String值
	 *
	 * @param index
	 *            指定位置
	 * @param fallback
	 *            默认值
	 * @return String值
	 */
	public String optString(int index, String fallback) {
		String value = optString(index);

		if (value != null) {
			return value;
		}

		return fallback;
	}

	/**
	 * 存储对象值
	 *
	 * @param value
	 *            对象值
	 */
	public void put(Object value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的对象值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            对象值
	 */
	public void put(int index, Object value) {
		mList.add(index, value);
	}

	/**
	 * 存储JsonObject对象
	 *
	 * @param value
	 *            JsonObject对象值
	 */
	public void put(JsonObject value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的JsonObject对象
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            JsonObject对象值
	 */
	public void put(int index, JsonObject value) {
		mList.add(index, value);
	}

	/**
	 * 存储Boolean值
	 *
	 * @param value
	 *            Boolean值
	 */
	public void put(boolean value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的Boolean值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            Boolean值
	 */
	public void put(int index, boolean value) {
		mList.add(index, value);
	}

	/**
	 * 存储Integer值
	 *
	 * @param value
	 *            Integer值
	 */
	public void put(int value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的Integer值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            Integer值
	 */
	public void put(int index, int value) {
		mList.add(index, value);
	}

	/**
	 * 存储Long值
	 *
	 * @param value
	 *            Long值
	 */
	public void put(long value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的Long值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            Long值
	 */
	public void put(int index, long value) {
		mList.add(index, value);
	}

	/**
	 * 存储Double值
	 *
	 * @param value
	 *            Double值
	 */
	public void put(double value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的Double值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            Double值
	 */
	public void put(int index, double value) {
		mList.add(index, value);
	}

	/**
	 * 存储String值
	 *
	 * @param value
	 *            String值
	 */
	public void put(String value) {
		mList.add(value);
	}

	/**
	 * 存储指定位置的String值
	 *
	 * @param index
	 *            指定位置
	 * @param value
	 *            String值
	 */
	public void put(int index, String value) {
		mList.add(index, value);
	}

	/**
	 * 删除指定位置的值
	 *
	 * @param index
	 *            指定位置
	 * @return 删除值
	 */
	public Object remove(int index) {
		return mList.remove(index);
	}

	/**
	 * 删除指定值
	 *
	 * @param value
	 *            删除值
	 */
	public void remove(Object value) {
		mList.remove(value);
	}

	/**
	 * 批量存储值
	 *
	 * @param collection
	 *            批量值
	 */
	public void putAll(Collection<Object> collection) {
		mList.addAll(collection);
	}

	/**
	 * 清空所有值
	 */
	public void clear() {
		mList.clear();
	}

	@Override
	protected Object clone() {
		return new JsonArray(mList);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonArray) {
			return ((JsonArray) obj).mList.equals(mList);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mList.hashCode();
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			JsonArrayConverter.INSTANCE.write(new JsonWriter(writer), this);
		} catch (JsonException e) {
		}
		return writer.getBuffer().toString();
	}
}
