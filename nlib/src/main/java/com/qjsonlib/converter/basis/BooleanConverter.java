package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Boolean数据转换工具
 *
 * @author mzw
 *
 */
public final class BooleanConverter extends TypeConverter<Boolean> {
	private static final String TAG = "com.qjsonlib.converter.basis.BooleanConverter";

	public static final BooleanConverter INSTANCE = new BooleanConverter();

	@Override
	public Boolean read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BOOLEAN
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a BOOLEAN, but was " + jsonReader.next());
			return null;
		}

		if (jsonReader.next() == JsonToken.BOOLEAN) {
			return jsonReader.nextBoolean();
		}

		try {
			return Boolean.valueOf(jsonReader.nextString());
		} catch (Exception ignore) {
			Log.w(TAG, "Expected a BOOLEAN, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Boolean value)
			throws JsonException {
		jsonWriter.nextValue(value);
	}

}
