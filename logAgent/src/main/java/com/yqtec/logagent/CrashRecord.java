package com.yqtec.logagent;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

class CrashRecord implements UncaughtExceptionHandler {
	private Thread.UncaughtExceptionHandler mDefaultHandler;

	private static CrashRecord crashHandler;

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub
		if (crashHandler != null) {
			JSONObject jo = UserInfo.getMetrics(LogAgent.sContext);
			try {
				jo.put(LogAgent.LOG_INFO, Log.getStackTraceString(ex));
				LogAgent.setupToJSON(jo, "crash", ReportPolicy.PRIORITY_CRASH);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			FileUtils.writeLogToFile(new File(LogAgent.sReportPolicy.getCacheFilePath()), jo.toString());
		}
		mDefaultHandler.uncaughtException(thread, ex);
	}

	public CrashRecord() {
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	public static void init() {
		if (crashHandler == null)
			crashHandler = new CrashRecord();
	}
}
