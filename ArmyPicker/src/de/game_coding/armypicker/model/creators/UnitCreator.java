package de.game_coding.armypicker.model.creators;

import de.game_coding.armypicker.model.Unit;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class UnitCreator implements Creator<Unit> {

	@Override
	public Unit createFromParcel(final Parcel source) {
		return new Unit(source);
	}

	@Override
	public Unit[] newArray(final int size) {
		return new Unit[size];
	}

}
