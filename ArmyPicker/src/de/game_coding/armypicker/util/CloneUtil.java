package de.game_coding.armypicker.util;

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
}
