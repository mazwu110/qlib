package com.dbdao.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

import com.qlib.qremote.QApp;

public class QDatabase extends SQLite {

	public QDatabase() {
		super(QApp.getInstance(), QApp.getAppName());
		this.dbVersion = 1;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table IF NOT EXISTS userInfo(user_id varchar(50), mobile varchar(50), nickname varchar(50), "
				+ "sex varchar(30), head_url varchar(50), shopname varchar(50), age varchar(50), note varchar(250))"); // 用户信息表
		initData(db);
	}

	private void initData(SQLiteDatabase db) {
		ContentValues Values = new ContentValues();
		Values.put("user_id", "122222");
		Values.put("nick_name", "admin");
		Values.put("password", "123456");
		Values.put("sex", "男");

		db.insert("userInfo", null, Values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("create table IF NOT EXISTS power_manager(id varchar(50), title varchar(20), location varchar(20),"
				+ "url varchar(250), versiontype varchar(20), navorder integer, navtype varchar(20), badge varchar(20),"
				+ "androidimg varchar(250), loadtype varchar(10), resId integer, atposition integer, useType integer)"); // loadtype 图片加载方式 0 加载本地图片 1 加载网络图片
		
		// db.execSQL("CREATE TABLE IF NOT EXISTS userInfo(user_id varchar(60), "
		// +
		// "nick_name varchar(60), mobile varchar(20), Insert_tivme TIMESTAMP default (datetime('now', 'localtime')),"
		// +
		// "sex varchar(10), age varchar(6), head_url varchar(200), qq_num varchar(10), email_address varchar(50))");
	}
}
