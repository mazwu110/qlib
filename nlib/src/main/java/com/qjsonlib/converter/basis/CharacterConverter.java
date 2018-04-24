package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * Character数据转换工具
 *
 * @author mzw
 *
 */
public final class CharacterConverter extends TypeConverter<Character> {
	private static final String TAG = "com.qjsonlib.converter.basis.CharacterConverter";

	public static final CharacterConverter INSTANCE = new CharacterConverter();

	@Override
	public Character read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.STRING
				&& jsonReader.next() != JsonToken.NUMBER) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a CHARACTER, but was " + jsonReader.next());
			return null;
		}

		String str = jsonReader.nextString();
		if (str.length() == 1) {
			return str.charAt(0);
		}

		Log.w(TAG, "Expected a CHARACTER, but was STRING");

		return null;
	}

	@Override
	public void write(JsonWriter jsonWriter, Character value)
			throws JsonException {
		jsonWriter.nextValue(value == null ? null : String.valueOf(value));
	}

}
