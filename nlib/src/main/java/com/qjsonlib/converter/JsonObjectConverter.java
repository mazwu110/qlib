package com.qjsonlib.converter;

import com.qjsonlib.internal.LazilyParsedNumber;
import com.qjsonlib.Converters;
import com.qjsonlib.JsonException;
import com.qjsonlib.JsonObject;
import com.qjsonlib.Log;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

/**
 * JsonObject转换工具
 *
 * @author mzw
 *
 */
public final class JsonObjectConverter extends TypeConverter<JsonObject> {
	private static final String TAG = "com.qjsonlib.converter.JsonObjectConverter";

	public static final JsonObjectConverter INSTANCE = new JsonObjectConverter();

	@Override
	public JsonObject read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_OBJECT) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a OBJECT, but was " + jsonReader.next());
			return null;
		}

		JsonObject jsonObject = new JsonObject();
		jsonReader.beginObject();
		while (jsonReader.hasNext()) {
			String key = jsonReader.nextName();
			Object value;
			switch (jsonReader.next()) {
				case BEGIN_ARRAY:
					value = JsonArrayConverter.INSTANCE.read(jsonReader);
					break;
				case BEGIN_OBJECT:
					value = JsonObjectConverter.INSTANCE.read(jsonReader);
					break;
				case BOOLEAN:
					value = jsonReader.nextBoolean();
					break;
				case NULL:
					value = null;
					jsonReader.nextNull();
					break;
				case NUMBER:
					value = new LazilyParsedNumber(jsonReader.nextString());
					break;
				case STRING:
					value = jsonReader.nextString();
					break;
				default:
					throw new IllegalArgumentException();
			}

			jsonObject.put(key, value);
		}
		jsonReader.endObject();

		return jsonObject;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, JsonObject value)
			throws JsonException {
		if (value == null) {
			jsonWriter.nextNull();
			return;
		}

		jsonWriter.beginObject();
		String[] keys = value.keys();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			Object item = value.get(key);
			jsonWriter.nextName(key);
			if (item == null) {
				jsonWriter.nextNull();
			} else {
				TypeConverter<Object> typeConverter = (TypeConverter<Object>) Converters
						.getConverters().getConverter(
								TypeToken.get(item.getClass()));
				typeConverter.write(jsonWriter, item);
			}
		}
		jsonWriter.endObject();
	}

}
