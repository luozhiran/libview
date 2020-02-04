package com.yqtec.logagent;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.yqtec.logagent.ReportPolicy.Builder;

public class LogAgent {

	protected static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",
			Locale.getDefault());

	private static final String TAG = "LogAgent";
	private static final String INTERVAL_REPORT_ACTION = "interval_report_action";
	private static final String UPDATE_CONFIG_ACTION = "update_online_config";
	private static final int DALIY_INTERVAL = 1000 * 60 * 60 * 24;// 24h
	private static final int UPDATE_CONFIG_INTERVAL = 1000 * 60;
	static final String LOG_TIME = "logt";
	static final String LOG_EVENT = "loge";
	static final String LOG_INFO = "logi";
	static final String LOG_PRIO = "logp";

	protected static Context sContext;

	private static long sLastOnResumeTime;
	private static String sLastOnResumeName;
//	private static long sLastOnPageStartTime;
//	private static String sLastOnPageStartName;

	protected static ReportPolicy sReportPolicy;

	private static LinkedBlockingQueue<String> sCacheLogQueue;
	public static final String DEFAULT_REPORT_POLICY_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "Yuan/log_agent_policy.txt";
	public static boolean DEBUG = false;

	public static final String DEFAULT_REPORT_POLICY_URL = "http://127.0.0.1:8080/log_agent_policy.txt";// fixme
	private static LogReporter sLogReporter;
	// private static UserInfo sUserInfo;
	private static ReportBroadcastReceiver sReportBroadReceiver;
	private static UpdateConfigBroadcastReceiver sUpdateConfigReceiver;
	private static boolean isInited;
	private static String sReportPolicyUrl = DEFAULT_REPORT_POLICY_URL;
	private static boolean shouldUpdateOnlineConfig;
	private static boolean updateOnlineConfigSuccess;
	private static HandlerThread sRecordThread;
	private static final int MSG_ADD_LOG = 0;
	private static RecordHandler sRecordHandler;
	private static String sUid;

	/**
	 * 初始化后会从后台获取发送策略的配置文件 获取到配置文件之前SDK不工作
	 * 
	 * @param uerId
	 * @param context
	 * @param updateReportPolicyUrl
	 *            用于获取线上发送策略配置文件的URL
	 */
	public synchronized static void initByOnlineConfig(Context context, String updateReportPolicyUrl,String uid) {
		if (context == null) {
			return;
		}
		sUid = uid;
		sReportPolicyUrl = updateReportPolicyUrl;
		shouldUpdateOnlineConfig = true;
		updateOnlineConfigSuccess = false;
		sContext = context.getApplicationContext();
		CrashRecord.init();
		sReportPolicy = new ReportPolicy(new Builder());
		isInited = false;
		if (isNetAvaible()) {
			updateOnlinePolicy();
		}
		registerUpdateConfigBroadcast();
		startUpdateOnlineConfigBroad(5000);
	}

	/**
	 * 根据传入的发送策略进行初始化
	 * 
	 * @param uerId
	 *            家长ID或机器人ID
	 * @param context
	 * @param policy
	 *            发送策略
	 */
	public synchronized static void init(Context context, ReportPolicy policy,String uid) {
		if (policy == null || context == null) {
			return;
		}
		sContext = context.getApplicationContext();
		CrashRecord.init();
		sReportPolicy = policy;
		sUid = uid;
		if (sRecordThread == null || !sRecordThread.isAlive()) {
			sRecordThread = new HandlerThread("RecordThread") {
				protected void onLooperPrepared() {
					isInited = true;
					onInitialized();
				}
			};
			sRecordThread.start();
		}
		if (sRecordHandler == null) {
			sRecordHandler = new RecordHandler(sRecordThread.getLooper());
		}
		if (ReportPolicy.POLICY_LAUNCH.equals(sReportPolicy.getMode())
				|| ReportPolicy.POLICY_REALTIME.equals(sReportPolicy.getMode())) {
			startReportRecords();
		} else if (ReportPolicy.POLICY_INTERVAL.equals(sReportPolicy.getMode())) {
			startIntervalReport();
			registerIntervalReportBroadcast();
		} else if (ReportPolicy.POLICY_DALIY.equals(sReportPolicy.getMode())) {
			startDaliyReport();
			registerIntervalReportBroadcast();
		} else if (ReportPolicy.POLICY_FILE_LENGTH.equals(sReportPolicy.getMode())) {
			startFileLenReport();
		}

	}

	/**
	 * 用于记录进入Activity
	 * 
	 * @param context
	 */
	public static void onResume(Context context) {
		if (context == null || !isInitialized())
			return;
		onEvent("onResume", context.getClass().getName(), ReportPolicy.PRIORITY_DEFAULT);
		sLastOnResumeTime = System.currentTimeMillis();
		sLastOnResumeName = context.getClass().getName();
	}

	/**
	 * 用于记录离开Activity，自动计算与onResume消息的时间间隔并写入duration字段
	 * 
	 * @param context
	 */
	public static void onPause(Context context) {
		if (context == null || !isInitialized())
			return;
		String name = context.getClass().getName();
		if (name.equals(sLastOnResumeName)) {
			long duration = System.currentTimeMillis() - sLastOnResumeTime;
			onEvent("onPause", getJSON(new String[] { LOG_INFO, "duration" }, new Object[] { name, duration }),
					ReportPolicy.PRIORITY_DEFAULT);
		}
	}

	/**
	 * 用于记录进入Fragment
	 * 
	 * @param info
	 */
	public static void onPageStart(String info) {
		if (TextUtils.isEmpty(info) || !isInitialized())
			return;
		onEvent("onPageStart", info, ReportPolicy.PRIORITY_DEFAULT);
//		sLastOnPageStartName = info;
//		sLastOnPageStartTime = System.currentTimeMillis();
	}

	/**
	 * 用于记录离开Fragment
	 * 
	 * @param info
	 */
	public static void onPageEnd(String info) {
		if (TextUtils.isEmpty(info) || !isInitialized())
			return;
//		if (info.equals(sLastOnPageStartName)) {
//			long duration = System.currentTimeMillis() - sLastOnPageStartTime;
			onEvent("onPageEnd", getJSON(new String[] { LOG_INFO }, new Object[] { info }),
					ReportPolicy.PRIORITY_DEFAULT);
//		}
	}

	/**
	 * 用于记录自定义事件
	 * 
	 * @param event
	 *            事件ID
	 * @param info
	 * @param priotiry
	 *            优先级
	 */
	public static void onEvent(String event, String info, int priotiry) {
		if (TextUtils.isEmpty(event) || TextUtils.isEmpty(info) || !isInitialized())
			return;
		JSONObject jo = new JSONObject();
		try {
			jo.put(LOG_INFO, info);
			setupToJSON(jo, event, priotiry);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// writeRecord(jo.toString());
		sRecordHandler.obtainMessage(MSG_ADD_LOG, jo.toString()).sendToTarget();
	}

	/**
	 * 用于记录自定义事件
	 * 
	 * @param event
	 *            事件ID
	 * @param info
	 * @param priotiry
	 *            优先级
	 */
	public static void onEvent(String event, JSONObject info, int priotiry) {
		if (TextUtils.isEmpty(event) || info == null || !isInitialized())
			return;
		try {
			setupToJSON(info, event, priotiry);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// writeRecord(info.toString());
		sRecordHandler.obtainMessage(MSG_ADD_LOG, info.toString()).sendToTarget();
	}

	/**
	 * 用于记录自定义事件 优先级默认为1
	 * 
	 * @param event
	 *            事件ID
	 * @param info
	 */
	public static void onEvent(String event, String info) {
		onEvent(event, info, ReportPolicy.PRIORITY_DEFAULT);
	}

	/**
	 * 用于记录自定义事件 优先级默认为1
	 * 
	 * @param event
	 *            事件ID
	 * @param info
	 */
	public static void onEvent(String event, JSONObject info) {
		onEvent(event, info, ReportPolicy.PRIORITY_DEFAULT);
	}

	/**
	 * 用于触发上传操作，仅在发送策略为POLICY_DEVELOPMENT时有效
	 */
	public static void triggerReportManually() {
		if (ReportPolicy.POLICY_DEVELOPMENT.equals(sReportPolicy.getMode())) {
			startReportRecords();
		}
	}

	/**
	 * 在子线程中获取线上的发送策略配置文件
	 */
	public static void updateOnlinePolicy() {
		if (!shouldUpdateOnlineConfig) {
			return;
		}
		updateOnlineConfigSuccess = false;
		new Thread() {
			public void run() {
				HttpGet httpGet = new HttpGet(sReportPolicyUrl);
				HttpClient httpClient = new DefaultHttpClient();
				try {
					HttpResponse response = httpClient.execute(httpGet);
					String result = EntityUtils.toString(response.getEntity(), "utf-8");
					FileUtils.saveStringToFile(result, DEFAULT_REPORT_POLICY_PATH);
					updateOnlineConfigSuccess = true;
				} catch (Exception e) {
					updateOnlineConfigSuccess = false;
					e.printStackTrace();
				}
			}
		}.start();
	}

	public static void quit() {
		if (!isInited)
			return;
		try {
			if (sLogReporter != null) {
				sLogReporter.interrupt();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		if (ReportPolicy.POLICY_INTERVAL.equals(sReportPolicy.getMode())
				|| ReportPolicy.POLICY_DALIY.equals(sReportPolicy.getMode())) {
			stopIntervalReport();
			sContext.unregisterReceiver(sReportBroadReceiver);
		}
		if (shouldUpdateOnlineConfig)
			sContext.unregisterReceiver(sUpdateConfigReceiver);
	}

	/**
	 * 根据当前的发送策略选择将记录存入队列还是写入文件
	 * 
	 * @param info
	 */
	protected static void writeRecord(String info) {
		if (isNetAvaible() && ReportPolicy.POLICY_REALTIME.equals(sReportPolicy.getMode())) {
			try {
				if (sCacheLogQueue != null)
					sCacheLogQueue.put(info);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			FileUtils.writeLogToFile(new File(sReportPolicy.getCacheFilePath()), info);
			if (ReportPolicy.POLICY_FILE_LENGTH.equals(sReportPolicy.getMode())) {
				startFileLenReport();
			}
		}
	}

	/**
	 * 初始化缓存队列 启动文件上传线程
	 */
	private static void startReportRecords() {
		if (isNetAvaible()) {
			if (sCacheLogQueue == null)
				sCacheLogQueue = new LinkedBlockingQueue<String>();
			if (sLogReporter == null || !sLogReporter.isAlive()) {
				sLogReporter = new LogReporter("LogReporter", sCacheLogQueue);
				sLogReporter.start();
			} else {
				sLogReporter.reportLocalRecord();
			}
		}
	}

	private static JSONObject getJSON(String[] keys, Object[] values) {
		JSONObject jo = new JSONObject();
		if (keys == null || values == null || (keys.length != values.length)) {
			return jo;
		}
		try {
			for (int i = 0; i < keys.length; i++) {
				jo.put(keys[i], values[i]);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jo;

	}

	/**
	 * 获取当前时间的字符串
	 */
	protected static String getCurTime() {
		return TIME_FORMAT.format(Calendar.getInstance().getTime());
	}

	/**
	 * 用于判断网络是否有效
	 * 
	 * @return 如果发送策略为仅Wifi时上传，则仅在wifi连接时返回true 否则都返回false
	 */

	private static boolean isNetAvaible() {
		ConnectivityManager connectivityManager = (ConnectivityManager) sContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		if (activeNetworkInfo == null || !activeNetworkInfo.isConnected())
			return false;
		if (sReportPolicy.getReportOnlyWifi() && activeNetworkInfo.getType() != ConnectivityManager.TYPE_WIFI)
			return false;
		else
			return true;
	}

	/**
	 * 解析JSON字符串 生成发送策略
	 * 
	 * @param str
	 * @return
	 */
	private static ReportPolicy getStringPolicy(final String str) {
		ReportPolicy policy = new ReportPolicy(new Builder());
		if (TextUtils.isEmpty(str))
			return policy;
		try {
			JSONObject jo = new JSONObject(str);
			policy.setUpFromJSON(jo);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return policy;
	}

	private static ReportPolicy getLocalFilePolicy() {
		String info = FileUtils.getStringFromFile(DEFAULT_REPORT_POLICY_PATH);
		return getStringPolicy(info);
	}

	/**
	 * 除了传入的参数， 默认向json中写入user_id ,versionCode ,app_name, LOG_TIME等信息
	 * 
	 * @param jo
	 * @param event
	 * @param priority
	 * @throws JSONException
	 */
	public static void setupToJSON(final JSONObject jo, String event, int priority) throws JSONException {
		// 方法传入的参数
		jo.put(LOG_EVENT, event);
		jo.put(LOG_PRIO, priority);

		jo.put("uid", sUid);
		// 默认添加的字段
		jo.put(LOG_TIME, getCurTime());
	}

	/**
	 * ReportPolicy.POLICY_INTERVAL模式时启动定时任务 触发上传操作
	 */
	@SuppressLint("NewApi")
	private static void startIntervalReport() {
		AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(INTERVAL_REPORT_ACTION);
		PendingIntent pendingIntent = PendingIntent
				.getBroadcast(sContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtMillis = SystemClock.elapsedRealtime();
		alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtMillis + sReportPolicy.getReportInterval(),
				sReportPolicy.getReportInterval(), pendingIntent);
	}

	/**
	 * ReportPolicy.POLICY_FILE_LENGTH模式时检查缓存文件大小 触发上传操作
	 */
	private static void startFileLenReport() {
		long len = sReportPolicy.getFileLengthThresold();
		if (new File(sReportPolicy.getCacheFilePath()).length() >= len) {
			startReportRecords();
		}
	}

	/**
	 * ReportPolicy.POLICY_DALIY模式时启动定时任务 每天固定时间点触发一次上传操作
	 */
	private static void startDaliyReport() {
		String clock = sReportPolicy.getReportClock();
		Calendar cal = Calendar.getInstance();
		try {
			String[] infos = clock.split(":");
			int hour = Integer.parseInt(infos[0]);
			int minute = Integer.parseInt(infos[1]);
			int second = Integer.parseInt(infos[2]);
			cal.set(Calendar.HOUR_OF_DAY, hour);
			cal.set(Calendar.MINUTE, minute);
			cal.set(Calendar.SECOND, second);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(INTERVAL_REPORT_ACTION);
		PendingIntent pendingIntent = PendingIntent
				.getBroadcast(sContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), DALIY_INTERVAL, pendingIntent);
	}

	/**
	 * 停止POLICY_DALIY和POLICY_INTERVAL模式时启动的定时任务
	 */
	private static void stopIntervalReport() {
		AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(INTERVAL_REPORT_ACTION);
		PendingIntent pendingIntent = PendingIntent
				.getBroadcast(sContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pendingIntent);
	}

	private static void registerIntervalReportBroadcast() {
		IntentFilter intentFilter = new IntentFilter(INTERVAL_REPORT_ACTION);
		sReportBroadReceiver = new ReportBroadcastReceiver();
		sContext.registerReceiver(sReportBroadReceiver, intentFilter);
	}

	/**
	 * 开启定时任务 定时从后台获取发送策略的配置文件
	 */
	@SuppressLint("NewApi")
	private static void startUpdateOnlineConfigBroad(long triggerDelay) {
		if (shouldUpdateOnlineConfig) {
			AlarmManager alarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(UPDATE_CONFIG_ACTION);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(sContext, 0, intent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			long triggerAtMillis = SystemClock.elapsedRealtime();
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtMillis + triggerDelay,
					UPDATE_CONFIG_INTERVAL, pendingIntent);
		}
	}

	/**
	 * 注册定时更新线上发送策略的广播
	 */
	private static void registerUpdateConfigBroadcast() {
		IntentFilter intentFilter = new IntentFilter(UPDATE_CONFIG_ACTION);
		sUpdateConfigReceiver = new UpdateConfigBroadcastReceiver();
		sContext.registerReceiver(sUpdateConfigReceiver, intentFilter);
	}

	static class ReportBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (INTERVAL_REPORT_ACTION.equals(action)) {
				startReportRecords();
			}
		}
	}

	static class UpdateConfigBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// 获取发送策略的方式为从线上获取
			if (UPDATE_CONFIG_ACTION.equals(action) && shouldUpdateOnlineConfig) {
				// 成功从后台获取配置文件
				if (updateOnlineConfigSuccess) {
					if (isInited) {
						// 之前已经初始化成功 更新sReportPolicy
						sReportPolicy = getLocalFilePolicy();
					} else {
						// 之前尚未初始化成功 首次下载成功后触发
						init(sContext, getLocalFilePolicy(),sUid);
					}
				}
				updateOnlinePolicy();
			}
		}
	}

	private static void onInitialized() {
		JSONObject jo = UserInfo.getMetrics(sContext);
		try {
			setupToJSON(jo, "on_initialized", ReportPolicy.PRIORITY_DEFAULT);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeRecord(jo.toString());
	}

	static class RecordHandler extends Handler {

		public RecordHandler(Looper looper) {
			super(looper);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ADD_LOG:
				writeRecord((String) msg.obj);
				break;
			}
		}
	}

	private static boolean isInitialized() {
		if (!isInited) {
			Log.e(TAG, "SDK is not initialized!");
		}
		return isInited;
	}
}
