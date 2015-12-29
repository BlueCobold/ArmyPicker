package de.game_coding.armypicker.model.creators;

import de.game_coding.armypicker.model.Army;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class ArmyCreator implements Creator<Army> {

	@Override
	public Army createFromParcel(final Parcel source) {
		return new Army(source);
	}

	@Override
	public Army[] newArray(final int size) {
		return new Army[size];
	}
}
