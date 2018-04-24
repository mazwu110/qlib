package com.dbdao.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.qlib.dbdao.bin.QList;

public abstract class SQLite {
	protected SQLiteOpenHelper dbHelper;
	protected SQLiteDatabase db = null;
	protected String dbName = "qlib_db.db";  // 数据库文件名
	protected int dbVersion = 1; //版本号
	protected Context context = null;

	public SQLite(Context context) {
		this.context = context;
	}

	public SQLite(Context context, String dbName) {
		this.context = context;
		this.dbName = dbName;
	}

	protected synchronized void connection() {
		if (this.db == null) {
			this.dbHelper = new SQLiteOpenHelper(context, dbName, null,
					dbVersion) {
				@Override
				public void onCreate(SQLiteDatabase db) {
					SQLite.this.onCreate(db);
				}

				@Override
				public void onUpgrade(SQLiteDatabase db, int oldVersion,
						int newVersion) {
					SQLite.this.onUpgrade(db, oldVersion, newVersion);
				}
			};
			this.db = dbHelper.getWritableDatabase();
		}
	}

	public abstract void onCreate(SQLiteDatabase db);

	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion,
			int newVersion);

	public synchronized QList query(String sql, String[] selectionArgs) {
		this.connection();
		QList list = new QList();
		Cursor cursor = this.db.rawQuery(sql, selectionArgs);
		int j = 0;
		while (cursor.moveToNext()) {
			for (int i = 0; i < cursor.getColumnCount(); i++) {
				list.put(j, cursor.getColumnName(i).toString(),
						cursor.getString(i));
			}
			j++;
		}
		cursor.close();
		this.close();
		return list;
	}

	public synchronized boolean execute(String sql, Object[] bindArgs) {
		boolean flag = false;
		this.connection();
		// 启动事务
		this.db.beginTransaction();
		try {
			if (bindArgs == null)
				this.db.execSQL(sql);
			else
				this.db.execSQL(sql, bindArgs);
			this.db.setTransactionSuccessful();
			flag = true;
		} finally {
			this.db.endTransaction();
		}
		this.close();
		return flag;
	}

	public synchronized boolean insert(String table, String nullColumnHack,
			ContentValues values) {
		boolean flag = false;
		this.connection();
		this.db.beginTransaction();
		try {
			this.db.insert(table, nullColumnHack, values);
			this.db.setTransactionSuccessful();
			flag = true;
		} finally {
			this.db.endTransaction();
		}
		this.close();

		return flag;
	}

	public synchronized boolean delete(String table, String whereClause,
			String[] whereArgs) {
		boolean flag = false;
		this.connection();
		this.db.beginTransaction();
		try {
			this.db.delete(table, whereClause, whereArgs);
			this.db.setTransactionSuccessful();
			flag = true;
		} finally {
			this.db.endTransaction();
		}
		this.close();

		return flag;
	}

	public synchronized boolean update(String table, ContentValues values,
			String whereClause, String[] whereArgs) {
		boolean flag = false;
		this.connection();
		this.db.beginTransaction();
		try {
			this.db.update(table, values, whereClause, whereArgs);
			this.db.setTransactionSuccessful();
			flag = true;
		} finally {
			this.db.endTransaction();
		}
		this.close();
		return flag;
	}

	// 批量插入数据, keyArgs 字段数
	public synchronized boolean batchInsertData(QList values, String sql,
			String[] keyArgs) {
		if (values.size() <= 0) {
			return false;
		}

		boolean flag = false;
		this.connection();
		db.beginTransaction();

		SQLiteStatement stmt = db.compileStatement(sql);
		for (int i = 0; i < values.size(); i++) {
			for (int j = 0; j < keyArgs.length; j++) {
				stmt.bindString(j + 1, values.getStrValue(i, keyArgs[j]));
			}
			stmt.execute();
			stmt.clearBindings();
		}

		db.setTransactionSuccessful();
		db.endTransaction();

		this.close();
		return flag;
	}

	public synchronized void close() {
		this.db.close();
		this.db = null;
	}
}
