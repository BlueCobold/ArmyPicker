package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.creators.CharacterOptionCreator;

public class CharacterOption extends Model {

	public static final Parcelable.Creator<CharacterOption> CREATOR = new CharacterOptionCreator();

	private String name;

	private List<CharacterOption> subOptions = new ArrayList<CharacterOption>();

	public CharacterOption() {
	}

	public CharacterOption(final String name) {
		this.name = name;
	}

	public CharacterOption(final String name, final CharacterOption subOption) {
		this.name = name;
		subOptions.add(subOption);
	}

	public CharacterOption(final Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void addSubOption(final String name) {
		subOptions.add(new CharacterOption(name));
	}

	public List<CharacterOption> getSubOptions() {
		return subOptions;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		name = source.readString();
		subOptions = readList(source, CREATOR);
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		writeList(dest, subOptions);
	}

	@Override
	public String toString() {
		return name;
	}
}
