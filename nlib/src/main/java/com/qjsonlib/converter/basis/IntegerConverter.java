package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Integer数据转换工具
 *
 * @author mzw
 *
 */
public final class IntegerConverter extends TypeConverter<Integer> {
	private static final String TAG = "com.qjsonlib.converter.basis.IntegerConverter";

	public static final IntegerConverter INSTANCE = new IntegerConverter();

	@Override
	public Integer read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a INTEGER, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextInt();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a INTEGER, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter writer, Integer value) throws JsonException {
		writer.nextValue(value);
	}

}
