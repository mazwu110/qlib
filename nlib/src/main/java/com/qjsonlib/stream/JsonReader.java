package com.qjsonlib.stream;

import com.qjsonlib.JsonException;
import com.qjsonlib.stream.ScopeStack.Scope;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

/**
 * JSON读取流
 * <p>
 * 提供JSON的读取方式，具体的数据类型，可根据{@linkplain JsonReader#next() <span
 * color="#0000FF">JsonReader.next()</span>}获取。
 *
 * @author mzw
 *
 */
public final class JsonReader implements Closeable {
	/** 短字符串缓存池 */
	private static final StringPool mStringPool = new StringPool();

	/** JSON字符串读取流 */
	private final Reader mReader;
	/** JSON字符串缓存区 */
	private char[] mBuffer = new char[1024];
	/** 缓存区开始位置 */
	private int mPos = 0;
	/** 缓存区结束位置 */
	private int mLimit = 0;
	/** JSON字符串读取位置 */
	private int mCurrentPos = 0;
	/** 预存值 */
	private String mNextValue;
	/** JSON字符串解析栈 */
	private ScopeStack mScopeStack = new ScopeStack();
	/** 当前解析的值类型 */
	private JsonToken mJsonToken = JsonToken.NONE;

	/**
	 * 构造方法
	 *
	 * @param json
	 *            JSON数据
	 */
	public JsonReader(String json) {
		this(new StringReader(json));
	}

	/**
	 * 构造方法
	 *
	 * @param reader
	 *            JSON数据读取流
	 */
	public JsonReader(Reader reader) {
		if (reader == null) {
			throw new NullPointerException("reader can't be null");
		}
		mReader = reader;
	}

	/**
	 * 开始读取数组
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void beginArray() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		if (token == JsonToken.BEGIN_ARRAY) {
			mScopeStack.push(Scope.EMPTY_ARRAY);
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected BEGIN_ARRAY but was " + next(),
					mNextValue);
		}
	}

	/**
	 * 结束读取数组
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void endArray() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		if (token == JsonToken.END_ARRAY) {
			mScopeStack.pop();
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected END_ARRAY but was " + next(),
					mNextValue);
		}
	}

	/**
	 * 开始读取对象
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void beginObject() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		if (token == JsonToken.BEGIN_OBJECT) {
			mScopeStack.push(Scope.EMPTY_OBJECT);
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected BEGIN_OBJECT but was " + next(),
					mNextValue);
		}
	}

	/**
	 * 结束读取对象
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void endObject() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		if (token == JsonToken.END_OBJECT) {
			mScopeStack.pop();
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected END_OBJECT but was " + next(),
					mNextValue);
		}
	}

	/**
	 * 是否有值读取
	 *
	 * @return true 是
	 *         <p>
	 *         false 否
	 * @throws JsonException
	 *             错误信息
	 */
	public boolean hasNext() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		return token != JsonToken.END_OBJECT && token != JsonToken.END_ARRAY;
	}

	/**
	 * 读取键名
	 *
	 * @return 键名
	 * @throws JsonException
	 *             错误信息
	 */
	public String nextName() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		String result;
		if (token == JsonToken.NAME) {
			result = mNextValue;
		} else {
			throw syntaxException("Expected a name but was " + next(),
					mNextValue);
		}

		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取字符串
	 *
	 * @return 字符串
	 * @throws JsonException
	 *             错误信息
	 */
	public String nextString() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		String result;
		if (token == JsonToken.STRING || token == JsonToken.NUMBER
				|| token == JsonToken.BOOLEAN) {
			result = mNextValue;
		} else {
			throw syntaxException("Expected a string but was " + next(),
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取空值
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void nextNull() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		if (token == JsonToken.NULL) {
			mNextValue = null;
			mJsonToken = JsonToken.NONE;
		} else {
			throw syntaxException("Expected null but was " + next(), mNextValue);
		}
	}

	/**
	 * 读取Boolean值
	 *
	 * @return Boolean值
	 * @throws JsonException
	 *             错误信息
	 */
	public Boolean nextBoolean() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		Boolean result;
		if (token == JsonToken.BOOLEAN) {
			result = Boolean.parseBoolean(mNextValue);
		} else {
			throw syntaxException("Expected a boolean but was " + next(),
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取Double值
	 *
	 * @return Double值
	 * @throws JsonException
	 *             错误信息
	 */
	public Double nextDouble() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		double result;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			result = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a double but was " + next(),
					mNextValue);
		}

		// 判断是否非法
		if ((Double.isNaN(result) || Double.isInfinite(result))) {
			throw syntaxException("JSON forbids NaN and infinities: " + result,
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取Float值
	 *
	 * @return Float值
	 * @throws JsonException
	 *             错误信息
	 */
	public Float nextFloat() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a float but was " + next(),
					mNextValue);
		}

		float result = (float) asDouble;
		if (result != asDouble) { // 确认是否精度丢失
			throw syntaxException("Expected an float but was " + next(),
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取Long值
	 *
	 * @return Long值
	 * @throws JsonException
	 *             错误信息
	 */
	public Long nextLong() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a long but was " + next(),
					mNextValue);
		}

		long result = (long) asDouble;
		if (result != asDouble) { // 确认是否精度丢失
			throw syntaxException("Expected an long but was " + next(),
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 读取Integer值
	 *
	 * @return Integer值
	 * @throws JsonException
	 *             错误信息
	 */
	public Integer nextInt() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		double asDouble;
		if (token == JsonToken.NUMBER || token == JsonToken.STRING) {
			asDouble = Double.parseDouble(mNextValue);
		} else {
			throw syntaxException("Expected a int but was " + next(),
					mNextValue);
		}

		int result = (int) asDouble;
		if (result != asDouble) { // 确认是否精度丢失
			throw syntaxException("Expected a int but was " + next(),
					mNextValue);
		}

		mNextValue = null;
		mJsonToken = JsonToken.NONE;
		return result;
	}

	/**
	 * 略过该值的读取
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	public void skipValue() throws JsonException {
		int count = 0;
		do {
			JsonToken token = mJsonToken;
			if (token == JsonToken.NONE) {
				token = doPeek();
			}

			if (token == JsonToken.BEGIN_ARRAY) {
				mScopeStack.push(Scope.EMPTY_ARRAY);
				count++;
			} else if (token == JsonToken.BEGIN_OBJECT) {
				mScopeStack.push(Scope.EMPTY_OBJECT);
				count++;
			} else if (token == JsonToken.END_ARRAY) {
				mScopeStack.pop();
				count--;
			} else if (token == JsonToken.END_OBJECT) {
				mScopeStack.pop();
				count--;
			} else if (token == JsonToken.STRING || token == JsonToken.NUMBER) {
				mNextValue = null;
			}

			mJsonToken = JsonToken.NONE;
		} while (count != 0);
	}

	/**
	 * 读取下个值
	 *
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonToken next() throws JsonException {
		JsonToken token = mJsonToken;
		if (token == JsonToken.NONE) {
			token = doPeek();
		}

		switch (token) {
			case NONE:
				throw new AssertionError();
			default:
				return token;
		}
	}

	/**
	 * 读取值
	 *
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	public JsonToken doPeek() throws JsonException {
		Scope peekStack = mScopeStack.peek();
		if (peekStack == Scope.EMPTY_DOCUMENT) {
			// // 过滤不规范的开头
			// consumeNonExecutePrefix();
			// 标志开始读取流内容
			mScopeStack.replaceTop(Scope.NONEMPTY_DOCUMENT);
		} else if (peekStack == Scope.NONEMPTY_DOCUMENT) {
			int c = nextNonWhitespace(false);// 读取下个字符至数组缓存中
			if (c == -1) {// 流内容已读取结束
				return mJsonToken = JsonToken.END_DOCUMENT;
			} else {
				backward();
			}
		} else if (peekStack == Scope.EMPTY_OBJECT
				|| peekStack == Scope.NONEMPTY_OBJECT) {
			mScopeStack.replaceTop(Scope.DANGLING_NAME);

			// 在下个元素之前，查找到逗号
			if (peekStack == Scope.NONEMPTY_OBJECT) {
				int c = nextNonWhitespace(true);
				switch (c) {
					case '}':// 读取到对象结束标志
						return mJsonToken = JsonToken.END_OBJECT;
					case ';':
					case ',':// 下个对象之前必须的逗号
						break;
					default:
						throw syntaxException("Unterminated object", mNextValue);
				}
			}

			// 正式读取对象的第一个元素的键
			int c = nextNonWhitespace(true);
			switch (c) {
				case '"':// 正确的读取到键的双引号
					nextQuotedValue('"');
					return mJsonToken = JsonToken.NAME;
				case '\'':// 兼容版本的读取单引号
					nextQuotedValue('\'');
					return mJsonToken = JsonToken.NAME;
				case '}':// 对象结束
					if (peekStack != Scope.NONEMPTY_OBJECT) {
						return mJsonToken = JsonToken.END_OBJECT;
					} else {
						// 未读取到键
						throw syntaxException("Expected name", mNextValue);
					}
				default:// 读取到的是无引号的键
					backward();
					if (isLiteral((char) c)) {// 确认读取到的不是非法字符
						nextUnquotedValue();
						return mJsonToken = JsonToken.NAME;
					} else {
						throw syntaxException("Expected name", mNextValue);
					}
			}
		} else if (peekStack == Scope.DANGLING_NAME) {
			mScopeStack.replaceTop(Scope.NONEMPTY_OBJECT);
			// 查找在键值之间的冒号:
			int c = nextNonWhitespace(true);
			switch (c) {
				case ':':// 正确获取
					break;
				case '=':// 兼容JSON格式
					break;
				default:
					throw syntaxException("Expected ':'", mNextValue);
			}
		} else if (peekStack == Scope.EMPTY_ARRAY) {
			mScopeStack.replaceTop(Scope.NONEMPTY_ARRAY);
		} else if (peekStack == Scope.NONEMPTY_ARRAY) {
			int c = nextNonWhitespace(true);
			switch (c) {
				case ']':
					return mJsonToken = JsonToken.END_ARRAY;
				case ';':
				case ',':
					break;
				default:
					throw syntaxException("Unterminated array", mNextValue);
			}
		} else if (peekStack == Scope.CLOSED) {
			throw new JsonException("JsonReader is closed");
		}

		int c = nextNonWhitespace(true);
		switch (c) {
			case '{':// 读取到对象开始标志
				return mJsonToken = JsonToken.BEGIN_OBJECT;
			case '[':// 读取到数组开始标志
				return mJsonToken = JsonToken.BEGIN_ARRAY;
			case ']':
				if (peekStack == Scope.EMPTY_ARRAY) {
					return mJsonToken = JsonToken.END_ARRAY;
				}
			case ';':
			case ',':
				if (peekStack == Scope.EMPTY_ARRAY
						|| peekStack == Scope.NONEMPTY_ARRAY) {
					backward();
					return mJsonToken = JsonToken.NULL;
				} else {
					throw syntaxException("Unexpected value", mNextValue);
				}
			case '\'':
				return mJsonToken = nextQuotedValue('\'');
			case '"':// 读取键值对的值
				return mJsonToken = nextQuotedValue('"');
			default:// 退回栈
				backward();
		}

		JsonToken result = nextKeyWord();
		if (result != JsonToken.NONE) {
			return mJsonToken = result;
		}

		result = nextNumber();
		if (result != JsonToken.NONE) {
			return mJsonToken = result;
		}

		if (!isLiteral(mBuffer[mPos])) {
			throw syntaxException("Expected value", mNextValue);
		}

		return mJsonToken = nextUnquotedValue();
	}

	/**
	 * 字符流后退
	 */
	public void backward() {
		mCurrentPos--;
		mPos--;
	}

	/**
	 * 读取关键字
	 *
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	private JsonToken nextKeyWord() throws JsonException {
		// 根据首个字符匹配关键字
		char c = mBuffer[mPos];
		String keyword;
		String keywordUpper;
		JsonToken token;
		if (c == 't' || c == 'T') {
			keyword = "true";
			keywordUpper = "TRUE";
			token = JsonToken.BOOLEAN;
		} else if (c == 'f' || c == 'F') {
			keyword = "false";
			keywordUpper = "FALSE";
			token = JsonToken.BOOLEAN;
		} else if (c == 'n' || c == 'N') {
			keyword = "null";
			keywordUpper = "NULL";
			token = JsonToken.NULL;
		} else {
			return JsonToken.NONE;
		}

		// 逐一匹配关键字的值[1..length]
		int length = keyword.length();
		for (int i = 1; i < length; i++) {
			if (mPos + i >= mLimit && !fillBuffer(i + 1)) {
				return JsonToken.NONE;
			}

			c = mBuffer[mPos + i];

			if (c != keyword.charAt(i) && c != keywordUpper.charAt(i)) {
				return JsonToken.NONE;
			}
		}

		// 值读取到true，false，null就结束，完全符合
		if ((mPos + length < mLimit || fillBuffer(length + 1))
				&& isLiteral(mBuffer[mPos + length])) {
			return JsonToken.NONE;
		}

		mNextValue = new String(mBuffer, mPos, length);
		mCurrentPos += length;
		mPos += length;
		return token;
	}

	/**
	 * 读取数字
	 *
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	private JsonToken nextNumber() throws JsonException {
		char[] buffer = mBuffer;
		int p = mPos;
		int l = mLimit;
		int i = 0;

		/** 开始读取数字 */
		int NUMBER_NONE = 0;
		/** 读取到负号 */
		int NUMBER_SIGN = 1;
		/** 读取到数字 */
		int NUMBER_DIGIT = 2;
		/** 读取到小数点 */
		int NUMBER_DOT = 3;
		/** 读取到分数的数字 */
		int NUMBER_FRACTION_DIGIT = 4;
		/** 读取到指数标志E或e */
		int NUMBER_EXP_E = 5;
		/** 读取到跟随在指数标志后的-+号 */
		int NUMBER_EXP_SIGN = 6;
		/** 读取到指数后面的数字 */
		int NUMBER_EXP_DIGIT = 7;

		int last = NUMBER_NONE;
		long value = 0;

		for (; true; i++) {
			if (p + i == l) {
				if (!fillBuffer(i + 1)) {
					return JsonToken.NONE;
				}
				p = mPos;
				l = mLimit;
			}

			char c = buffer[p + i];
			if (c == '-') {
				if (last == NUMBER_NONE) {
					last = NUMBER_SIGN;
					continue;
				}
				if (last == NUMBER_EXP_E) {
					last = NUMBER_EXP_SIGN;
					continue;
				}
				return JsonToken.NONE;
			} else if (c == '+') {
				if (last == NUMBER_EXP_E) {
					last = NUMBER_EXP_SIGN;
					continue;
				}
				return JsonToken.NONE;
			} else if (c == '.') {
				if (last == NUMBER_DIGIT) {
					last = NUMBER_DOT;
					continue;
				}
				return JsonToken.NONE;
			} else if (c == 'e' || c == 'E') {
				if (last == NUMBER_FRACTION_DIGIT || last == NUMBER_DIGIT) {
					last = NUMBER_EXP_E;
					continue;
				}
				return JsonToken.NONE;
			} else {
				if (c < '0' || c > '9') {
					if (!isLiteral(c)) {
						break;
					}
					return JsonToken.NONE;
				}
				if (last == NUMBER_NONE || last == NUMBER_SIGN) {
					value = -(c - '0');
					last = NUMBER_DIGIT;
				} else if (last == NUMBER_DIGIT) {
					if (value == 0) {
						return JsonToken.NONE;
					}

					long newValue = value * 10 - (c - '0');
					value = newValue;
				} else if (last == NUMBER_DOT) {
					last = NUMBER_FRACTION_DIGIT;
				} else if (last == NUMBER_EXP_E || last == NUMBER_EXP_SIGN) {
					last = NUMBER_EXP_DIGIT;
				}
			}
		}

		if (last == NUMBER_DIGIT || last == NUMBER_FRACTION_DIGIT
				|| last == NUMBER_EXP_DIGIT) {
			mNextValue = new String(mBuffer, mPos, i);
			mCurrentPos += i;
			mPos += i;
			return JsonToken.NUMBER;
		} else {
			return JsonToken.NONE;
		}
	}

	/**
	 * 读取一个不带引号的值，并作为字符串返回。
	 *
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	private JsonToken nextUnquotedValue() throws JsonException {
		StringBuilder builder = null;
		int i = 0;

		while (true) {
			// 循环检查剩余的字符，判断是否遇到隔断字符
			for (; mPos + i < mLimit; i++) {
				char c = mBuffer[mPos + i];
				if (c == '/' || c == '\\' || c == ';' || c == '#' || c == '=') {
					break;
				}
				if (c == '{' || c == '}' || c == '[' || c == ']' || c == ':'
						|| c == ',' || c == ' ' || c == '\t' || c == '\f'
						|| c == '\n' || c == '\r') {
					break;
				}
			}

			// 检查到隔断字符
			if (mPos + i < mLimit) {
				break;
			}

			// 当把剩余的字符读取结束，但未检测到隔断字符时，需要调整数组缓存空间，
			// 使所需字符填充满数组缓存，达到最大利用缓存的目的。
			if (i < mBuffer.length) {
				if (fillBuffer(i + 1)) {
					continue;
				} else {
					break;
				}
			}

			// 当读取完整个数组缓存，并且未读取到隔断字符时，该值已过长，需要使用StringBuilder来进行缓存
			if (builder == null) {
				builder = new StringBuilder();
			}
			builder.append(mBuffer, mPos, i);
			mCurrentPos += i;
			mPos += i;
			i = 0;
			if (!fillBuffer(1)) {
				break;
			}
		}

		String result;
		if (builder == null) {
			result = new String(mBuffer, mPos, i);
		} else {
			builder.append(mBuffer, mPos, i);
			result = builder.toString();
		}

		mCurrentPos += i;
		mPos += i;
		mNextValue = result;
		return JsonToken.STRING;
	}

	/**
	 * 返回下一个引号之前的字符串，不包括引号。
	 *
	 * @param quoted
	 *            引号
	 * @return 值类型
	 * @throws JsonException
	 *             错误信息
	 */
	private JsonToken nextQuotedValue(char quoted) throws JsonException {
		char[] buffer = mBuffer;
		StringBuilder builder = null;
		int hashCode = 0;
		while (true) {
			int p = mPos;
			int l = mLimit;
			// 当前未添加到builder内的字符串的第一个字符位置
			int start = p;
			while (p < l) {// 逐一匹配当前缓存区的字符
				int c = buffer[p++];

				if (c == quoted) {// 匹配到对于的引号
					mCurrentPos += p - mPos;
					mPos = p;
					if (builder == null) {
						mNextValue = mStringPool.get(buffer, start, p - start
								- 1, hashCode);
					} else {
						builder.append(buffer, start, p - start - 1);
						mNextValue = builder.toString();
					}
					return JsonToken.STRING;
				} else if (c == '\\') {// 转义字符
					mCurrentPos += p - mPos;
					mPos = p;
					if (builder == null) {
						builder = new StringBuilder();
					}
					builder.append(buffer, start, p - start - 1);
					builder.append(readEscapeCharacter());

					p = mPos;
					l = mLimit;
					start = p;
				} else {// 其他字符
					hashCode = (hashCode * 31) + c;
				}
			}

			if (builder == null) {
				builder = new StringBuilder();
			}
			builder.append(buffer, start, p - start);
			mCurrentPos += p - mPos;
			mPos = p;
			if (!fillBuffer(1)) {
				throw syntaxException("Unterminated string", builder.toString());
			}
		}
	}

	/**
	 *
	 * 返回流中的下一个字符，该字符不能为空格或注释的一部分。返回后，该字符始终存储在数组缓存中的mPos-1的位置。
	 *
	 * @param throwOnEof
	 *            读取到结束是否抛出异常
	 * @return 字符
	 * @throws JsonException
	 *             错误信息
	 */
	private int nextNonWhitespace(boolean throwOnEof) throws JsonException {
		char[] buffer = mBuffer;
		int p = mPos;
		int l = mLimit;

		while (true) {
			if (p == l) {// 数组缓存内数据已读取结束
				mCurrentPos += p - mPos;
				mPos = p;
				if (!fillBuffer(1)) {
					break;
				}
				p = mPos;
				l = mLimit;
			}

			int c = buffer[p++];
			if (c == '\n' || c == ' ' || c == '\r' || c == '\t') {// 读取到换行符、空格或制表符号
				continue;
			}

			if (c == '/') {// 过滤注解
				mCurrentPos += p - mPos;
				mPos = p;
				if (p == l) {// 读取后面的字符
					mCurrentPos--;
					mPos--;// 将'/'放置回数组缓存
					boolean charsLoaded = fillBuffer(2);
					mCurrentPos++;
					mPos++;// 重新读取'/'字符
					if (!charsLoaded) {// 后面没有字符，非注解，返回该字符
						return c;
					}
				}

				char peek = buffer[mPos];
				switch (peek) {
					case '*':// 多行注解
						mCurrentPos++;
						mPos++;// 跳过多行注解的开头'*'
						if (!skipTo("*/")) {// 跳转到多行注解的末尾
							throw syntaxException("Unterminated comment", null);
						}
						p = mPos + 2;
						l = mLimit;
						continue;
					case '/':// 单行注解
						mCurrentPos++;
						mPos++;// 跳过第二个'/'
						skipToEndOfLine();// 跳过"//"后面一行的注解内容
						p = mPos;
						l = mLimit;
						continue;
					default: // 非注解
						return c;
				}
			} else if (c == '#') {// 跳过#之后的哈希注解。JSON的RFC没有特别指出，但需要在解析中指出。http://b/2571423
				mCurrentPos += p - mPos;
				mPos = p;
				skipToEndOfLine();
				p = mPos;
				l = mLimit;
			} else {// 读取到所需数据
				mCurrentPos += p - mPos;
				mPos = p;
				return c;
			}
		}

		if (throwOnEof) {
			throw syntaxException("Unterminated of input", null);
		} else {
			return -1;
		}
	}

	/**
	 * 读取字符，直到换行符。如果换行符由"\r\n"组成，需要由调用该方法的调用者自行消除\n。
	 *
	 * @throws JsonException
	 *             错误信息
	 */
	private void skipToEndOfLine() throws JsonException {
		while (mPos < mLimit || fillBuffer(1)) {
			mCurrentPos++;
			char c = mBuffer[mPos++];
			if (c == '\n' || c == '\r') {
				break;
			}
		}
	}

	/**
	 * 过滤夹杂在JSON字符串中的多行注释
	 *
	 * @param toFind
	 *            * 或者/
	 * @return true 过滤完成
	 *         <p>
	 *         false 过滤失败
	 * @throws JsonException
	 *             错误信息
	 */
	private boolean skipTo(String toFind) throws JsonException {
		while (mPos + toFind.length() <= mLimit || fillBuffer(toFind.length())) {
			if (mBuffer[mPos] == '\n') {
				mCurrentPos++;
				mPos++;
				continue;
			}

			int i = 0;
			while (i < toFind.length()) {
				if (mBuffer[mPos + 1] != toFind.charAt(i)) {
					break;
				}
				i++;
			}

			if (i >= toFind.length()) {
				return true;
			} else {
				mCurrentPos++;
				mPos++;
			}
		}

		return false;
	}

	/**
	 * 读取转义字符，支持读取Unicode转义符及\n、\r、\b、\f。
	 *
	 * @return 转义字符
	 * @throws JsonException
	 *             错误信息
	 */
	private char readEscapeCharacter() throws JsonException {
		if (mPos == mLimit && !fillBuffer(1)) {
			throw syntaxException("Unterminated escape sequence", null);
		}

		char escaped = mBuffer[mPos++];
		switch (escaped) {
			case 'u':
				if (mPos + 4 > mLimit && !fillBuffer(4)) {
					throw syntaxException("Unterminated escape sequence",
							new String(mBuffer, mPos, mLimit - mPos));
				}

				// Unicode16进制转换
				char result = 0;
				for (int i = mPos, end = i + 4; i < end; i++) {
					char c = mBuffer[i];
					result <<= 4;
					if (c >= '0' && c <= '9') {
						result += (c - '0');
					} else if (c >= 'a' && c <= 'f') {
						result += (c - 'a' + 10);
					} else if (c >= 'A' && c <= 'F') {
						result += (c = 'A' + 10);
					} else {
						throw syntaxException("Unicode \\u"
										+ new String(mBuffer, mPos, 4) + "transform error",
								null);
					}
				}
				mCurrentPos += 4;
				mPos += 4;
				return result;
			case 't':
				return '\t';
			case 'b':
				return '\b';
			case 'n':
				return '\n';
			case 'r':
				return '\r';
			case 'f':
				return '\f';
			case '\n':
			case '\'':
			case '"':
			case '\\':
			default:
				return escaped;
		}
	}

	/**
	 * 是否普通字符
	 *
	 * @param c
	 *            字符
	 * @return true 为普通字符
	 */
	private boolean isLiteral(char c) {
		switch (c) {
			case '/':
			case '\\':
			case ';':
			case '#':
			case '=':
			case '{':
			case '}':
			case '[':
			case ']':
			case ':':
			case ',':
			case ' ':
			case '\t':
			case '\f':
			case '\r':
			case '\n':
				return false;
			default:
				return true;
		}
	}

	/**
	 * 缓存流值缓存区
	 *
	 * @param min
	 *            最少缓存字节
	 * @return true 缓存成功
	 *         <p>
	 *         false 缓存失败
	 * @throws JsonException
	 *             错误信息
	 */
	private boolean fillBuffer(int min) throws JsonException {
		char[] buffer = mBuffer;
		if (mPos != mLimit) {// 将未读取的数据载入到缓存中
			mLimit -= mPos;
			System.arraycopy(buffer, mPos, buffer, 0, mLimit);
		} else {// 缓存中的数据已读取结束
			mLimit = 0;
		}

		mPos = 0;
		try {
			int total;
			// 读取数据填充数组缓存的空位置
			while ((total = mReader
					.read(buffer, mLimit, buffer.length - mLimit)) != -1) {
				mLimit += total;
				// 第一次读取，如果存在位元组顺序标志，则跳过
				if (mCurrentPos == 0 && mLimit > 0 && buffer[0] == '\ufeff') {
					mPos++;
					mCurrentPos++;
					min++;
				}

				if (mLimit >= min) {// 已读取缓存大于最低要求
					return true;
				}
			}
		} catch (IOException e) {
			throw new JsonException(e);
		}

		return false;
	}

	/**
	 * 语法错误构建
	 *
	 * @param msg
	 *            错误信息
	 * @param value
	 *            错误值
	 * @return 错误信息
	 */
	private JsonException syntaxException(String msg, String value) {
		StringBuilder sb = new StringBuilder(msg);
		if (value != null && value.length() != 0) {
			sb.append(" for ");
			sb.append(value);
		}
		sb.append(" at character ");
		sb.append(mCurrentPos);

		return new JsonException(sb.toString());
	}

	@Override
	public void close() throws IOException {
		mReader.close();
		mScopeStack.close();
	}

	/**
	 * 值类型
	 *
	 * @author mzw
	 *
	 */
	public enum JsonToken {
		/** 数组开端 */
		BEGIN_ARRAY,
		/** 数组结尾 */
		END_ARRAY,
		/** 对象开端 */
		BEGIN_OBJECT,
		/** 对象结尾 */
		END_OBJECT,
		/** 键 */
		NAME,
		/** 字符串类型 */
		STRING,
		/** 数字类型 */
		NUMBER,
		/** 布尔类型 */
		BOOLEAN,
		/** 空值 */
		NULL,
		/** 无类型 */
		NONE,
		/** 文档结尾 */
		END_DOCUMENT
	}

	/**
	 * 短字符串哈希数列存储池
	 *
	 * @author mzw
	 *
	 */
	static class StringPool {
		private static final int MAX_LENGTH = 20;
		private final String[] pool = new String[1024];

		public String get(char[] array, int start, int length, int hashCode) {
			if (length > MAX_LENGTH) {
				return new String(array, start, length);
			}

			hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
			hashCode ^= (hashCode >>> 7) ^ (hashCode >>> 4);
			int index = hashCode & (pool.length - 1);

			String pooled = pool[index];
			if (pooled == null || pooled.length() != length) {
				String result = new String(array, start, length);
				pool[index] = result;
				return result;
			}

			for (int i = 0; i < length; i++) {
				if (pooled.charAt(i) != array[start + i]) {
					String result = new String(array, start, length);
					pool[index] = result;
					return result;
				}
			}

			return pooled;
		}
	}

	static {
		JsonReaderInternalAccess.INSTANCE = new JsonReaderInternalAccess();
	}

	/**
	 * 内部过滤名称为值工具
	 *
	 * @author mzw
	 *
	 */
	public static final class JsonReaderInternalAccess {
		public static JsonReaderInternalAccess INSTANCE;

		public void nameToValue(JsonReader reader) throws JsonException {
			JsonToken token = reader.mJsonToken;
			if (token == JsonToken.NONE) {
				token = reader.doPeek();
			}

			if (token == JsonToken.NAME) {
				reader.mJsonToken = JsonToken.STRING;
			} else {
				throw reader.syntaxException("Expected a name but was "
						+ reader.next(), reader.mNextValue);
			}
		}
	}
}
