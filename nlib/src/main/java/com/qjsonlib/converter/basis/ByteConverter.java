package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Byte数据转换工具
 *
 * @author mzw
 *
 */
public final class ByteConverter extends TypeConverter<Byte> {
	private static final String TAG = "com.qjsonlib.converter.basis.ByteConverter";

	public static final ByteConverter INSTANCE = new ByteConverter();

	@Override
	public Byte read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.NUMBER
				&& jsonReader.next() != JsonToken.STRING) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a BYTE, but was " + jsonReader.next());
			return null;
		}

		try {
			return jsonReader.nextInt().byteValue();
		} catch (Exception ignore) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a BYTE, but was STRING", ignore);
		}
		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Byte value) throws JsonException {
		jsonWriter.nextValue(value);
	}

}
