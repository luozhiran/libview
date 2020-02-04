package com.yqtec.logagent;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class UserInfo {
	public static final String TAG = "UserInfo";

	static String getAppName(Context context) {
		return context.getPackageName();
	}

	static String getImei(Context context) {
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		return imei;
	}

	/**
	 * Returns the current operating system version as a displayable string.
	 */
	static String getOSVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	/**
	 * Returns the current device model.
	 */
	static String getDevice() {
		return android.os.Build.MODEL;
	}

	/**
	 * Returns the non-scaled pixel resolution of the current default display
	 * being used by the WindowManager in the specified context.
	 * 
	 * @param context
	 *            context to use to retrieve the current WindowManager
	 * @return a string in the format "WxH", or the empty string "" if
	 *         resolution cannot be determined
	 */
	static String getResolution(final Context context) {
		// user reported NPE in this method; that means either getSystemService
		// or getDefaultDisplay
		// were returning null, even though the documentation doesn't say they
		// should do so; so now
		// we catch Throwable and return empty string if that happens
		String resolution = "";
		try {
			final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			final Display display = wm.getDefaultDisplay();
			final DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			resolution = metrics.widthPixels + "x" + metrics.heightPixels;
		} catch (Throwable t) {
			Log.i(TAG, "Device resolution cannot be determined");
		}
		return resolution;
	}

	/**
	 * Maps the current display density to a string constant.
	 * 
	 * @param context
	 *            context to use to retrieve the current display metrics
	 * @return a string constant representing the current display density, or
	 *         the empty string if the density is unknown
	 */
	static String getDensity(final Context context) {
		final int density = context.getResources().getDisplayMetrics().densityDpi;
		return String.valueOf(density);
	}

	/**
	 * Returns the display name of the current network operator from the
	 * TelephonyManager from the specified context.
	 * 
	 * @param context
	 *            context to use to retrieve the TelephonyManager from
	 * @return the display name of the current network operator, or the empty
	 *         string if it cannot be accessed or determined
	 */
	static String getCarrier(final Context context) {
		String carrier = "";
		final TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager != null) {
			carrier = manager.getNetworkOperatorName();
		}
		return carrier;
	}

	/**
	 * Returns the application version string stored in the specified context's
	 * package info versionName field, or "1.0" if versionName is not present.
	 */
	static String getAppVersion(final Context context) {
		String result = "";
		try {
			result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode + "";
		} catch (PackageManager.NameNotFoundException e) {
			Log.i(TAG, "No app version found");
		}
		return result;
	}

	/**
	 * Returns the package name of the app that installed this app
	 */
	static String getStore(final Context context) {
		String result = "";
		if (android.os.Build.VERSION.SDK_INT >= 3) {
			try {
				result = context.getPackageManager().getInstallerPackageName(context.getPackageName());
			} catch (Exception e) {
				Log.e(TAG, "Can't get Installer package");
			}
		}
		return result;
	}

	/**
	 * Returns a JSON  containing the device metrics to be
	 * associated with a begin session event.
	 */
	static JSONObject getMetrics(final Context context) {
		final JSONObject json = new JSONObject();

		fillJSONIfValuesNotEmpty(json,"_app_name", getAppName(context),"_imei",getImei(context), "_device", getDevice(), "_os_version", getOSVersion(), "_carrier",
				getCarrier(context), "_resolution", getResolution(context), "_density", getDensity(context),
				"_app_version", getAppVersion(context), "_store", getStore(context));

		return json;
	}

	/**
	 * Utility method to fill JSONObject with supplied objects for supplied
	 * keys. Fills json only with non-null and non-empty key/value pairs.
	 * 
	 * @param json
	 *            JSONObject to fill
	 * @param objects
	 *            varargs of this kind: key1, value1, key2, value2, ...
	 */
	static void fillJSONIfValuesNotEmpty(final JSONObject json, final String... objects) {
		try {
			if (objects.length > 0 && objects.length % 2 == 0) {
				for (int i = 0; i < objects.length; i += 2) {
					final String key = objects[i];
					final String value = objects[i + 1];
					if (value != null && value.length() > 0) {
						json.put(key, value);
					}
				}
			}
		} catch (JSONException ignored) {
			// shouldn't ever happen when putting String objects into a
			// JSONObject,
			// it can only happen when putting NaN or INFINITE doubles or floats
			// into it
		}
	}

}
