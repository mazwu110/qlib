package com.qjsonlib;

import com.qjsonlib.converter.JsonObjectConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonWriter;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * JSON对象
 *
 * @author mzw
 *
 */
public final class JsonObject implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/** 参数集合 */
	private final Map<String, Object> mMap = new LinkedHashMap<String, Object>(
			16);

	/**
	 * 构造方法
	 */
	public JsonObject() {
	}

	/**
	 * 构造方法
	 *
	 * @param map
	 *            参数集合
	 */
	public JsonObject(Map<String, Object> map) {
		mMap.putAll(map);
	}

	/**
	 * 构造方法
	 *
	 * @param json
	 *            JSON数据
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonObject(String json) throws JsonException {
		JsonObject jsonObject = JSON.parseObject(json, JsonObject.class);
		mMap.putAll(jsonObject.mMap);
	}

	/**
	 * 构造方法
	 *
	 * @param reader
	 *            JSON数据读取流
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonObject(Reader reader) throws JsonException {
		JsonObject jsonObject = JSON.parseObject(reader, JsonObject.class);
		mMap.putAll(jsonObject.mMap);
	}

	/**
	 * 构造方法
	 *
	 * @param jsonReader
	 *            JSON读取流
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonObject(JsonReader jsonReader) throws JsonException {
		JsonObject jsonObject = JSON.parseObject(jsonReader, JsonObject.class);
		mMap.putAll(jsonObject.mMap);
	}

	/**
	 * 获取参数数量
	 *
	 * @return 参数数量
	 */
	public int length() {
		return mMap.size();
	}

	/**
	 * 获取键对应的值
	 *
	 * @param key
	 *            键名
	 * @return 对象值
	 */
	public Object get(String key) {
		return mMap.get(key);
	}

	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key
	 *            键名
	 * @return JsonObject对象值
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonObject getJsonObject(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof JsonObject) {
			return (JsonObject) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName()
				+ " cannot be converted to JsonObject");
	}

	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key
	 *            键名
	 * @return JsonArray对象值
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonArray getJsonArray(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof JsonArray) {
			return (JsonArray) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName()
				+ " cannot be converted to JsonArray");
	}

	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key
	 *            键名
	 * @return Boolean值
	 * @throws JsonException
	 *             错误信息
	 */
	public boolean getBoolean(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof Boolean) {
			return (Boolean) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName()
				+ " cannot be converted to boolean");
	}

	/**
	 * 获取键对应的Integer值
	 *
	 * @param key
	 *            键名
	 * @return Integer值
	 * @throws JsonException
	 *             错误信息
	 */
	public int getInt(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof Number) {
			return ((Number) value).intValue();
		}

		if (value instanceof Integer) {
			return (Integer) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName() + " cannot be converted to int");
	}

	/**
	 * 获取键对应的Long值
	 *
	 * @param key
	 *            键名
	 * @return Long值
	 * @throws JsonException
	 *             错误信息
	 */
	public long getLong(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof Number) {
			return ((Number) value).longValue();
		}

		if (value instanceof Long) {
			return (Long) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName() + " cannot be converted to long");
	}

	/**
	 * 获取键对应的Float值
	 *
	 * @param key
	 *            键名
	 * @return Float值
	 * @throws JsonException
	 *             错误信息
	 */
	public float getFloat(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof Number) {
			return ((Number) value).floatValue();
		}

		if (value instanceof Float) {
			return (Float) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName() + " cannot be converted to float");
	}

	/**
	 * 获取键对应的Double值
	 *
	 * @param key
	 *            键名
	 * @return Double值
	 * @throws JsonException
	 *             错误信息
	 */
	public double getDouble(String key) throws JsonException {
		Object value = get(key);

		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}

		if (value instanceof Double) {
			return (Double) value;
		}

		throw new JsonException("Value " + value + " at a of type "
				+ value.getClass().getName() + " cannot be converted to double");
	}

	/**
	 * 获取键对应的String值
	 *
	 * @param key
	 *            键名
	 * @return String值
	 * @throws JsonException
	 *             错误信息
	 */
	public String getString(String key) {
		Object value = get(key);

		if (value instanceof String) {
			return (String) value;
		}

		return String.valueOf(value);
	}

	/**
	 * 获取键对应的值
	 *
	 * @param key
	 *            键名
	 * @return 对象值
	 */
	public Object opt(String key) {
		return get(key);
	}

	/**
	 * 获取键对应的值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return 对象值
	 */
	public Object opt(String key, Object fallback) {
		Object value = get(key);
		if (value == null) {
			return fallback;
		}
		return get(key);
	}

	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key
	 *            键名
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(String key) {
		try {
			return getJsonObject(key);
		} catch (JsonException e) {
			return null;
		}
	}

	/**
	 * 获取键对应的JsonObject值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return JsonObject对象值
	 */
	public JsonObject optJsonObject(String key, JsonObject fallback) {
		try {
			return getJsonObject(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key
	 *            键名
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(String key) {
		try {
			return getJsonArray(key);
		} catch (JsonException e) {
			return null;
		}
	}

	/**
	 * 获取键对应的JsonArray值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return JsonArray对象值
	 */
	public JsonArray optJsonArray(String key, JsonArray fallback) {
		try {
			return getJsonArray(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key
	 *            键名
	 * @return Boolean值
	 */
	public boolean optBoolean(String key) {
		try {
			return getBoolean(key);
		} catch (JsonException e) {
			return false;
		}
	}

	/**
	 * 获取键对应的Boolean值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return Boolean值
	 */
	public boolean optBoolean(String key, boolean fallback) {
		try {
			return getBoolean(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的Integer值
	 *
	 * @param key
	 *            键名
	 * @return Integer值
	 */
	public int optInt(String key) {
		try {
			return getInt(key);
		} catch (JsonException e) {
			return 0;
		}
	}

	/**
	 * 获取键对应的Integer值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return Integer值
	 */
	public int optInt(String key, int fallback) {
		try {
			return getInt(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的Long值
	 *
	 * @param key
	 *            键名
	 * @return Long值
	 */
	public long optLong(String key) {
		try {
			return getLong(key);
		} catch (JsonException e) {
			return 0L;
		}
	}

	/**
	 * 获取键对应的Long值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return Long值
	 */
	public long optLong(String key, long fallback) {
		try {
			return getLong(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的Float值
	 *
	 * @param key
	 *            键名
	 * @return Float值
	 */
	public float optFloat(String key) {
		try {
			return getFloat(key);
		} catch (JsonException e) {
			return 0F;
		}
	}

	/**
	 * 获取键对应的Float值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return Float值
	 */
	public float optFloat(String key, float fallback) {
		try {
			return getFloat(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的Double值
	 *
	 * @param key
	 *            键名
	 * @return Double值
	 */
	public double optDouble(String key) {
		try {
			return getDouble(key);
		} catch (JsonException e) {
			return 0D;
		}
	}

	/**
	 * 获取键对应的Double值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return Double值
	 */
	public double optDouble(String key, double fallback) {
		try {
			return getDouble(key);
		} catch (JsonException e) {
			return fallback;
		}
	}

	/**
	 * 获取键对应的String值
	 *
	 * @param key
	 *            键名
	 * @return String值
	 */
	public String optString(String key) {
		Object value = get(key);

		if (value instanceof String) {
			return (String) value;
		}

		return null;
	}

	/**
	 * 获取键对应的String值
	 *
	 * @param key
	 *            键名
	 * @param fallback
	 *            默认值
	 * @return String值
	 */
	public String optString(String key, String fallback) {
		String value = optString(key);

		if (value != null) {
			return value;
		}

		return fallback;
	}

	/**
	 * 存储对象值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            对象值
	 */
	public void put(String key, Object value) {
		mMap.put(key, value);
	}

	/**
	 * 存储Boolean值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            Boolean值
	 */
	public void put(String key, boolean value) {
		mMap.put(key, value);
	}

	/**
	 * 存储Integer值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            Integer值
	 */
	public void put(String key, int value) {
		mMap.put(key, value);
	}

	/**
	 * 存储Long值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            Long值
	 */
	public void put(String key, long value) {
		mMap.put(key, value);
	}

	/**
	 * 存储Float值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            Float值
	 */
	public void put(String key, float value) {
		mMap.put(key, value);
	}

	/**
	 * 存储Double值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            Double值
	 */
	public void put(String key, double value) {
		mMap.put(key, value);
	}

	/**
	 * 存储String值
	 *
	 * @param key
	 *            键名
	 * @param value
	 *            String值
	 */
	public void put(String key, String value) {
		mMap.put(key, value);
	}

	/**
	 * 删除指定键值
	 *
	 * @param key
	 *            键名
	 * @return 删除值
	 */
	public Object remove(String key) {
		return mMap.remove(key);
	}

	/**
	 * 清空所有值
	 */
	public void clear() {
		mMap.clear();
	}

	/**
	 * 获取所有键名
	 *
	 * @return 所有键名
	 */
	public String[] keys() {
		String[] keys = new String[mMap.size()];
		return mMap.keySet().toArray(keys);
	}

	@Override
	protected Object clone() {
		return new JsonObject(mMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof JsonObject) {
			return ((JsonObject) obj).mMap.equals(mMap);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return mMap.hashCode();
	}

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		try {
			JsonObjectConverter.INSTANCE.write(new JsonWriter(writer), this);
		} catch (JsonException e) {
		}
		return writer.getBuffer().toString();
	}
}
