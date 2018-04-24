package com.qjsonlib.converter;

import com.qjsonlib.Converters;
import com.qjsonlib.JsonException;
import com.qjsonlib.Log;
import com.qjsonlib.annotation.JsonIgnore;
import com.qjsonlib.annotation.JsonName;
import com.qjsonlib.internal.Primitives;
import com.qjsonlib.stream.JsonReader;
import com.qjsonlib.stream.JsonReader.JsonToken;
import com.qjsonlib.stream.JsonWriter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Java对象转换工具
 *
 * @author mzw
 *
 * @param <T>
 */
public final class JavaBeanConverter<T> extends TypeConverter<T> {
	private static final String TAG = "com.qjsonlib.converter.JavaBeanConverter";
	/** 对象类型 */
	private TypeToken<T> mTypeToken;
	/** 字段解析集 */
	private Map<String, BoundField> mBoundFields;

	/**
	 * 构造方法
	 *
	 * @param typeToken
	 *            对象类型
	 */
	public JavaBeanConverter(TypeToken<T> typeToken) {
		mTypeToken = typeToken;
		mBoundFields = getBoundFields(typeToken);
	}

	@Override
	public T read(JsonReader jsonReader) throws JsonException {
		if (jsonReader.next() != JsonToken.BEGIN_OBJECT) {
			jsonReader.skipValue();
			Log.w(TAG, "Expected a OBJECT, but was " + jsonReader.next());
			return null;
		}

		T javaObject = null;

		jsonReader.beginObject();
		if (jsonReader.hasNext()) {
			javaObject = constructor(mTypeToken);
		}
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			BoundField field = mBoundFields.get(name);
			if (field == null) {
				jsonReader.skipValue();
			} else {
				field.read(jsonReader, javaObject);
			}
		}
		jsonReader.endObject();

		return javaObject;
	}

	@Override
	public void write(JsonWriter jsonWriter, T value) throws JsonException {
		if (value == null) {
			jsonWriter.nextNull();
			return;
		}

		jsonWriter.beginObject();
		for (BoundField boundField : mBoundFields.values()) {
			jsonWriter.nextName(boundField.name);
			boundField.write(jsonWriter, value);
		}
		jsonWriter.endObject();
	}

	/**
	 * 获取对象字段解析集合
	 *
	 * @param typeToken
	 *            对象类型
	 * @return 字段解析集合
	 */
	private Map<String, BoundField> getBoundFields(TypeToken<?> typeToken) {
		Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();

		Class<?> rawClazz = typeToken.getRawType();
		if (rawClazz.isInterface()) {
			return result;
		}

		while (rawClazz != Object.class) {// 循环读取父类字段
			Field[] fields = rawClazz.getDeclaredFields();
			for (Field field : fields) {
				field.setAccessible(true);

				String name = getFieldName(field);
				if (name == null) {
					continue;
				}

				Type fieldType = JSONTypes.resolve(typeToken.getType(),
						rawClazz, field.getGenericType());
				Method setter = getFieldSetMethod(rawClazz, field);
				Method getter = getFieldGetMethod(rawClazz, field);
				if (setter == null || getter == null) {
					continue;
				}
				BoundField boundField = new BoundField(name, getter, setter,
						TypeToken.get(fieldType));
				BoundField previous = result.put(boundField.name, boundField);
				if (previous != null) {
					throw new IllegalArgumentException(typeToken.getType()
							+ " declares multiple JSON fields named "
							+ previous.name);
				}
			}

			typeToken = TypeToken.get(JSONTypes.resolve(typeToken.getType(),
					rawClazz, rawClazz.getGenericSuperclass()));
			rawClazz = typeToken.getRawType();
		}

		return result;
	}

	/**
	 * 获取字段名
	 *
	 * @param field
	 *            字段
	 * @return 字段名
	 */
	private String getFieldName(Field field) {
		JsonIgnore ignore = field.getAnnotation(JsonIgnore.class);
		if (ignore != null) {
			return null;
		}

		if (Modifier.isFinal(field.getModifiers())) {
			return null;
		}

		if (Modifier.isStatic(field.getModifiers())) {
			return null;
		}

		JsonName name = field.getAnnotation(JsonName.class);
		return name != null ? name.value() : field.getName();
	}

	/**
	 * 获取字段赋值方法
	 *
	 * @param clazz
	 *            对象类型
	 * @param field
	 *            字段
	 * @return 字段赋值方法
	 */
	public static Method getFieldSetMethod(Class<?> clazz, Field field) {
		if (field.getType() == boolean.class) {
			Method method = getBooleanSetMethod(clazz, field);
			if (method != null) {
				return method;
			}
		}

		String fieldName = field.getName();
		String methodName = "set" + Character.toUpperCase(fieldName.charAt(0))
				+ fieldName.substring(1);
		try {
			return clazz.getDeclaredMethod(methodName, field.getType());
		} catch (NoSuchMethodException ignore) {
		}
		return null;
	}

	/**
	 * 获取字段赋值方法(boolean)
	 *
	 * @param clazz
	 *            对象类型
	 * @param field
	 *            字段
	 * @return 字段赋值方法
	 */
	public static Method getBooleanSetMethod(Class<?> clazz, Field field) {
		String fieldName = field.getName();
		String methodName;
		if (fieldName.startsWith("is")
				&& Character.isUpperCase(fieldName.charAt(2))) {
			methodName = "set"
					+ fieldName.substring(2, 3).toUpperCase(Locale.US)
					+ fieldName.substring(3);
		} else {
			methodName = "set"
					+ fieldName.substring(0, 1).toUpperCase(Locale.US)
					+ fieldName.substring(1);
		}
		try {
			return clazz.getDeclaredMethod(methodName, field.getType());
		} catch (NoSuchMethodException ignore) {
		}
		return null;
	}

	/**
	 * 获取字段取值方法
	 *
	 * @param clazz
	 *            对象类型
	 * @param field
	 *            字段
	 * @return 字段取值方法
	 */
	public static Method getFieldGetMethod(Class<?> clazz, Field field) {
		if (field.getType() == boolean.class) {
			Method method = getBooleanFieldGetMethod(clazz, field);
			if (method != null) {
				return method;
			}
		}

		String fieldName = field.getName();
		String methodName = "get" + Character.toUpperCase(fieldName.charAt(0))
				+ fieldName.substring(1);
		try {
			return clazz.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException ignore) {
		}
		return null;
	}

	/**
	 * 获取字段取值方法(boolean)
	 *
	 * @param clazz
	 *            对象类型
	 * @param field
	 *            字段
	 * @return 字段取值方法
	 */
	public static Method getBooleanFieldGetMethod(Class<?> clazz, Field field) {
		String fieldName = field.getName();
		String methodName;
		if (fieldName.startsWith("is")
				&& Character.isUpperCase(fieldName.charAt(2))) {
			methodName = fieldName;
		} else {
			methodName = "is"
					+ fieldName.substring(0, 1).toUpperCase(Locale.US)
					+ fieldName.substring(1);
		}
		try {
			return clazz.getDeclaredMethod(methodName);
		} catch (NoSuchMethodException ignore) {
		}
		return null;
	}

	/**
	 * 字段数据读取类
	 *
	 * @author mzw
	 *
	 */
	static class BoundField {
		/** 字段名 */
		final String name;
		/** 取值方法 */
		final Method getter;
		/** 赋值方法 */
		final Method setter;
		/** 字段类型 */
		final TypeToken<?> typeToken;
		/** 是否基本类型 */
		final boolean isPrimitives;

		/**
		 * 构造方法
		 *
		 * @param name
		 *            字段名
		 * @param getter
		 *            取值方法
		 * @param setter
		 *            赋值方法
		 * @param typeToken
		 *            字段类型
		 */
		public BoundField(String name, Method getter, Method setter,
						  TypeToken<?> typeToken) {
			this.name = name;
			this.getter = getter;
			this.setter = setter;
			this.typeToken = typeToken;
			isPrimitives = Primitives.isPrimitive(typeToken.getRawType());
		}

		/**
		 * 读取JSON数据，转换为字段值
		 *
		 * @param jsonReader
		 *            JSON读取流
		 * @param value
		 *            对象实例
		 * @throws JsonException
		 *             错误信息
		 */
		void read(JsonReader jsonReader, Object value) throws JsonException {
			TypeConverter<?> typeConverter = Converters.getConverters()
					.getConverter(typeToken);
			Object fieldValue = typeConverter.read(jsonReader);

			if (fieldValue != null || !isPrimitives) {
				try {
					setter.invoke(value, fieldValue);
				} catch (Exception ignore) {
				}
			}
		}

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
		@SuppressWarnings("unchecked")
		void write(JsonWriter jsonWriter, Object value) throws JsonException {
			TypeConverter<Object> typeConverter = (TypeConverter<Object>) Converters
					.getConverters().getConverter(typeToken);
			Object fieldValue = null;
			try {
				fieldValue = getter.invoke(value);
			} catch (Exception ignore) {
				return;
			}
			typeConverter.write(jsonWriter, fieldValue);
		}
	}
}
