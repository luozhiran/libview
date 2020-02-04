package com.yqtec.logagent;

import java.io.File;

import org.json.JSONObject;

import android.os.Environment;

public class ReportPolicy {

	public static final int PRIORITY_CRASH = 0;
	public static final int PRIORITY_DEFAULT = 1;

	/** 实时发送 离线则存在本地文件中 */
	public static final String POLICY_REALTIME = "real_time";
	/** 程序开启时从缓存文件中读取并上传 */
	public static final String POLICY_LAUNCH = "launch";
	/** 以指定的间隔检查缓存文件并逐条上传 */
	public static final String POLICY_INTERVAL = "interval";
	/** 缓存文件超过一定大小时触发上传 */
	public static final String POLICY_FILE_LENGTH = "cache_file_length";
	/** 每天固定的时间点触发上传 */
	public static final String POLICY_DALIY = "daliy";
	/** 调试模式 代码触发上传 否则不上传 */
	public static final String POLICY_DEVELOPMENT = "develop";

	protected static final String DEFAULT_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ File.separator + "Yuan/log_agent.txt";

	protected static final String DEFAULT_REPORT_URL = "http://tickmq.yuanqutech.com:9800/applog?";
	protected static final long DEFAULT_FILELEN_THRESOLD = 100 * 1024;
	protected static final int DEFAULT_RETRY_COUNT = 2;
	protected static final int DEFAULT_INTERVAL = 3600000;
	protected static final String DEFAULT_CLOCK = "00:12:34";
	// 发送模式
	private String mode;
	// 发送间隔 mode == POLICY_INTERVAL时有效
	private long reportInterval;
	// 优先级的限制
	private int priorityThreshold;
	// 缓存文件路径
	private String cacheFilePath;
	// 上传地址
	private String reportUrl;
	// 缓存文件大小限制 超过会触发上传 单位:字节 mode == POLICY_FILE_LENGTH 时有效
	private long fileLengthThresold;
	// 定点上传的时间点 00:12:34 mode == POLICY_DALIY 时有效
	private String reportClock;
	// 是否仅在wifi下上传
	private boolean reportOnlyWifi;

	public ReportPolicy(Builder builder) {
		mode = builder.mode;
		reportInterval = builder.reportInterval;
		priorityThreshold = builder.priorityThreshold;
		reportUrl = builder.reportUrl;
		fileLengthThresold = builder.fileLengthThresold;
		cacheFilePath = builder.cacheFilePath;
		reportClock = builder.reportClock;
		reportOnlyWifi = builder.reportOnlyWifi;
	}

	public static class Builder {
		private String mode = POLICY_REALTIME;
		private long reportInterval = DEFAULT_INTERVAL;
		private int priorityThreshold = PRIORITY_DEFAULT;
		private String reportUrl = DEFAULT_REPORT_URL;
		private long fileLengthThresold = DEFAULT_FILELEN_THRESOLD;
		private String cacheFilePath = DEFAULT_CACHE_PATH;
		private String reportClock = DEFAULT_CLOCK;
		private boolean reportOnlyWifi = false;

		/**
		 * 设置发送间隔  POLICY_INTERVAL时有效
		 * 
		 * @param reportIinterval
		 *            单位：毫秒
		 * @return
		 */
		public Builder setReportInterval(long reportIinterval) {
			this.reportInterval = reportIinterval;
			return this;
		}

		/**
		 * 在上传时根据设置的优先级进行过滤
		 * 
		 * @param priorityThreshold
		 * @return
		 */
		public Builder setPriorityThreshold(int priorityThreshold) {
			this.priorityThreshold = priorityThreshold;
			return this;
		}

		/**
		 * 设置每天定时发送的时间点  POLICY_DALIY 时有效
		 * 
		 * @param clock
		 *            格式："00:23:77" (hour:minute:second)
		 * @return
		 */
		public Builder setReportClock(String clock) {
			this.reportClock = clock;
			return this;
		}

		/**
		 * 设置上传log时的url
		 * 
		 * @param reportUrl
		 * @return
		 */
		public Builder setReportUrl(String reportUrl) {
			this.reportUrl = reportUrl;
			return this;
		}

		/**
		 * 设置缓存文件大小的阈值  POLICY_FILE_LENGTH 时有效
		 * 
		 * @param fileLengthThresold
		 *            单位为字节
		 * @return
		 */
		public Builder setFileLengthThresold(long fileLengthThresold) {
			this.fileLengthThresold = fileLengthThresold;
			return this;
		}

		/**
		 * 设置发送模式
		 * 
		 * @param mode
		 * @return
		 */
		public Builder setMode(String mode) {
			this.mode = mode;
			return this;
		}

		/**
		 * 设置缓存文件路径
		 * 
		 * @param cacheFilePath
		 * @return
		 */
		public Builder setCacheFilePath(String cacheFilePath) {
			this.cacheFilePath = cacheFilePath;
			return this;
		}

		/**
		 * 设置是否仅在WIFI下上传
		 * 
		 * @param cacheFilePath
		 * @return
		 */
		public Builder setReportOnlyWifi(boolean onlyWifi) {
			this.reportOnlyWifi = onlyWifi;
			return this;
		}

		public ReportPolicy build() {
			return new ReportPolicy(this);
		}

	}

	public String getMode() {
		return mode;
	}

	public long getReportInterval() {
		return reportInterval;
	}

	public int getPriorityThreshold() {
		return priorityThreshold;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public long getFileLengthThresold() {
		return fileLengthThresold;
	}

	public String getCacheFilePath() {
		return cacheFilePath;
	}

	public String getReportClock() {
		return reportClock;
	}

	public boolean getReportOnlyWifi() {
		return reportOnlyWifi;
	}

	/**
	 * 从json中读取字段，用以配置发送策略
	 * 
	 * @param jo
	 */
	protected void setUpFromJSON(JSONObject jo) {
		if (jo == null)
			return;
		this.mode = jo.optString("mode", POLICY_REALTIME);
		this.cacheFilePath = jo.optString("cacheFilePath", DEFAULT_CACHE_PATH);
		this.fileLengthThresold = jo.optLong("fileLengthThresold", DEFAULT_FILELEN_THRESOLD);
		this.priorityThreshold = jo.optInt("priorityThreshold", PRIORITY_DEFAULT);
		this.reportClock = jo.optString("reportClock", DEFAULT_CLOCK);
		this.reportInterval = jo.optLong("reportInterval", DEFAULT_INTERVAL);
		this.reportUrl = jo.optString("reportUrl", DEFAULT_REPORT_URL);
		this.reportOnlyWifi = jo.optBoolean("reportOnlyWifi", false);
	}
}
