package de.game_coding.armypicker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitOption implements Parcelable {

	public static Parcelable.Creator<UnitOption> CREATOR = new UnitOptionCreator();

	private String name;
	private int costs;
	private int amountSelected;
	private int id;
	private boolean enabled = true;

	public UnitOption(final int id, final String name, final int costs, final int defaultAmount) {
		this.id = id;
		this.name = name;
		this.costs = costs;
		this.amountSelected = defaultAmount;
	}

	public UnitOption(final String name, final int costs, final int defaultAmount) {
		this.name = name;
		this.costs = costs;
		this.amountSelected = defaultAmount;
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

	public int getCosts() {
		return costs;
	}

	public int getAmountSelected() {
		return amountSelected;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(name);
		dest.writeInt(id);
		dest.writeInt(costs);
		dest.writeInt(amountSelected);
	};

	private void readFromParcel(final Parcel source) {
		name = source.readString();
		id = source.readInt();
		costs = source.readInt();
		amountSelected = source.readInt();
	}
}
