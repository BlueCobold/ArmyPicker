package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class UnitOptionGroupCreator implements Creator<UnitOptionGroup> {

	private final Creator<?> ruleCreator;

	public UnitOptionGroupCreator(final Parcelable.Creator<?> ruleCreator) {
		this.ruleCreator = ruleCreator;
	}

	@Override
	public UnitOptionGroup createFromParcel(final Parcel source) {
		return new UnitOptionGroup(source, ruleCreator);
	}

	@Override
	public UnitOptionGroup[] newArray(final int size) {
		return new UnitOptionGroup[size];
	}
}
