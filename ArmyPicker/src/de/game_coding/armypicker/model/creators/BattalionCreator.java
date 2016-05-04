package de.game_coding.armypicker.model.creators;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import de.game_coding.armypicker.model.Battalion;

public class BattalionCreator implements Creator<Battalion> {

	@Override
	public Battalion createFromParcel(final Parcel source) {
		return new Battalion(source);
	}

	@Override
	public Battalion[] newArray(final int size) {
		return new Battalion[size];
	}
}
