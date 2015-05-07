package de.game_coding.armypicker.model;

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
}
