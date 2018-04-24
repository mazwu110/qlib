package com.qjsonlib;

import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.converter.TypeToken;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonWriter;

import java.io.Reader;
import java.io.StringWriter;
import java.lang.reflect.Type;

/**
 * JSON解析及转换工具
 *
 * @author mzw
 *
 */
@SuppressWarnings("unchecked")
public final class JSON {

	/**
	 * 配置DEBUG信息模式
	 *
	 * @param debug
	 *            true 开启
	 *            <p>
	 *            false 关闭
	 */
	public static void configDebug(boolean debug) {
		Log.isDebug = debug;
	}

	/**
	 * JSON转对象
	 *
	 * @param json
	 *            JSON字符串
	 * @param clazz
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(String json, Class<T> clazz)
			throws JsonException {
		return (T) parseObject(new JsonReader(json), TypeToken.get(clazz));
	}

	/**
	 * JSON转对象
	 *
	 * @param json
	 *            JSON字符串
	 * @param type
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(String json, Type type)
			throws JsonException {
		return (T) parseObject(new JsonReader(json), TypeToken.get(type));
	}

	/**
	 * JSON转对象
	 *
	 * @param json
	 *            JSON字符串
	 * @param typeToken
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(String json, TypeToken<T> typeToken)
			throws JsonException {
		return parseObject(new JsonReader(json), typeToken);
	}

	/**
	 * JSON转对象
	 *
	 * @param reader
	 *            JSON字符流
	 * @param clazz
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(Reader reader, Class<T> clazz)
			throws JsonException {
		return (T) parseObject(new JsonReader(reader), TypeToken.get(clazz));
	}

	/**
	 * JSON转对象
	 *
	 * @param reader
	 *            JSON字符流
	 * @param type
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(Reader reader, Type type)
			throws JsonException {
		return (T) parseObject(new JsonReader(reader), TypeToken.get(type));
	}

	/**
	 * JSON转对象
	 *
	 * @param reader
	 *            JSON字符流
	 * @param typeToken
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(Reader reader, TypeToken<T> typeToken)
			throws JsonException {
		return parseObject(new JsonReader(reader), typeToken);
	}

	/**
	 * JSON转对象
	 *
	 * @param jsonReader
	 *            JSON字符流
	 * @param clazz
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(JsonReader jsonReader, Class<T> clazz)
			throws JsonException {
		return (T) parseObject(jsonReader, TypeToken.get(clazz));
	}

	/**
	 * JSON转对象
	 *
	 * @param jsonReader
	 *            JSON字符流
	 * @param type
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(JsonReader jsonReader, Type type)
			throws JsonException {
		return (T) parseObject(jsonReader, TypeToken.get(type));
	}

	/**
	 * JSON转对象
	 *
	 * @param jsonReader
	 *            JSON字符流
	 * @param typeToken
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public static <T> T parseObject(JsonReader jsonReader,
									TypeToken<T> typeToken) throws JsonException {
		TypeConverter<T> typeConverter = Converters.getConverters()
				.getConverter(typeToken);
		return typeConverter.read(jsonReader);
	}

	/**
	 * 对象转JSON
	 *
	 * @param object
	 *            对象实例
	 * @return JSON字符串
	 * @throws JsonException
	 *             错误信息
	 */
	public static String toJSON(Object object) throws JsonException {
		TypeToken<Object> typeToken = (TypeToken<Object>) TypeToken
				.get((Type) object.getClass());
		TypeConverter<Object> typeConverter = Converters.getConverters()
				.getConverter(typeToken);
		StringWriter writer = new StringWriter();
		typeConverter.write(new JsonWriter(writer), object);
		return writer.toString();
	}
}
