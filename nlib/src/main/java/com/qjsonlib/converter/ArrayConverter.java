package com.qjsonlib.converter;

import android.util.Log;

import com.qjsonlib.Converters;
import com.qjsonlib.JsonException;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * 数组对象数据转换工具
 *
 * @author mzw
 *
 * @param <T>
 */
public final class ArrayConverter<T> extends TypeConverter<T> {
	private static final String TAG = "com.qjsonlib.converter.ArrayConverter";
	/** 数组类型 */
	private TypeToken<T> mTypeToken;

	/**
	 * 构造方法
	 *
	 * @param typeToken
	 *            数组类型
	 */
	public ArrayConverter(TypeToken<T> typeToken) {
		mTypeToken = typeToken;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_ARRAY) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a ARRAY, but was " + jsonReader.next());
			return null;
		}

		// 获取数组子类型，解决多重数组
		Class<?> componentType = mTypeToken.getRawType().getComponentType();
		List<Object> list = new ArrayList<Object>();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			TypeConverter<?> typeConverter = Converters.getConverters()
					.getConverter(TypeToken.get(componentType));
			Object item = typeConverter.read(jsonReader);
			list.add(item);
		}
		jsonReader.endArray();

		Object array = Array.newInstance(componentType, list.size());
		for (int i = 0; i < list.size(); i++) {
			Array.set(array, i, list.get(i));
		}
		return (T) array;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, T value) throws JsonException {
		if (value == null) {
			jsonWriter.nextNull();
			return;
		}

		jsonWriter.beginArray();

		Class<?> componentType = value.getClass().getComponentType();
		for (int i = 0, length = Array.getLength(value); i < length; i++) {
			Object item = Array.get(value, i);
			TypeToken<Object> typeToken;
			if (componentType == null) {
				typeToken = (TypeToken<Object>) TypeToken.get(item.getClass());
			} else {
				typeToken = (TypeToken<Object>) TypeToken.get(componentType);
			}
			TypeConverter<Object> t = Converters.getConverters().getConverter(
					typeToken);
			t.write(jsonWriter, item);
		}

		jsonWriter.endArray();
	}

}
