package com.qjsonlib.converter;

import com.qjsonlib.internal.LazilyParsedNumber;
import com.qjsonlib.Converters;
import com.qjsonlib.JsonArray;
import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * JsonArray转换工具
 *
 * @author mzw
 *
 */
public final class JsonArrayConverter extends TypeConverter<JsonArray> {
	private static final String TAG = "com.qjsonlib.converter.JsonArrayConverter";

	public static final JsonArrayConverter INSTANCE = new JsonArrayConverter();

	@Override
	public JsonArray read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_ARRAY) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a ARRAY, but was " + jsonReader.next());
			return null;
		}

		JsonArray jsonArray = new JsonArray();
		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			Object value;
			switch (jsonReader.next()) {
				case STRING:
					value = jsonReader.nextString();
					break;
				case NUMBER:
					value = new LazilyParsedNumber(jsonReader.nextString());
					break;
				case BOOLEAN:
					value = jsonReader.nextBoolean();
					break;
				case NULL:
					value = null;
					jsonReader.nextNull();
					break;
				case BEGIN_ARRAY:
					value = JsonArrayConverter.INSTANCE.read(jsonReader);
					break;
				case BEGIN_OBJECT:
					value = JsonObjectConverter.INSTANCE.read(jsonReader);
					break;
				default:
					throw new JsonException("Error token " + jsonReader.next());
			}
			jsonArray.put(value);
		}
		jsonReader.endArray();

		return jsonArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, JsonArray value)
			throws JsonException {
		jsonWriter.beginArray();
		for (int i = 0; i < value.length(); i++) {
			Object item = value.get(i);

			TypeConverter<Object> typeConverter = (TypeConverter<Object>) Converters
					.getConverters().getConverter(
							TypeToken.get(item.getClass()));
			typeConverter.write(jsonWriter, item);
		}
		jsonWriter.endArray();
	}

}
