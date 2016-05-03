package de.game_coding.armypicker.model.creators;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import de.game_coding.armypicker.model.GameRule;

public class GameRuleCreator implements Creator<GameRule> {

	@Override
	public GameRule[] newArray(final int size) {
		return new GameRule[size];
	}

	@Override
	public GameRule createFromParcel(final Parcel source) {
		return new GameRule(source);
	}
}
