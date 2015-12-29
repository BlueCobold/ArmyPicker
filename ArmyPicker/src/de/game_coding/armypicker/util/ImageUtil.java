package de.game_coding.armypicker.util;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;

public final class ImageUtil {

	public static final int IMAGE_QUALITY = 90;

	private ImageUtil() {
	}

	public static Bitmap decodeUri(final Context context, final Uri uri, final int preferredWidth,
		final int preferredHeight) {
		if (uri == null) {
			return null;
		}
		ParcelFileDescriptor parcelFD = null;
		try {
			parcelFD = context.getContentResolver().openFileDescriptor(uri, "r");
			final FileDescriptor imageSource = parcelFD.getFileDescriptor();

			final BitmapFactory.Options bounds = new BitmapFactory.Options();
			bounds.inJustDecodeBounds = true;
			BitmapFactory.decodeFileDescriptor(imageSource, null, bounds);

			final BitmapFactory.Options opts = new BitmapFactory.Options();

			if (bounds.outWidth > preferredWidth) {
				opts.inSampleSize = (int) (bounds.outWidth / (float) preferredWidth);
			} else if (bounds.outHeight > preferredHeight) {
				opts.inSampleSize = (int) (bounds.outHeight / (float) preferredHeight);
			}

			Bitmap bmp = BitmapFactory.decodeFileDescriptor(imageSource, null, opts);
			if (bmp == null) {
				return null;
			}

			final int imageOrientation = getImageOrientation(context, uri);
			if (imageOrientation != -1) {
				final Matrix mat = new Matrix();
				mat.postRotate(imageOrientation);
				bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), mat, true);
			}
			return bmp;
		} catch (final Exception e) {
			Log.e(ImageUtil.class.getName(), "Cannot load image", e);
		} finally {
			if (parcelFD != null) {
				try {
					parcelFD.close();
				} catch (final IOException e) {
					Log.e(ImageUtil.class.getName(), "Cannot close file descriptor", e);
				}
			}
		}
		return null;
	}

	public static Bitmap toRect(final Bitmap src) {
		final Matrix mat = new Matrix();
		final int min = Math.min(src.getWidth(), src.getHeight());
		return Bitmap.createBitmap(src, (src.getWidth() - min) / 2, (src.getHeight() - min) / 2, min, min, mat, false);
	}

	public static Uri saveBitmap(final Context context, final String fileName, final Bitmap src) {
		FileOutputStream target = null;
		try {
			target = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			src.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, target);
		} catch (final FileNotFoundException e) {
			Log.e(ImageUtil.class.getName(), "Cannot save image", e);
		} finally {
			try {
				if (target != null) {
					target.close();
				}
			} catch (final IOException e) {
				Log.w(ImageUtil.class.getName(), "Cannot close the output stream.", e);
			}
		}
		return Uri.parse(context.getFilesDir().getAbsolutePath() + "/" + fileName);
	}

	public static int getImageOrientation(final Context context, final Uri src) {
		try {
			final Cursor cursor = context.getContentResolver().query(src,
				new String[] { MediaStore.Images.ImageColumns.ORIENTATION }, null, null, null);

			if (cursor == null || cursor.getCount() != 1) {
				return -1;
			}

			cursor.moveToFirst();
			return cursor.getInt(0);
		} catch (final Exception e) {
			return -1;
		}
	}
}
