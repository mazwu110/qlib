package com.qjsonlib.converter;

import com.qjsonlib.Converters;
import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonReaderInternalAccess;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * JSON转Map工具
 * <p>
 * 仅支持键类型为String，其他类型会导致JSON解析错误。
 *
 * @author mzw
 *
 * @param <K>
 * @param <V>
 */
public final class MapConverter<K, V> extends TypeConverter<Map<K, V>> {
	private static final String TAG = "com.qjsonlib.converter.MapConverter";
	/** 类型 */
	private TypeToken<Map<K, V>> mTypeToken;

	/**
	 * 构造方法
	 *
	 * @param typeToken
	 *            类型
	 */
	public MapConverter(TypeToken<Map<K, V>> typeToken) {
		mTypeToken = typeToken;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<K, V> read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_ARRAY
				&& jsonReader.next() != JsonToken.BEGIN_OBJECT) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a MAP, but was " + jsonReader.next());
			return null;
		}

		Map<K, V> map = constructor(mTypeToken);

		Type type = mTypeToken.getType();
		Class<?> rawTypeOfSrc = JSONTypes.getRawType(type);
		Type[] types = JSONTypes.getMapKeyAndValueTypes(type, rawTypeOfSrc);

		TypeConverter<K> keyTypeConverter = Converters.getConverters()
				.getConverter((TypeToken<K>) TypeToken.get(types[0]));
		TypeConverter<V> valueTypeConverter = Converters.getConverters()
				.getConverter((TypeToken<V>) TypeToken.get(types[1]));

		if (jsonReader.next() == JsonToken.BEGIN_ARRAY) {
			jsonReader.beginArray();
			while (jsonReader.hasNext()) {
				jsonReader.beginArray();
				K key = keyTypeConverter.read(jsonReader);
				V value = valueTypeConverter.read(jsonReader);
				V replaced = map.put(key, value);
				if (replaced != null) {
					throw new JsonException("duplicate key: " + key);
				}
				jsonReader.endArray();
			}
			jsonReader.endArray();
		} else {
			jsonReader.beginObject();
			while (jsonReader.hasNext()) {
				JsonReaderInternalAccess.INSTANCE.nameToValue(jsonReader);
				K key = keyTypeConverter.read(jsonReader);
				V value = valueTypeConverter.read(jsonReader);
				V replaced = map.put(key, value);
				if (replaced != null) {
					throw new JsonException("duplicate key: " + key);
				}
			}
			jsonReader.endObject();
		}
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void write(JsonWriter jsonWriter, Map<K, V> value)
			throws JsonException {
		if (value == null) {
			jsonWriter.nextNull();
			return;
		}

		Type type = mTypeToken.getType();
		Class<?> rawTypeOfSrc = JSONTypes.getRawType(type);
		Type[] types = JSONTypes.getMapKeyAndValueTypes(type, rawTypeOfSrc);

		TypeConverter<V> valueTypeConverter = Converters.getConverters()
				.getConverter((TypeToken<V>) TypeToken.get(types[1]));

		jsonWriter.beginObject();
		for (Map.Entry<K, V> entry : value.entrySet()) {
			jsonWriter.nextName((String) entry.getKey());
			valueTypeConverter.write(jsonWriter, entry.getValue());
		}
		jsonWriter.endObject();
	}

}
