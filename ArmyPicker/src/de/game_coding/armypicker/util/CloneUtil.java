package de.game_coding.armypicker.util;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public final class CloneUtil {
	private CloneUtil() {
	}

	public static <T extends Parcelable> T clone(final T original, final Parcelable.Creator<T> creator) {
		final Parcel parcel = Parcel.obtain();
		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final T copy = creator.createFromParcel(parcel);
		parcel.recycle();
		return copy;
	}

	public static <T extends Parcelable> List<T> clone(final List<T> models, final Parcelable.Creator<T> creator) {
		final List<T> result = new ArrayList<T>();
		for (final T model : models) {
			result.add(clone(model, creator));
		}
		return result;
	}
}
