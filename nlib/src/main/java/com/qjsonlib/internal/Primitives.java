package com.qjsonlib.internal;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Primitives {
	private Primitives() {
	}

	private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER_TYPE;
	@SuppressWarnings("unused")
	private static final Map<Class<?>, Class<?>> WRAPPER_TO_PRIMITIVE_TYPE;

	static {
		Map<Class<?>, Class<?>> primToWrap = new HashMap<Class<?>, Class<?>>(16);
		Map<Class<?>, Class<?>> wrapToPrim = new HashMap<Class<?>, Class<?>>(16);

		add(primToWrap, wrapToPrim, boolean.class, Boolean.class);
		add(primToWrap, wrapToPrim, byte.class, Byte.class);
		add(primToWrap, wrapToPrim, char.class, Character.class);
		add(primToWrap, wrapToPrim, double.class, Double.class);
		add(primToWrap, wrapToPrim, float.class, Float.class);
		add(primToWrap, wrapToPrim, int.class, Integer.class);
		add(primToWrap, wrapToPrim, long.class, Long.class);
		add(primToWrap, wrapToPrim, short.class, Short.class);
		add(primToWrap, wrapToPrim, void.class, Void.class);

		PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
		WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
	}

	private static void add(Map<Class<?>, Class<?>> forward,
			Map<Class<?>, Class<?>> backward, Class<?> key, Class<?> value) {
		forward.put(key, value);
		backward.put(value, key);
	}

	public static boolean isPrimitive(Type type) {
		return PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type);
	}
}
