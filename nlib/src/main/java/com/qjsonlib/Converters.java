package com.qjsonlib;

import com.qjsonlib.converter.ArrayConverter;
import com.qjsonlib.converter.CollectionConverter;
import com.qjsonlib.converter.EnumConverter;
import com.qjsonlib.converter.JavaBeanConverter;
import com.qjsonlib.converter.JsonArrayConverter;
import com.qjsonlib.converter.JsonObjectConverter;
import com.qjsonlib.converter.MapConverter;
import com.qjsonlib.converter.TypeConverter;
import com.qjsonlib.converter.TypeToken;
import com.qjsonlib.converter.basis.BigDecimalConverter;
import com.qjsonlib.converter.basis.BigIntegerConverter;
import com.qjsonlib.converter.basis.BooleanConverter;
import com.qjsonlib.converter.basis.ByteConverter;
import com.qjsonlib.converter.basis.CharacterConverter;
import com.qjsonlib.converter.basis.DoubleConverter;
import com.qjsonlib.converter.basis.FloatConverter;
import com.qjsonlib.converter.basis.IntegerConverter;
import com.qjsonlib.converter.basis.LongConverter;
import com.qjsonlib.converter.basis.NumberConverter;
import com.qjsonlib.converter.basis.ShortConverter;
import com.qjsonlib.converter.basis.StringConverter;
import com.qjsonlib.internal.LazilyParsedNumber;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换工具集合
 *
 * @author mzw
 *
 */
public class Converters {
	private static Converters CONVERTER = new Converters();

	/**
	 * 获取类型转换工具集合
	 *
	 * @return 类型转换工具集合
	 */
	public static Converters getConverters() {
		return CONVERTER;
	}

	/**
	 * 添加自定义类型转换工具
	 *
	 * @param type
	 *            对象类型
	 * @param typeConverter
	 *            类型转换工具
	 */
	public static void addConverter(Type type, TypeConverter<?> typeConverter) {
		CONVERTER.converters.put(type, typeConverter);
	}

	public static TypeConverter<?> getConverter(Type type) {
		return CONVERTER.converters.get(type);
	}

	private HashMap<Type, TypeConverter<?>> converters = new HashMap<Type, TypeConverter<?>>();

	private Converters() {
		converters.put(Double.class, DoubleConverter.INSTANCE);
		converters.put(double.class, DoubleConverter.INSTANCE);

		converters.put(Float.class, FloatConverter.INSTANCE);
		converters.put(float.class, FloatConverter.INSTANCE);

		converters.put(Long.class, LongConverter.INSTANCE);
		converters.put(long.class, LongConverter.INSTANCE);

		converters.put(Integer.class, IntegerConverter.INSTANCE);
		converters.put(int.class, IntegerConverter.INSTANCE);

		converters.put(Short.class, ShortConverter.INSTANCE);
		converters.put(short.class, ShortConverter.INSTANCE);

		converters.put(BigInteger.class, BigIntegerConverter.INSTANCE);

		converters.put(BigDecimal.class, BigDecimalConverter.INSTANCE);

		converters.put(Number.class, NumberConverter.INSTANCE);
		converters.put(LazilyParsedNumber.class, NumberConverter.INSTANCE);

		converters.put(Character.class, CharacterConverter.INSTANCE);
		converters.put(char.class, CharacterConverter.INSTANCE);

		converters.put(Byte.class, ByteConverter.INSTANCE);
		converters.put(byte.class, ByteConverter.INSTANCE);

		converters.put(Boolean.class, BooleanConverter.INSTANCE);
		converters.put(boolean.class, BooleanConverter.INSTANCE);

		converters.put(String.class, StringConverter.INSTANCE);

		converters.put(JsonObject.class, JsonObjectConverter.INSTANCE);
		converters.put(JsonArray.class, JsonArrayConverter.INSTANCE);
	}

	/**
	 * 获取类型转换工具
	 *
	 * @param typeToken
	 *            对象类型
	 * @return 类型转换工具
	 */
	@SuppressWarnings("unchecked")
	public <T> TypeConverter<T> getConverter(TypeToken<T> typeToken) {
		TypeConverter<T> typeConverter = (TypeConverter<T>) converters
				.get(typeToken.getType());
		if (typeConverter != null) {
			return typeConverter;
		}

		if (typeToken.getType() instanceof Class<?>) {
			return getConverter((Class<?>) typeToken.getType(), typeToken);
		}

		return getConverter(typeToken.getRawType(), typeToken);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> TypeConverter<T> getConverter(Class<?> clazz,
											  TypeToken<T> typeToken) {
		TypeConverter<T> typeConverter = (TypeConverter<T>) converters
				.get(clazz);
		if (typeConverter != null) {
			return typeConverter;
		}

		if (clazz.isEnum()) {
			typeConverter = new EnumConverter(clazz);
		} else if (clazz.isArray()) {
			typeConverter = new ArrayConverter(typeToken);
		} else if (Collection.class.isAssignableFrom(clazz)) {
			typeConverter = new CollectionConverter(typeToken);
		} else if (Map.class.isAssignableFrom(clazz)) {
			typeConverter = new MapConverter(typeToken);
		} else {
			typeConverter = new JavaBeanConverter(typeToken);
		}

		converters.put(typeToken.getType(), typeConverter);

		return typeConverter;
	}
}
