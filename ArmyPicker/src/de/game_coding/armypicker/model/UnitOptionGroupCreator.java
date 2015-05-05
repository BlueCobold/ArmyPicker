package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UnitOptionGroupCreator implements Creator<UnitOptionGroup> {

	@Override
	public UnitOptionGroup createFromParcel(final Parcel source) {
		return new UnitOptionGroup(source);
	}

	@Override
	public UnitOptionGroup[] newArray(final int size) {
		return new UnitOptionGroup[size];
	}
}
