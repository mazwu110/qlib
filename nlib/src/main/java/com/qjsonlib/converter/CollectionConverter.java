package com.qjsonlib.converter;

import com.qjsonlib.Converters;
import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.lang.reflect.Type;
import java.util.Collection;

/**
 * 集合对象转换工具
 *
 * @author mzw
 *
 * @param <E>
 */
public final class CollectionConverter<E> extends TypeConverter<Collection<E>> {
	private static final String TAG = "com.qjsonlib.converter.CollectionConverter";
	/** 集合类型 */
	private TypeToken<Collection<E>> mTypeToken;
	/** 集合参数类型转换工具 */
	private TypeConverter<E> mElementConverter;

	/**
	 * 构造方法
	 *
	 * @param typeToken
	 *            集合类型
	 */
	@SuppressWarnings("unchecked")
	public CollectionConverter(TypeToken<Collection<E>> typeToken) {
		mTypeToken = typeToken;

		Type elementType = JSONTypes.getCollectionElementType(
				mTypeToken.getType(), mTypeToken.getRawType());
		mElementConverter = Converters.getConverters().getConverter(
				(TypeToken<E>) TypeToken.get(elementType));
	}

	@Override
	public Collection<E> read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_ARRAY) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a ARRAY, but was " + jsonReader.next());
			return null;
		}

		Collection<E> collection = constructor(mTypeToken);

		jsonReader.beginArray();
		while (jsonReader.hasNext()) {
			collection.add(mElementConverter.read(jsonReader));
		}
		jsonReader.endArray();

		return collection;
	}

	@Override
	public void write(JsonWriter jsonWriter, Collection<E> value)
			throws JsonException {
		if (value == null) {
			jsonWriter.nextNull();
			return;
		}

		jsonWriter.beginArray();
		for (E element : value) {
			mElementConverter.write(jsonWriter, element);
		}
		jsonWriter.endArray();
	}

}
