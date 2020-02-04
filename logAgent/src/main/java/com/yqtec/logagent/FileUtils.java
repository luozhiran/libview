package com.yqtec.logagent;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import android.text.TextUtils;

public class FileUtils {

	public static void writeLogToFile(File logFile, String info) {
		if (logFile == null || TextUtils.isEmpty(info)) {
			return;
		}
		synchronized (FileUtils.class) {
			FileWriter fileWriter = null;
			BufferedWriter bufdWriter = null;
			PrintWriter printWriter = null;
			try {
				logFile.getParentFile().mkdirs();
				if (!logFile.exists()) {
					logFile.createNewFile();
				}
				fileWriter = new FileWriter(logFile, true);
				bufdWriter = new BufferedWriter(fileWriter);
				printWriter = new PrintWriter(fileWriter);

				bufdWriter.append('\n').append(info);
				bufdWriter.flush();
				printWriter.flush();
				fileWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				closeQuietly(fileWriter);
				closeQuietly(bufdWriter);
				closeQuietly(printWriter);
			}
		}
	}

	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException ioe) {
				// ignore
			}
		}
	}

	public static String getStringFromFile(String filePath) {
		String result = "";
		String line = "";
		BufferedReader bufferReader = null;
		if (!new File(filePath).exists())
			return result;
		try {
			bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "utf-8"));
			while ((line = bufferReader.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeQuietly(bufferReader);
		}
		return result;

	}

	public static void saveStringToFile(String infos, String filePath) {
		if (!new File(filePath).getParentFile().exists()) {
			new File(filePath).getParentFile().mkdirs();
		}
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		try {
			write = new OutputStreamWriter(new FileOutputStream(filePath), "utf-8");
			writer = new BufferedWriter(write);
			writer.write(infos);
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeQuietly(write);
			closeQuietly(writer);
		}
	}

	// 重命名文件
	public static boolean renameFile(final String sPath, final String newPath) {
		File file = new File(sPath);
		File newFile = new File(newPath);
		if (newFile.exists()) {
			newFile.delete();
		}
		return file.renameTo(new File(newPath));
	}

}
