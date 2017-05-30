package de.game_coding.armypicker.util;

import java.io.ByteArrayOutputStream;
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
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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

	public static void storeToFile(final Parcelable data, final String fileName, final Context context) {
		final File dir = getFilePath(context);
		if (dir == null) {
			return;
		}
		final Parcel parcel = Parcel.obtain();
		OutputStream os = null;
		try {
			os = new FileOutputStream(new File(dir.getAbsolutePath(), fileName));
			data.writeToParcel(parcel, 0);
			final Deflater compressor = new Deflater();
			compressor.setLevel(Deflater.BEST_COMPRESSION);
			compressor.setInput(parcel.marshall());
			compressor.finish();
			final byte[] buffer = new byte[1024];
			while (!compressor.finished()) {
				final int count = compressor.deflate(buffer);
				if (count == 0) {
					break;
				}
				os.write(buffer, 0, count);
			}
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

	public static void storeArmy(final Army army, final Context context) {
		storeToFile(army, army.getId() + FILE_SUFFIX, context);
	}

	public static List<Army> readArmies(final Context context, final String infix, final boolean include) {
		final List<Army> results = new ArrayList<Army>();
		final File dir = getFilePath(context);
		final File[] armies = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(final File pathname) {
				return pathname.getAbsolutePath().endsWith(FILE_SUFFIX)
					&& (pathname.getAbsolutePath().contains(infix) == include);
			}
		});
		for (final File armyFile : armies) {
			final Army army = readFromFile(armyFile, context, Army.CREATOR);
			if (army != null) {
				results.add(army);
			}
		}
		return results;
	}

	public static <T> T readFromFile(final String fileName, final Context context,
		final Parcelable.Creator<T> creator) {
		final File dir = getFilePath(context);
		if (dir == null) {
			return null;
		}
		return readFromFile(new File(dir.getAbsolutePath(), fileName), context, creator);
	}

	private static <T> T readFromFile(final File file, final Context context, final Parcelable.Creator<T> creator) {
		T result = null;
		InputStream os = null;
		try {
			os = new FileInputStream(file);
			final int available = os.available();
			final byte[] buffer = new byte[available];
			os.read(buffer);
			result = readData(buffer, creator);
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
		return result;
	}

	public static <T> T readData(byte[] buffer, final Parcelable.Creator<T> creator) {
		final Parcel parcel = Parcel.obtain();
		final Inflater inflater = new Inflater();
		inflater.setInput(buffer);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(buffer.length);
		buffer = new byte[1024];
		try {
			while (!inflater.finished()) {
				final int count = inflater.inflate(buffer);
				if (count == 0) {
					break;
				}
				outputStream.write(buffer, 0, count);
			}
			outputStream.close();
		} catch (final DataFormatException e) {
			Log.e(TAG, CANNOT_READ_ARMY_FILE, e);
		} catch (final IOException e) {
			Log.e(TAG, CANNOT_READ_ARMY_FILE, e);
		}
		final byte[] output = outputStream.toByteArray();
		parcel.unmarshall(output, 0, output.length);
		parcel.setDataPosition(0);
		final T result = creator.createFromParcel(parcel);
		parcel.recycle();
		return result;
	}

	public static void delete(final Army army, final Context context) {
		deleteFile(army.getId() + FILE_SUFFIX, context);
	}

	public static void deleteFile(final String file, final Context context) {
		final File dir = getFilePath(context);
		final File armyFile = new File(dir, file);
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
