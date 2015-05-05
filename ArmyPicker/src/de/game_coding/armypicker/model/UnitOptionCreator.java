package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UnitOptionCreator implements Creator<UnitOption> {

	@Override
	public UnitOption[] newArray(final int size) {
		return new UnitOption[size];
	}

	@Override
	public UnitOption createFromParcel(final Parcel source) {
		return new UnitOption(source);
	}
}
