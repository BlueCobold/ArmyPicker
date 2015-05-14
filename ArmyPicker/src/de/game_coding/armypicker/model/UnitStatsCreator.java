package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UnitStatsCreator implements Creator<UnitStats> {

	@Override
	public UnitStats createFromParcel(final Parcel source) {
		return new UnitStats(source);
	}

	@Override
	public UnitStats[] newArray(final int size) {
		return new UnitStats[size];
	}
}
