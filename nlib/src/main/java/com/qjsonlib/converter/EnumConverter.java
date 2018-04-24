package com.qjsonlib.converter;

import android.util.SparseArray;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * 枚举对象转换工具
 *
 * @author mzw
 *
 * @param <T>
 */
public final class EnumConverter<T extends Enum<T>> extends TypeConverter<T> {
	private static final String TAG = "com.qjsonlib.converter.EnumConverter";
	/** 枚举类型 */
	private Class<T> mEnumType;
	/** 枚举序列集合 */
	private final SparseArray<T> constantOrdinal = new SparseArray<T>();

	/**
	 * 构造方法
	 *
	 * @param clazz
	 *            枚举类型
	 */
	public EnumConverter(Class<T> clazz) {
		mEnumType = clazz;
		for (T constant : clazz.getEnumConstants()) {
			constantOrdinal.put(constant.ordinal(), constant);
		}
	}

	@Override
	public T read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.STRING
				&& jsonReader.next() != JsonToken.NUMBER) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a ENUM, but was " + jsonReader.next());
			return null;
		}

		// 先根据枚举的名称赋值，无对应值再根据枚举的序列号赋值
		String nameOrOrdinal = jsonReader.nextString();
		T result = null;
		try {
			result = Enum.valueOf(mEnumType, nameOrOrdinal);
		} catch (Exception e) {
		}
		if (result == null) {
			try {
				result = constantOrdinal.get(Integer.parseInt(nameOrOrdinal));
			} catch (Exception e) {
			}
		}

		return result;
	}

	@Override
	public void write(JsonWriter jsonWriter, T value) throws JsonException {
		if (value != null) {
			jsonWriter.nextValue(value.name());
		} else {
			jsonWriter.nextNull();
		}
	}

}
