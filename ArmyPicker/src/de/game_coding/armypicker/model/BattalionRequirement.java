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
			return new BattalionRequirement(source);
		}
	};

	private String name;

	private int minCount;

	private int maxCount;

	private List<BattalionRequirement> requiredSubBattalions = new ArrayList<BattalionRequirement>();

	private List<BattalionRequirement> assignedSubBattalions = new ArrayList<BattalionRequirement>();

	private List<UnitRequirement> requiredUnits = new ArrayList<UnitRequirement>();

	private List<Unit> assignedUnits = new ArrayList<Unit>();

	private BattalionChoice choice;

	private boolean isMeta;

	private String description;

	private List<GameRule> rules = new ArrayList<GameRule>();

	private Object tag;

	public BattalionRequirement(final String name, final int minCount, final int maxCount) {
		super();
		this.name = name;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.choice = BattalionChoice.X_OF;
	}

	public BattalionRequirement(final String name, final int minCount, final int maxCount,
		final BattalionChoice choice) {
		super();
		this.name = name;
		this.minCount = minCount;
		this.maxCount = maxCount;
		this.choice = choice;
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

	public List<UnitRequirement> getRequiredUnits() {
		return requiredUnits;
	}

	public BattalionRequirement addUnit(final String name) {
		return addUnit(name, 1, 1);
	}

	public BattalionRequirement addUnit(final String name, final int min, final int max) {
		return addUnit(name, min, max, 0);
	}

	public BattalionRequirement addUnit(final String name, final int min, final int max, final int minModels) {
		requiredUnits.add(new UnitRequirement(name, min, max, minModels));
		return this;
	}

	public BattalionRequirement assignSubBattalion(final BattalionRequirement battalion) {
		if (battalion == null || assignedSubBattalions.contains(battalion)) {
			return this;
		}
		for (final BattalionRequirement req : requiredSubBattalions) {
			if (req.name.equals(battalion.name)) {
				assignedSubBattalions.add(battalion);
			}
		}
		return this;
	}

	public void removeSubBattalion(final BattalionRequirement battalion) {
		assignedSubBattalions.remove(battalion);
	}

	public Collection<BattalionRequirement> getAssignedSubBattalions() {
		return assignedSubBattalions;
	}

	public void assignUnit(final Unit unit) {
		for (final UnitRequirement req : requiredUnits) {
			if (req.getUnitName().equals(unit.getName())) {
				assignedUnits.add(unit);
			}
		}
	}

	public void removeUnit(final Unit unit) {
		assignedUnits.remove(unit);
	}

	public Collection<Unit> getAssignedUnits() {
		return assignedUnits;
	}

	public BattalionRequirement markAsMeta() {
		isMeta = true;
		return this;
	}

	public boolean isMeta() {
		return isMeta;
	}

	public BattalionChoice getChoice() {
		return choice;
	}

	public Object getTag() {
		return tag;
	}

	public void setTag(final Object tag) {
		this.tag = tag;
	}

	public String getDescription() {
		return description;
	}

	public BattalionRequirement withDescription(final String description) {
		this.description = description;
		return this;
	}

	public BattalionRequirement withRule(final String title, final String description) {
		return withRule(new GameRule(title, description));
	}

	public BattalionRequirement withRule(final GameRule rule) {
		rules.add(rule);
		return this;
	}

	public Collection<GameRule> getRules() {
		return rules;
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		name = source.readString();
		minCount = source.readInt();
		maxCount = source.readInt();
		requiredSubBattalions = readList(source, CREATOR);
		requiredUnits = readList(source, UnitRequirement.CREATOR);
		assignedSubBattalions = readList(source, CREATOR);
		assignedUnits = readList(source, Unit.CREATOR);
		// assignedUnits = source.readArrayList(Integer.class.getClassLoader());
		choice = BattalionChoice.values()[source.readInt()];
		isMeta = source.readInt() == 1;
		description = source.readString();
		rules = readList(source, GameRule.CREATOR);
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
		writeList(dest, requiredUnits);
		writeList(dest, assignedSubBattalions);
		writeList(dest, assignedUnits);
		// dest.writeList(assignedUnits);
		dest.writeInt(choice.ordinal());
		dest.writeInt(isMeta ? 1 : 0);
		dest.writeString(description != null ? description : "");
		writeList(dest, rules);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	@Override
	public String toString() {
		final StringBuilder description = new StringBuilder(name).append(" [");
		for (final BattalionRequirement sub : assignedSubBattalions) {
			description.append(sub.getName()).append(", ");
		}
		return description.append("]").toString();
	}

	public int getUnitCosts() {
		int total = 0;
		for (final BattalionRequirement sub : getAssignedSubBattalions()) {
			total += sub.getUnitCosts();
		}
		for (final Unit unit : getAssignedUnits()) {
			total += unit.getTotalCosts();
		}
		return total;
	}
}
