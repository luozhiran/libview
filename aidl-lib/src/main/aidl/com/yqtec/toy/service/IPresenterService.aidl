package com.yqtec.toy.service;
import android.content.ContentValues;
import com.yqtec.toy.service.IPresenterServiceCallback;

interface IPresenterService{
	void registerCallback(IPresenterServiceCallback cb);
	void unregisterCallback(IPresenterServiceCallback cb);
	void resumeDetect();
	void pauseDetect();
	long sqlIntert(String tableName, String nullColumnHack, in ContentValues values);
	void sendVideoLog(in ContentValues values);
	void setInTencentSDK(boolean flag);
	boolean isDebugVersion();
	void onAntiItemTimeIn(String item);
	void onAntiItemTimeOut(String item);
	void showAntiAddictionWarn(String item, boolean showPop);
	void onCartoonIn(in ContentValues values);
	void onCartoonOut();
	void unBindMonitorService();
	String getPetAttrs();
    void setOpenGame(String game);
    boolean isAddictionRestToTime();
    boolean isExceedCartoonTimeLimit();
}