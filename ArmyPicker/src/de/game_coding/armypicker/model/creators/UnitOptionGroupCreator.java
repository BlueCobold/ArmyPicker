package de.game_coding.armypicker.model.creators;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import de.game_coding.armypicker.model.IRule;
import de.game_coding.armypicker.model.UnitOptionGroup;

public class UnitOptionGroupCreator implements Creator<UnitOptionGroup> {

	private final Creator<IRule> ruleCreator;

	public UnitOptionGroupCreator(final Parcelable.Creator<IRule> ruleCreator) {
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
