package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Army implements Parcelable {

	public static final Parcelable.Creator<Army> CREATOR = new ArmyCreator();

	private String name;
	private Unit[] unitTemplates;
	private List<Unit> units = new ArrayList<Unit>();

	public Army(final String name, final Unit[] unitTemplates) {
		this.name = name;
		this.unitTemplates = unitTemplates;
	}

	public Army(final Parcel source) {
		readFromParcel(source);
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Unit[] getUnitTemplates() {
		return unitTemplates;
	}

	public List<Unit> getUnits() {
		return units;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeString(name);

		dest.writeInt(unitTemplates.length);
		dest.writeTypedArray(unitTemplates, flags);

		dest.writeList(units);
	}

	private void readFromParcel(final Parcel source) {
		name = source.readString();

		final int size = source.readInt();
		unitTemplates = new Unit[size];
		source.readTypedArray(unitTemplates, Unit.CREATOR);

		units = new ArrayList<Unit>();
		source.readList(units, Unit.class.getClassLoader());
	}

	public int getTotalCosts() {
		int total = 0;
		for (final Unit unit : units) {
			total += unit.getTotalCosts();
		}
		return total;
	}
}
