package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Army extends Model {

	public static final Parcelable.Creator<Army> CREATOR = new ArmyCreator();

	private String name = "";
	private Unit[] unitTemplates = new Unit[0];
	private List<Unit> units = new ArrayList<Unit>();
	private int id;
	private UnitStats stats = new UnitStats();

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

	public int getId() {
		return id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeInt(id);

		dest.writeInt(unitTemplates.length);
		dest.writeTypedArray(unitTemplates, flags);

		dest.writeList(units);
		stats.writeToParcel(dest, flags);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		name = source.readString();
		id = source.readInt();

		final int size = source.readInt();
		unitTemplates = new Unit[size];
		source.readTypedArray(unitTemplates, Unit.CREATOR);

		units = new ArrayList<Unit>();
		source.readList(units, Unit.class.getClassLoader());
		stats = new UnitStats(source);
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public int getTotalCosts() {
		int total = 0;
		for (final Unit unit : units) {
			total += unit.getTotalCosts();
		}
		return total;
	}

	public Army attachStats(final UnitStats stats) {
		this.stats = stats;
		return this;
	}

	public UnitStats getStats() {
		return stats;
	}
}
