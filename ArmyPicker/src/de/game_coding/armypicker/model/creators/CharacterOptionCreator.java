package de.game_coding.armypicker.model.creators;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import de.game_coding.armypicker.model.CharacterOption;

public class CharacterOptionCreator implements Creator<CharacterOption> {

	@Override
	public CharacterOption[] newArray(final int size) {
		return new CharacterOption[size];
	}

	@Override
	public CharacterOption createFromParcel(final Parcel source) {
		return new CharacterOption(source);
	}
}
