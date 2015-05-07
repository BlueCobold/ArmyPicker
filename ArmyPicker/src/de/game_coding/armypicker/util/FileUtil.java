package de.game_coding.armypicker.util;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Parcel;
import android.util.Log;
import de.game_coding.armypicker.model.Army;

public final class FileUtil {

	private static final String TAG = FileUtil.class.getName();

	private static final String CANNOT_READ_ARMY_FILE = "Cannot read army file";
	private static final String CANNOT_CLOSE_ARMY_FILE = "Cannot close army file";
	private static final String CANNOT_WRITE_ARMY_FILE = "Cannot write army file";
	private static final String FILE_SUFFIX = ".army";

	private FileUtil() {
	}

	public static void storeArmy(final Army army, final Context context) {
		final File dir = getFilePath(context);
		final Parcel parcel = Parcel.obtain();
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(dir.getAbsolutePath(), army.getId() + FILE_SUFFIX));
			army.writeToParcel(parcel, 0);
			os.write(parcel.marshall());
		} catch (final FileNotFoundException e) {
			Log.e(TAG, CANNOT_WRITE_ARMY_FILE, e);
		} catch (final IOException e) {
			Log.e(TAG, CANNOT_WRITE_ARMY_FILE, e);
		}
		if (os != null) {
			try {
				os.close();
			} catch (final IOException e) {
				Log.e(TAG, CANNOT_CLOSE_ARMY_FILE, e);
			}
		}
		parcel.recycle();
	}

	public static List<Army> readArmies(final Context context) {
		final List<Army> results = new ArrayList<Army>();
		final File dir = getFilePath(context);
		final File[] armies = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(final File pathname) {
				return pathname.getAbsolutePath().endsWith(FILE_SUFFIX);
			}
		});
		for (final File armyFile : armies) {
			final Army army = readArmy(armyFile, context);
			if (army != null) {
				results.add(army);
			}
		}
		return results;
	}

	public static Army readArmy(final File file, final Context context) {
		final Parcel parcel = Parcel.obtain();
		Army result = null;
		InputStream os = null;
		try {
			os = new FileInputStream(file);
			final int length = os.available();
			final byte[] buffer = new byte[length];
			os.read(buffer);
			parcel.unmarshall(buffer, 0, length);
			parcel.setDataPosition(0);
			result = Army.CREATOR.createFromParcel(parcel);
		} catch (final FileNotFoundException e) {
			Log.e(TAG, CANNOT_READ_ARMY_FILE, e);
		} catch (final IOException e) {
			Log.e(TAG, CANNOT_READ_ARMY_FILE, e);
		}
		if (os != null) {
			try {
				os.close();
			} catch (final IOException e) {
				Log.e(TAG, CANNOT_CLOSE_ARMY_FILE, e);
			}
		}
		parcel.recycle();
		return result;
	}

	public static void delete(final Army army, final Context context) {
		final File dir = getFilePath(context);
		final File armyFile = new File(dir, army.getId() + FILE_SUFFIX);
		if (armyFile.exists()) {
			armyFile.delete();
		}
	}

	private static File getFilePath(final Context context) {
		File dir = context.getExternalFilesDir(null);
		if (dir == null || !dir.exists()) {
			dir = context.getFilesDir();
		}
		return dir;
	}
}
