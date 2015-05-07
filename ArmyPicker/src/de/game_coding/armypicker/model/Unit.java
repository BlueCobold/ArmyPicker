package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class Unit extends Model {

	public static final Parcelable.Creator<Unit> CREATOR = new UnitCreator();

	private String name = "";
	private Type type = Type.STANDARD;
	private int points;
	private int amount = 1;
	private int initialAmount = 1;
	private int maxAmount = 1;
	private List<UnitOptionGroup> options = new ArrayList<UnitOptionGroup>();

	public Unit(final String name, final Type type, final int points, final int amount, final int maxAmount,
		final UnitOptionGroup... options) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.amount = amount;
		initialAmount = amount;
		this.maxAmount = maxAmount;
		this.options = Arrays.asList(options);
		setOptionAmounts();
	}

	public Unit(final String name, final Type type, final int points, final UnitOptionGroup... options) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.options = Arrays.asList(options);
		setOptionAmounts();
	}

	public Unit(final String name, final Type type, final int points) {
		this.name = name;
		this.type = type;
		this.points = points;
		this.amount = 1;
		setOptionAmounts();
	}

	public Unit(final Parcel source) {
		readFromParcel(source);
		setOptionAmounts();
	}

	private void setOptionAmounts() {
		for (final UnitOptionGroup group : options) {
			group.setLimit(amount);
		}
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Type getType() {
		return type;
	}

	public int getPoints() {
		return points;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(final int amount) {
		this.amount = amount;
		setOptionAmounts();
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	public List<UnitOptionGroup> getOptions() {
		return options;
	}

	public int getTotalCosts() {
		return amount * points + getTotalOptionCosts();
	}

	public int getTotalOptionCosts() {
		int total = 0;
		for (final UnitOptionGroup group : options) {
			total += group.getTotalCosts();
		}
		return total;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(name);
		dest.writeInt(points);
		dest.writeInt(type.ordinal());
		dest.writeInt(amount);
		dest.writeInt(maxAmount);
		dest.writeInt(initialAmount);

		dest.writeTypedList(options);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		name = source.readString();
		points = source.readInt();
		type = Type.values()[source.readInt()];
		amount = source.readInt();
		maxAmount = source.readInt();
		initialAmount = source.readInt();

		options = new ArrayList<UnitOptionGroup>();
		source.readTypedList(options, new UnitOptionGroupCreator(OptionRule.CREATOR));
		for (final UnitOptionGroup group : options) {
			for (final IRule rule : group.getRules()) {
				((OptionRule) rule).setTargets(options);
			}
		}
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	public Unit addRule(final int targetGroupId, final OptionRule rule) {
		rule.setTargets(options);
		for (final UnitOptionGroup group : options) {
			if (group.getId() == targetGroupId) {
				group.getRules().add(rule);
				break;
			}
		}
		return this;
	}
}
