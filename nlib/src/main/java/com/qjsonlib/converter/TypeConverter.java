package com.qjsonlib.converter;

import com.qjsonlib.JsonException;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonWriter;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * JSON数据类型转换工具
 * <p>
 * 提供{@linkplain TypeConverter#read(JsonReader) <span
 * color="0000FF">TypeConverter.read(JsonReader)}</span> 及
 * {@linkplain TypeConverter#write(JsonWriter, Object) <span
 * color="0000FF">TypeConverter.write(JsonWriter, Object)</span>} 方法，
 * 用于对JSON字段的读取及写入。
 * <p>
 * 默认提供常用的基本类型，同时可继承该类，自定义对象的转换读取方式。
 *
 * @author mzw
 *
 * @param <T>
 */
public abstract class TypeConverter<T> {
	/**
	 * 读取JSON数据，转换为对象
	 *
	 * @param jsonReader
	 *            JSON读取流
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public abstract T read(JsonReader jsonReader) throws JsonException;

	/**
	 * 写入JSON数据
	 *
	 * @param jsonWriter
	 *            JSON写入流
	 * @param value
	 *            对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	public abstract void write(JsonWriter jsonWriter, T value)
			throws JsonException;

	/**
	 * 提供常用类型反射构造方式
	 *
	 * @param typeToken
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	protected T constructor(TypeToken<T> typeToken) throws JsonException {
		final Type type = typeToken.getType();
		final Class<? super T> rawType = typeToken.getRawType();

		T object = newDefaultConstructor(rawType);
		if (object != null) {
			return object;
		}

		object = newDefaultImplementationConstructor(type, rawType);
		if (object != null) {
			return object;
		}

		throw new JsonException(
				("Unable to invoke no-args constructor for " + type + ". " + "Register an InstanceCreator with JSON for this type may fix this problem."));
	}

	/**
	 * 默认无参数构造方法
	 *
	 * @param rawType
	 *            对象类型
	 * @return 对象实例
	 * @throws JsonException
	 *             错误信息
	 */
	@SuppressWarnings("unchecked")
	private final T newDefaultConstructor(Class<? super T> rawType)
			throws JsonException {
		try {
			final Constructor<? super T> constructor = rawType
					.getDeclaredConstructor();
			if (!constructor.isAccessible()) {
				constructor.setAccessible(true);
			}
			try {
				Object[] args = null;
				return (T) constructor.newInstance(args);
			} catch (InstantiationException e) {
				throw new JsonException("Failed to invoke " + constructor
						+ " with no args", e);
			} catch (InvocationTargetException e) {
				throw new JsonException("Failed to invoke " + constructor
						+ " with no args", e.getTargetException());
			} catch (IllegalAccessException e) {
				throw new JsonException(e);
			}
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * 抽象对象构造方式
	 *
	 * @param type
	 * @param rawType
	 * @return 对象实例
	 */
	@SuppressWarnings("unchecked")
	private final T newDefaultImplementationConstructor(Type type,
														Class<? super T> rawType) {
		if (Collection.class.isAssignableFrom(rawType)) {
			if (SortedSet.class.isAssignableFrom(rawType)) {
				return (T) new TreeSet<Object>();
			} else if (Set.class.isAssignableFrom(rawType)) {
				return (T) new LinkedHashSet<Object>();
			} else if (Queue.class.isAssignableFrom(rawType)) {
				return (T) new LinkedList<Object>();
			} else {
				return (T) new ArrayList<Object>();
			}
		}

		if (Map.class.isAssignableFrom(rawType)) {
			if (SortedMap.class.isAssignableFrom(rawType)) {
				return (T) new TreeMap<Object, Object>();
			} else if (type instanceof ParameterizedType
					&& !(String.class.isAssignableFrom(TypeToken
					.get(((ParameterizedType) type)
							.getActualTypeArguments()[0]).getRawType()))) {
				return (T) new LinkedHashMap<Object, Object>();
			} else {
				return (T) new HashMap<Object, Object>();
			}
		}

		return null;
	}
}
