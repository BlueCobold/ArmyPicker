package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class Model implements Parcelable {

	private int fileVersion = -1;

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(getFeatureVersion());
	}

	protected void readFromParcel(final Parcel source) {
		fileVersion = source.readInt();
	}

	protected abstract int getFeatureVersion();

	public int getFileVersion() {
		return fileVersion;
	}

	protected static void writeList(final Parcel dest, final List<? extends Parcelable> model) {
		dest.writeInt(model.size());
		for (final Parcelable m : model) {
			m.writeToParcel(dest, 0);
		}
	}

	protected static <T extends Parcelable> List<T> readList(final Parcel source, final Parcelable.Creator<T> creator) {
		final int count = source.readInt();
		final ArrayList<T> result = new ArrayList<T>();
		for (int i = 0; i < count; i++) {
			result.add(creator.createFromParcel(source));
		}
		return result;
	}
}
