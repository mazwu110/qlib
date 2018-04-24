package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonWriter;
import com.qjsonlib.stream.JsonReader.JsonToken;

/**
 * Float数据转换工具
 *
 * @author mzw
 *
 */
public final class FloatConverter extends TypeConverter<Float> {
	private static final String TAG = "com.qjsonlib.converter.basis.FloatConverter";

	public static final FloatConverter INSTANCE = new FloatConverter();

	@Override
	public Float read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a FLOAT, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextFloat();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a FLOAT, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Float value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
