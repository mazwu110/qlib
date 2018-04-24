package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.math.BigDecimal;

/**
 * BigDecimal数据转换工具
 *
 * @author mzw
 *
 */
public final class BigDecimalConverter extends TypeConverter<BigDecimal> {
	private static final String TAG = "com.qjsonlib.converter.basis.BigDecimalConverter";

	public static final BigDecimalConverter INSTANCE = new BigDecimalConverter();

	@Override
	public BigDecimal read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a NUMBER, but was " + jsonReader.next());
			return null;
		}

		try {
			return new BigDecimal(jsonReader.nextString());
		} catch (Exception ignore) {
			Log.w(TAG, "Expected a NUMBER, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, BigDecimal value)
			throws JsonException {
		jsonWriter.nextValue(value);
	}

}
