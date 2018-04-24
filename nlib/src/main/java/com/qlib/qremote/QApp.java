package com.qlib.qremote;



public abstract class QApp extends QApplication {
	
	public static QApp getTechAppInstance()
	{
		return (QApp)QApp.getInstance();
	}
}
