package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;
import com.qjsonlib.internal.LazilyParsedNumber;

/**
 * Number数据转换工具
 *
 * @author mzw
 *
 */
public final class NumberConverter extends TypeConverter<Number> {
	private static final String TAG = "com.qjsonlib.converter.basis.NumberConverter";

	public static final NumberConverter INSTANCE = new NumberConverter();

	@Override
	public Number read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a NUMBER, but was " + jsonReader.next());
			return null;
		}

		return new LazilyParsedNumber(jsonReader.nextString());
	}

	@Override
	public void write(JsonWriter jsonWriter, Number value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
