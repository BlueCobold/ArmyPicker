package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.os.Parcel;

public class BattalionRequirement extends Model {

	public static final Creator<BattalionRequirement> CREATOR = new Creator<BattalionRequirement>() {

		@Override
		public BattalionRequirement[] newArray(final int size) {
			return new BattalionRequirement[size];
		}

		@Override
		public BattalionRequirement createFromParcel(final Parcel source) {
			return null;
		}
	};

	private String name;

	private int minCount;

	private int maxCount;

	private List<BattalionRequirement> requiredSubBattalions = new ArrayList<BattalionRequirement>();

	private List<BattalionRequirement> assignedBattalions = new ArrayList<BattalionRequirement>();

	private final List<String> requiredUnitNames = new ArrayList<String>();

	private List<Integer> assignedUnits = new ArrayList<Integer>();

	public BattalionRequirement(final String name, final int minCount, final int maxCount) {
		super();
		this.name = name;
		this.minCount = minCount;
		this.maxCount = maxCount;
	}

	public BattalionRequirement(final Parcel source) {
		readFromParcel(source);
	}

	public String getName() {
		return name;
	}

	public int getMinCount() {
		return minCount;
	}

	public int getMaxCount() {
		return maxCount;
	}

	public List<BattalionRequirement> getRequiredSubBattalions() {
		return requiredSubBattalions;
	}

	public BattalionRequirement addBattalion(final BattalionRequirement battalion) {
		requiredSubBattalions.add(battalion);
		return this;
	}

	public List<String> getRequiredUnitNames() {
		return requiredUnitNames;
	}

	public BattalionRequirement addUnitName(final String name) {
		requiredUnitNames.add(name);
		return this;
	}

	public void assignBattalion(final BattalionRequirement battalion) {
		for (final BattalionRequirement req : requiredSubBattalions) {
			if (req.name.equals(name)) {
				assignedBattalions.add(battalion);
			}
		}
	}

	public void removeBattalion(final BattalionRequirement battalion) {
		assignedBattalions.remove(battalion);
	}

	public Collection<BattalionRequirement> getAssignedBattalions() {
		return assignedBattalions;
	}

	public void assignUnit(final Unit unit) {
		for (final String req : requiredUnitNames) {
			if (req.equals(unit.getName())) {
				assignedUnits.add(unit.getId());
			}
		}
	}

	public void removeBattalion(final Unit unit) {
		assignedUnits.remove(unit.getId());
	}

	public Collection<Integer> getAssignedUnits() {
		return assignedUnits;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		name = source.readString();
		minCount = source.readInt();
		maxCount = source.readInt();
		requiredSubBattalions = readList(source, CREATOR);
		requiredUnitNames.clear();
		source.readStringList(requiredUnitNames);
		assignedBattalions = readList(source, CREATOR);
		assignedUnits = source.readArrayList(Integer.class.getClassLoader());
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		dest.writeString(name);
		dest.writeInt(minCount);
		dest.writeInt(maxCount);
		writeList(dest, requiredSubBattalions);
		dest.writeStringList(requiredUnitNames);
		writeList(dest, assignedBattalions);
		dest.writeList(assignedUnits);
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
