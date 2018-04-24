/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qjsonlib.stream;

import com.qjsonlib.JsonException;
import com.qjsonlib.stream.ScopeStack.Scope;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;

/**
 * JSON写入流
 * <p>
 * 提供JSON流的生成方式，写入的数据类型，根据实际的数据调用对应的方法。
 *
 * @author mzw
 *
 */
public final class JsonWriter implements Closeable, Flushable {

	private static final String[] REPLACEMENT_CHARS;
	private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
	static {
		REPLACEMENT_CHARS = new String[128];
		for (int i = 0; i <= 0x1f; i++) {
			REPLACEMENT_CHARS[i] = String.format("\\u%04x", (int) i);
		}
		REPLACEMENT_CHARS['"'] = "\\\"";
		REPLACEMENT_CHARS['\\'] = "\\\\";
		REPLACEMENT_CHARS['\t'] = "\\t";
		REPLACEMENT_CHARS['\b'] = "\\b";
		REPLACEMENT_CHARS['\n'] = "\\n";
		REPLACEMENT_CHARS['\r'] = "\\r";
		REPLACEMENT_CHARS['\f'] = "\\f";
		HTML_SAFE_REPLACEMENT_CHARS = REPLACEMENT_CHARS.clone();
		HTML_SAFE_REPLACEMENT_CHARS['<'] = "\\u003c";
		HTML_SAFE_REPLACEMENT_CHARS['>'] = "\\u003e";
		HTML_SAFE_REPLACEMENT_CHARS['&'] = "\\u0026";
		HTML_SAFE_REPLACEMENT_CHARS['='] = "\\u003d";
		HTML_SAFE_REPLACEMENT_CHARS['\''] = "\\u0027";
	}
	/** 键值分割符 */
	private static final String SEPARATOR = ":";
	/** JSON数据写入流 */
	private final Writer mWriter;
	/** 解析栈 */
	private ScopeStack mScopeStack = new ScopeStack();
	/** 当前写入键 */
	private String mWriteName;

	/**
	 * 构造方法
	 *
	 * @param writer
	 *            JSON数据写入流
	 */
	public JsonWriter(Writer writer) {
		if (writer == null) {
			throw new NullPointerException("writer can't be null");
		}
		mWriter = writer;
	}

	/**
	 * 开始写入数组
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void beginArray() throws JsonException {
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_DOCUMENT) {
			mScopeStack.replaceTop(Scope.NONEMPTY_DOCUMENT);
		} else if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("can't add array at " + scope);
		}

		mScopeStack.push(Scope.EMPTY_ARRAY);
		write("[");
	}

	/**
	 * 结束写入数组
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void endArray() throws JsonException {
		Scope scope = mScopeStack.pop();
		if (scope != Scope.EMPTY_ARRAY && scope != Scope.NONEMPTY_ARRAY) {
			throw new JsonException(
					"Expected EMPTY_ARRAY or NONEMPTY_ARRAY but was " + scope);
		}
		if (mWriteName != null) {
			throw new JsonException("dirty name: " + mWriteName);
		}

		write("]");
	}

	/**
	 * 开始写入对象
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void beginObject() throws JsonException {
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_DOCUMENT) {
			mScopeStack.replaceTop(Scope.NONEMPTY_DOCUMENT);
		} else if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("can't add object at " + scope);
		}

		mScopeStack.push(Scope.EMPTY_OBJECT);
		write("{");
	}

	/**
	 * 结束写入对象
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void endObject() throws JsonException {
		Scope scope = mScopeStack.pop();
		if (scope != Scope.EMPTY_OBJECT && scope != Scope.NONEMPTY_OBJECT) {
			throw new JsonException(
					"Expected EMPTY_OBJECT or NONEMPTY_OBJECT but was " + scope);
		}
		if (mWriteName != null) {
			throw new JsonException("dirty name: " + mWriteName);
		}

		write("}");
	}

	/**
	 * 写入键名
	 *
	 * @param name
	 *            键名
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextName(String name) throws JsonException {
		if (name == null) {
			throw new NullPointerException("name can't be null");
		}
		if (mWriteName != null) {
			throw new JsonException("name already exist");
		}
		mWriteName = name;
	}

	/**
	 * 写入值
	 *
	 * @param value
	 *            值
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextValue(String value) throws JsonException {
		if (value == null) {
			nextNull();
			return;
		}
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		safeWrite(value);
	}

	/**
	 * 写入空值
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextNull() throws JsonException {
		if (mWriteName != null) {
			writeName();
		}

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		write("null");
	}

	/**
	 * 写入值
	 *
	 * @param value
	 *            值
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextValue(boolean value) throws JsonException {
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		write(value ? "true" : "false");
	}

	/**
	 * 写入值
	 *
	 * @param value
	 *            值
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextValue(Double value) throws JsonException {
		if (Double.isNaN(value) || Double.isInfinite(value)) {
			throw new JsonException("Numeric values must be finite, but was "
					+ value);
		}
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		write(Double.toString(value));
	}

	/**
	 * 写入值
	 *
	 * @param value
	 *            值
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextValue(Long value) throws JsonException {
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		write(Long.toString(value));
	}

	/**
	 * 写入值
	 *
	 * @param value
	 *            值
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextValue(Number value) throws JsonException {
		if (value == null) {
			nextNull();
			return;
		}
		writeName();

		Scope scope = mScopeStack.peek();
		if (scope == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (scope == Scope.NONEMPTY_ARRAY) {
			write(",");
		} else if (scope == Scope.DANGLING_NAME) {
			write(SEPARATOR);
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
		} else {
			throw new JsonException("Expected value but was " + scope);
		}

		write(value.toString());
	}

	/**
	 * 输出键名
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	private void writeName() throws JsonException {
		if (mWriteName != null) {
			Scope scope = mScopeStack.peek();
			if (scope == Scope.NONEMPTY_OBJECT) {
				write(",");
			} else if (scope != Scope.EMPTY_OBJECT) {
				throw new JsonException("Nesting problem.");
			}
			mScopeStack.replaceTop(Scope.DANGLING_NAME);
			safeWrite(mWriteName);
			mWriteName = null;
		}
	}

	/**
	 * 转换字符串为兼容类型输出
	 *
	 * @param value
	 *            字符串值
	 * @throws JsonException
	 *             错误信息
	 */
	private void safeWrite(String value) throws JsonException {
		String[] replacements = HTML_SAFE_REPLACEMENT_CHARS;
		write("\"");
		int last = 0;
		int length = value.length();
		for (int i = 0; i < length; i++) {
			char c = value.charAt(i);
			String replacement;
			if (c < 128) {
				replacement = replacements[c];
				if (replacement == null) {
					continue;
				}
			} else if (c == '\u2028') {
				replacement = "\\u2028";
			} else if (c == '\u2029') {
				replacement = "\\u2029";
			} else {
				continue;
			}
			if (last < i) {
				write(value, last, i - last);
			}
			write(replacement);
			last = i + 1;
		}
		if (last < length) {
			write(value, last, length - last);
		}
		write("\"");
	}

	/**
	 * 输出字符串
	 *
	 * @param str
	 *            字符串
	 * @throws JsonException
	 *             错误信息
	 */
	private void write(String str) throws JsonException {
		try {
			mWriter.write(str);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	/**
	 * 输出字符串
	 *
	 * @param str
	 *            字符串
	 * @param off
	 *            偏移位置
	 * @param len
	 *            输出长度
	 * @throws JsonException
	 *             错误信息
	 */
	private void write(String str, int off, int len) throws JsonException {
		try {
			mWriter.write(str, off, len);
		} catch (IOException e) {
			throw new JsonException(e);
		}
	}

	@Override
	public void flush() throws IOException {
		mWriter.flush();
	}

	@Override
	public void close() throws IOException {
		mWriter.close();
		mScopeStack.close();
	}

}
