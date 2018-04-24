package com.qjsonlib.converter.basis;

import com.qjsonlib.JsonException;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.internal.LazilyParsedNumber;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.io.StringWriter;

/**
 * 字符串转换工具
 *
 * @author mzw
 *
 */
public final class StringConverter extends TypeConverter<String> {
	public static final StringConverter INSTANCE = new StringConverter();

	@Override
	public String read(JsonReader jsonReader) throws JsonException {
		JsonToken jsonToken = jsonReader.next();
		if (jsonToken == JsonToken.NULL) {
			jsonReader.nextNull();
			return null;
		} else if (jsonToken == JsonToken.BOOLEAN) {
			return Boolean.toString(jsonReader.nextBoolean());
		} else if (jsonToken == JsonToken.BEGIN_OBJECT) {
			StringWriter writer = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(writer);
			readJsonObject(jsonReader, jsonWriter);
			return writer.toString();
		} else if (jsonToken == JsonToken.BEGIN_ARRAY) {
			StringWriter writer = new StringWriter();
			JsonWriter jsonWriter = new JsonWriter(writer);
			readJsonArray(jsonReader, jsonWriter);
			return writer.toString();
		}

		return jsonReader.nextString();
	}

	@Override
	public void write(JsonWriter jsonWriter, String value) throws JsonException {
		jsonWriter.nextValue(value);
	}

	/**
	 * 读取JsonObject数据
	 *
	 * @param jsonReader
	 *            JSON读取流
	 * @param jsonWriter
	 *            字符串缓存
	 * @throws JsonException
	 *             错误信息
	 */
	private void readJsonObject(JsonReader jsonReader, JsonWriter jsonWriter)
			throws JsonException {
		jsonReader.beginObject();
		jsonWriter.beginObject();

		while (jsonReader.hasNext()) {
			jsonWriter.nextName(jsonReader.nextName());
			switch (jsonReader.next()) {
				case NULL:
					jsonReader.nextNull();
					jsonWriter.nextNull();
					break;
				case BEGIN_ARRAY:
					readJsonArray(jsonReader, jsonWriter);
					break;
				case BEGIN_OBJECT:
					readJsonObject(jsonReader, jsonWriter);
					break;
				case BOOLEAN:
					jsonWriter.nextValue(jsonReader.nextBoolean());
					break;
				case NUMBER:
					jsonWriter.nextValue(new LazilyParsedNumber(jsonReader
							.nextString()));
					break;
				case STRING:
					jsonWriter.nextValue(jsonReader.nextString());
					break;
				default:
					throw new JsonException("Error token " + jsonReader.next());
			}
		}

		jsonReader.endObject();
		jsonWriter.endObject();
	}

	/**
	 * 读取JsonArray数据
	 *
	 * @param jsonReader
	 *            JSON读取流
	 * @param jsonWriter
	 *            字符串缓存
	 * @throws JsonException
	 *             错误信息
	 */
	private void readJsonArray(JsonReader jsonReader, JsonWriter jsonWriter)
			throws JsonException {
		jsonReader.beginArray();
		jsonWriter.beginArray();

		while (jsonReader.hasNext()) {
			switch (jsonReader.next()) {
				case NULL:
					jsonReader.nextNull();
					jsonWriter.nextNull();
					break;
				case BEGIN_ARRAY:
					readJsonArray(jsonReader, jsonWriter);
					break;
				case BEGIN_OBJECT:
					readJsonObject(jsonReader, jsonWriter);
					break;
				case BOOLEAN:
					jsonWriter.nextValue(jsonReader.nextBoolean());
					break;
				case NUMBER:
					jsonWriter.nextValue(new LazilyParsedNumber(jsonReader
							.nextString()));
					break;
				case STRING:
					jsonWriter.nextValue(jsonReader.nextString());
					break;
				default:
					throw new JsonException("Error token " + jsonReader.next());
			}
		}

		jsonReader.endArray();
		jsonWriter.endArray();
	}
}
