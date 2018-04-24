package com.qjsonlib.converter;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * 对象类型获取工具
 *
 * @author mzw
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class TypeToken<T> {
	final Class<? super T> rawType;
	final Type type;
	final int hashCode;

	protected TypeToken() {
		this.type = JSONTypes.getSuperclassTypeParameter(getClass());
		this.rawType = (Class<? super T>) getRawType(type);
		this.hashCode = type.hashCode();
	}

	TypeToken(Type type) {
		this.type = type;
		this.rawType = (Class<? super T>) getRawType(type);
		this.hashCode = type.hashCode();
	}

	public final Class<? super T> getRawType() {
		return rawType;
	}

	public final Type getType() {
		return type;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public final static TypeToken<?> get(Type type) {
		return new TypeToken<Object>(type);
	}

	static Class<?> getRawType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		} else if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type rawType = parameterizedType.getRawType();
			return (Class<?>) rawType;
		} else if (type instanceof GenericArrayType) {
			Type componentType = ((GenericArrayType) type)
					.getGenericComponentType();
			return Array.newInstance(getRawType(componentType), 0).getClass();
		} else if (type instanceof TypeVariable) {
			return Object.class;
		} else if (type instanceof WildcardType) {
			return getRawType(((WildcardType) type).getUpperBounds()[0]);
		} else {
			String className = type == null ? "null" : type.getClass()
					.getName();
			throw new IllegalArgumentException(
					"Expected a Class, ParameterizedType, or "
							+ "GenericArrayType, but <" + type
							+ "> is of type " + className);
		}
	}
}
