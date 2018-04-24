package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Double数据转换工具
 *
 * @author mzw
 *
 */
public final class DoubleConverter extends TypeConverter<Double> {
	private static final String TAG = "com.qjsonlib.converter.basis.DoubleConverter";

	public static final DoubleConverter INSTANCE = new DoubleConverter();

	@Override
	public Double read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a DOUBLE, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextDouble();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a DOUBLE, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Double value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
