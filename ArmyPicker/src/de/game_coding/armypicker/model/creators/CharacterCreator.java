package de.game_coding.armypicker.model.creators;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import de.game_coding.armypicker.model.Character;

public class CharacterCreator implements Creator<Character> {

	@Override
	public Character[] newArray(final int size) {
		return new Character[size];
	}

	@Override
	public Character createFromParcel(final Parcel source) {
		return new Character(source);
	}
}
