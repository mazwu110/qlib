package com.qjsonlib.stream;

class ScopeStack {
	/** 解析栈 */
	private Scope[] stack = new Scope[32];
	/** 栈大小 */
	private int size = 0;
	{
		// 初始栈顶数据
		stack[size++] = Scope.EMPTY_DOCUMENT;
	}

	/**
	 * 查看栈顶数据
	 *
	 * @return 栈顶数据
	 */
	public Scope peek() {
		return stack[size - 1];
	}

	/**
	 * 更新栈顶数据
	 *
	 * @param newTop
	 *            栈顶数据
	 */
	public void replaceTop(Scope newTop) {
		stack[size - 1] = newTop;
	}

	/**
	 * 推入栈顶数据
	 *
	 * @param newTop
	 *            栈顶数据
	 */
	public void push(Scope newTop) {
		if (size == stack.length) {
			Scope[] newStack = new Scope[size * 2];
			System.arraycopy(stack, 0, newStack, 0, size);
			stack = newStack;
		}
		stack[size++] = newTop;
	}

	/**
	 * 弹出栈顶数据
	 *
	 * @return 栈顶数据
	 */
	public Scope pop() {
		Scope value = stack[size - 1];
		size--;
		return value;
	}

	/**
	 * 获取栈大小
	 *
	 * @return 栈大小
	 */
	public int getScopeSize() {
		return size;
	}

	/**
	 * 关闭栈
	 */
	public void close() {
		size = 1;
		stack[0] = Scope.CLOSED;
	}

	public enum Scope {
		/** 不存在值的数组，在关闭前不需要分隔符或换行符 */
		EMPTY_ARRAY,
		/** 存在至少有一个值的数组，在获取下个元素前需要逗号和换行符 */
		NONEMPTY_ARRAY,
		/** 不存在键值对的对象，在关闭前不需要分隔符或换行符 */
		EMPTY_OBJECT,
		/** 已读取键值对的键，下个元素必须为键值对的值 */
		DANGLING_NAME,
		/** 至少存在一个键值对对象，在读取下一个元素钱需要逗号和换行符 */
		NONEMPTY_OBJECT,
		/** 无相关文件读取 */
		EMPTY_DOCUMENT,
		/** 开始读取相关的文件 */
		NONEMPTY_DOCUMENT,
		/** 文件关闭并无法读取 */
		CLOSED;
	}
}
