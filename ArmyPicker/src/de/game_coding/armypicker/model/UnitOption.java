package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable;
import de.game_coding.armypicker.model.creators.UnitOptionCreator;

public class UnitOption extends Model {

	public static Parcelable.Creator<UnitOption> CREATOR = new UnitOptionCreator();

	private String name = "";
	private int costs;
	private int amountSelected;
	private int defaultAmount;
	private int id;
	private boolean enabled = true;
	private int parentId = -1;
	private String longName = "";

	public UnitOption(final int id, final String name, final int costs, final int defaultAmount) {
		this.id = id;
		this.name = name;
		this.costs = costs;
		this.amountSelected = defaultAmount;
		this.defaultAmount = defaultAmount;
	}

	public UnitOption(final String name, final int costs, final int defaultAmount) {
		this.name = name;
		this.costs = costs;
		this.amountSelected = defaultAmount;
		this.defaultAmount = defaultAmount;
	}

	public UnitOption(final String name, final int costs) {
		this.name = name;
		this.costs = costs;
	}

	public UnitOption(final int id, final String name, final int costs) {
		this.id = id;
		this.name = name;
		this.costs = costs;
	}

	public UnitOption(final Parcel source) {
		readFromParcel(source);
	}

	public String getName() {
		return name;
	}

	public String getLongName() {
		return longName.isEmpty() ? name : longName;
	}

	public int getCosts() {
		return costs;
	}

	public int getAmountSelected() {
		return amountSelected;
	}

	public int getDefaultAmount() {
		return defaultAmount;
	}

	public void setAmountSelected(final int amountSelected) {
		this.amountSelected = amountSelected;
	}

	public int getId() {
		return id;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
	}

	public UnitOption makeToChildOf(final int parentId) {
		this.parentId = parentId;
		return this;
	}

	public UnitOption applyLongName(final String name) {
		longName = name;
		return this;
	}

	public int getParentId() {
		return parentId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeString(longName);
		dest.writeInt(id);
		dest.writeInt(costs);
		dest.writeInt(amountSelected);
		dest.writeInt(defaultAmount);
		dest.writeInt(parentId);
	};

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		name = source.readString();
		longName = source.readString();
		id = source.readInt();
		costs = source.readInt();
		amountSelected = source.readInt();
		if (getFileVersion() >= 1) {
			defaultAmount = source.readInt();
		}
		parentId = source.readInt();
	}

	@Override
	protected int getFeatureVersion() {
		return 1;
	}

	@Override
	public String toString() {
		return getLongName() + " x" + amountSelected;
	}
}
