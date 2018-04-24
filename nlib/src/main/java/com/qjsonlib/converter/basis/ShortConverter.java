package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Short数据转换工具
 *
 * @author mzw
 *
 */
public final class ShortConverter extends TypeConverter<Short> {
	private static final String TAG = "com.qjsonlib.converter.basis.ShortConverter";

	public static final ShortConverter INSTANCE = new ShortConverter();

	@Override
	public Short read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a SHORT, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextInt().shortValue();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a SHORT, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Short value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
