package com.yqtec.logagent;

import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.concurrent.BlockingQueue;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
	OkHttpClient mOkHttpClient;

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
		String url = LogAgent.sReportPolicy.getReportUrl();
		if (mOkHttpClient == null)
			mOkHttpClient = new OkHttpClient();
		RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), record);
		mOkHttpClient.newCall(new Request.Builder().url(url).post(body).build())
				.enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				handleFailedRecord(record);
			}

			@Override
			public void onResponse(Call call, Response response) throws IOException {
                Log.d("LogReporter", "onResponse");
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
