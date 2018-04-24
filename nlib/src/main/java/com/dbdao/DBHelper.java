package com.dbdao;

import android.content.ContentValues;

import com.dbdao.database.QDatabase;
import com.qlib.dbdao.bin.QList;

public class DBHelper {
	protected static QDatabase db = new QDatabase();

	// 删除表数据
	public static void delTableData(String tableName) {
		db.delete(tableName, null, null);
	}

	// 指定字段删除数据
	public static void delTableData(String tableName, String[] keyName,
			String[] keyValue) {
		if (keyName == null || keyName.length <= 0) {
			return;
		}

		String keyStr = keyName[0] + " = ?";
		for (int i = 1; i < keyName.length; i++) {
			keyStr = keyStr + " and " + keyName[i] + " = ?";
		}

		db.delete(tableName, keyStr, keyValue);
	}

	public static QList queryTableData(String tableName) {
		return db.query("select * from " + tableName, null);
	}
	
	public static QList queryTableData(String tableName, String orderCondition) {
		return db.query("select * from " + tableName + " " + orderCondition, null);
	}

	// 根据条件查询数据, dataKey 获取的字段数据值
	public static String queryTableData(String tableName, String keyName,
			String kayValue, String dataKey) {
		String result = "";
		QList list = queryTableData(tableName, new String[] { keyName },
				new String[] { kayValue });
		if (list.size() > 0)
			result = list.getStrValue(0, dataKey);
		return result;
	}

	// 根据条件查询数据, dataKey 获取的字段数据值
	public static QList queryTableData(String tableName, String keyName,
			String kayValue) {
		return queryTableData(tableName, new String[] { keyName },
				new String[] { kayValue });
	}

	// 根据条件查询数据
	public static QList queryTableData(String tableName, String[] keyName,
			String[] kayValue) {
		if (keyName == null || keyName.length == 0) {
			return db.query("select * from " + tableName, null);
		}

		String keyStr = " where " + keyName[0] + " = ?";

		for (int i = 1; i < keyName.length; i++) {
			keyStr = keyStr + " and " + keyName[i] + " = ?";
		}

		return db.query("select * from " + tableName + keyStr, kayValue);
	}

	// 单表插入
	public static void insertData(String tableName, String[] keyName,
			String[] keyValue) {
		if (keyName.length <= 0) {
			return;
		}

		ContentValues Values = new ContentValues();
		for (int i = 0; i < keyName.length; i++) {
			Values.put(keyName[i], keyValue[i]);
		}

		db.insert(tableName, null, Values);
	}

	public static QList checkTableData(String tableName, String keyName,
			String keyValue) {
		return db.query("select * from " + tableName + " where " + keyName
				+ " = ?", new String[] { keyValue });
	}

	// 批量插入数据, isDelete true 先删除数据
	public static void batchInsertData(String tableName, String[] keyArgs,
			QList list, boolean isDelete) {
		if (isDelete)
			delTableData(tableName);

		if (keyArgs.length <= 0 || list.size() <= 0) {
			return;
		}

		String keyStr = keyArgs[0];
		String values = "?";
		for (int i = 1; i < keyArgs.length; i++) {
			keyStr = keyStr + "," + keyArgs[i];
			values = values + "," + "?";
		}

		String sql = "INSERT INTO " + tableName + "(" + keyStr + ") VALUES ("
				+ values + ")";
		db.batchInsertData(list, sql, keyArgs);
	}

	// 根据条件更新数据
	public static void updateTableData(String tableName, String keyName,
			String keyValue, String upByKeyId, String upByKeyValue) {
		String keyStr = upByKeyId + " = ?";
		ContentValues Values = new ContentValues();
		Values.put(keyName, keyValue);

		db.update(tableName, Values, keyStr, new String[] { upByKeyValue });
	}

	// 根据主键和值更新数据
	public static void updateTableData(String tableName, String[] keyName,
			String[] keyValue, String[] upByKeyId, String[] upByKeyValue) {

		String keyStr = upByKeyId[0] + " = ?";
		for (int i = 1; i < upByKeyId.length; i++) {
			keyStr = keyStr + " and " + upByKeyId[i] + " = ?";
		}

		ContentValues Values = new ContentValues();
		for (int i = 0; i < keyName.length; i++) {
			Values.put(keyName[i], keyValue[i]);
		}

		db.update(tableName, Values, keyStr, upByKeyValue);
	}

	// 存储消息设置参数
	public static void SaveInfo_set(String strvalue, int type) {
		ContentValues Values = new ContentValues();
		String fieldname = "";
		if (type == 0) {
			fieldname = "newinfo";
		} else if (type == 1) {
			fieldname = "contents";
		} else if (type == 2) {
			fieldname = "voice";
		} else if (type == 3) {
			fieldname = "shock";
		} else if (type == 4) {
			fieldname = "voice_path";
		}

		if (fieldname.equals("")) {
			return;
		}

		Values.put(fieldname, strvalue);

		if (CheckInfo_set() <= 0) { // 插入 newinfo contents voice shock
			db.insert("jpush_infoset", null, Values);
		} else {
			db.update("jpush_infoset", Values, null, null);
		}
	}

	public static int CheckInfo_set() {
		return queryTableData("jpush_infoset").size();
	}

}
