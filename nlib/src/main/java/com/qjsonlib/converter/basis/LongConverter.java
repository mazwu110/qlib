package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Long数据转换工具
 *
 * @author mzw
 *
 */
public final class LongConverter extends TypeConverter<Long> {
	private static final String TAG = "com.qjsonlib.converter.basis.LongConverter";

	public static final LongConverter INSTANCE = new LongConverter();

	@Override
	public Long read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a LONG, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextLong();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a LONG, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Long value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
