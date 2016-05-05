package de.game_coding.armypicker.model;

import android.os.Parcel;

public class UnitRequirement extends Model {

	public static final Creator<UnitRequirement> CREATOR = new Creator<UnitRequirement>() {

		@Override
		public UnitRequirement[] newArray(final int size) {
			return new UnitRequirement[size];
		}

		@Override
		public UnitRequirement createFromParcel(final Parcel source) {
			return new UnitRequirement(source);
		}
	};

	private int min;

	private int max;

	private String unitName;

	private int minModels;

	public UnitRequirement(final String unitName, final int min, final int max, final int minModels) {
		this.min = min;
		this.max = max;
		this.unitName = unitName;
		this.minModels = minModels;
	}

	public UnitRequirement(final Parcel source) {
		readFromParcel(source);
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public String getUnitName() {
		return unitName;
	}

	public int getMinModels() {
		return minModels;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		unitName = source.readString();
		min = source.readInt();
		max = source.readInt();
		minModels = source.readInt();
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(unitName);
		dest.writeInt(min);
		dest.writeInt(max);
		dest.writeInt(minModels);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

}
