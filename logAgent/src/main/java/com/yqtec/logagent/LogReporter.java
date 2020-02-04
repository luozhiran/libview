package com.yqtec.logagent;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.sup.itg.netlib.ItgNetSend;
import com.sup.itg.netlib.okhttpLib.interfaces.ItgCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.concurrent.BlockingQueue;

/**
 * LogReporter线程负责从队列中读取记录并上传
 * 
 * @author jiffy
 * 
 */
class LogReporter extends Thread {
	/**
	 * 缓存队列
	 */
	BlockingQueue<String> mQueue;

	LogReporter(String name, BlockingQueue<String> queue) {
		super(name);
		mQueue = queue;
	}

	@Override
	public void run() {
		Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
		onPrepareReport();
		while (true) {
			try {
				// Take a record from the queue.
				String record = mQueue.take();
				// boolean success =
				postRecord(record, 0);
			} catch (InterruptedException e) {
				continue;
			}
		}
	}

	/**
	 * 上传一条数据至后台
	 */
	boolean postRecord(final String record, int retryCount) {
//		HttpPost localHttpPost = new HttpPost(LogAgent.sReportPolicy.getReportUrl());
//		localHttpPost.addHeader("Content-Type", "application/json");
//		BasicHttpParams localBasicHttpParams = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(localBasicHttpParams, 10000);
//		HttpConnectionParams.setSoTimeout(localBasicHttpParams, 30000);
//		DefaultHttpClient localDefaultHttpClient = new DefaultHttpClient(localBasicHttpParams);
//		try {
//
//			localHttpPost.setEntity(new StringEntity(record, HTTP.UTF_8));
//			HttpResponse localHttpResponse = localDefaultHttpClient.execute(localHttpPost);
//			int code = localHttpResponse.getStatusLine().getStatusCode();
//			JSONObject jo = new JSONObject(record);
//			String logTime = jo.optString(LogAgent.LOG_TIME);
//			Log.e("LogReporter", "status code : " + code + "  logTime :" + logTime);
//			if (localHttpResponse.getStatusLine().getStatusCode() == 200) {
//				// String receive =
//				// EntityUtils.toString(localHttpResponse.getEntity());
//				// Log.e("LogReporter", "Receive response " + receive);
//				return true;
//			}
//			return false;
//		} catch (ClientProtocolException localClientProtocolException) {
//			Log.e("LogReporter", "ClientProtocolException,Failed to send message.");
//			return false;
//		} catch (UnsupportedEncodingException localClientProtocolException) {
//			Log.e("LogReporter", "UnsupportedEncodingException,Failed to send message.");
//			return false;
//		} catch (IOException localIOException) {
//			Log.e("LogReporter", "IOException,Failed to send message. retry:" + retryCount);
//			retryCount++;
//			if (retryCount <= ReportPolicy.DEFAULT_RETRY_COUNT)
//				postRecord(record, retryCount);
//			else {
//				handleFailedRecord(record);
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return false;

		String url = LogAgent.sReportPolicy.getReportUrl();
		ItgNetSend.itg()
				.builder(ItgNetSend.POST).url(url)
				.addContent(record,"application/json; charset=utf-8")
				.send(new ItgCallback() {
					@Override
					public void onFailure(String er) {
						handleFailedRecord(record);
					}

					@Override
					public void onResponse(String result, int code) {

					}
				});
		return false;
	}



	void onPrepareReport() {
		reportLocalRecord();
	}

	/**
	 * 开启线程 从本地缓存文件中逐条读取记录并put到缓存队列中
	 */
	void reportLocalRecord() {
		if (!new File(LogAgent.sReportPolicy.getCacheFilePath()).exists())
			return;
		final String tmpFileName = new File(LogAgent.sReportPolicy.getCacheFilePath()).getParent() + File.separator+ LogAgent.getCurTime().replace(":", "-") + "tmp.txt";
		new Thread() {
			public void run() {
				if (!new File(LogAgent.sReportPolicy.getCacheFilePath()).exists())
					return;
				// 将缓存文件重命名
				synchronized (FileUtils.class) {
					if (!FileUtils.renameFile(LogAgent.sReportPolicy.getCacheFilePath(), tmpFileName))
						return;
				}
				String line = "";
				BufferedReader bufferReader = null;
				if (!new File(tmpFileName).exists())
					return;
				try {
					bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpFileName), "utf-8"));
					while ((line = bufferReader.readLine()) != null) {
						if (!TextUtils.isEmpty(line)) {
							JSONObject jo = new JSONObject(line);
							// 优先级限制
							if (jo.optInt(LogAgent.LOG_PRIO) <= LogAgent.sReportPolicy.getPriorityThreshold())
								mQueue.put(line);
						}
					}
					if (!LogAgent.DEBUG)
						new File(tmpFileName).delete();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					FileUtils.closeQuietly(bufferReader);
				}

			}
		}.start();

	}

	void handleFailedRecord(String record) {
		try {
			JSONObject jo = new JSONObject(record);
			String logTime = jo.optString(LogAgent.LOG_TIME);
			long time = LogAgent.TIME_FORMAT.parse(logTime).getTime();
			int priority = jo.optInt(LogAgent.LOG_PRIO);
			if (System.currentTimeMillis() - time < 7 * 24 * 3600000l || priority == ReportPolicy.PRIORITY_CRASH) {
				LogAgent.writeRecord(record);
			}
			Log.e("LogReporter", "handleFailedRecord logTime :" + logTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
