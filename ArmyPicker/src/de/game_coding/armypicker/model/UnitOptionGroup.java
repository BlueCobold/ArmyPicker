package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitOptionGroup implements Parcelable {

	public enum GroupType {
		/**
		 * Can only be taken once for the entire unit
		 */
		ONE_PER_UNIT,
		/**
		 * Can be taken x times for the entire unit, no matter the number of
		 * members
		 */
		UP_TO_X_PER_UNIT,
		/**
		 * Taken for each model in the unit - all will be equipped with it
		 */
		ONE_PER_MODEL,
		/**
		 * Can take a number of X of the upgrades for every Y models in the unit
		 */
		UP_TO_X_PER_Y_MODELS
	}

	public final static Parcelable.Creator<UnitOptionGroup> CREATOR = new UnitOptionGroupCreator();

	private GroupType type;

	private int optionNumberPerGroup;

	private int groupSize;

	private int limit;

	private List<UnitOption> options = new ArrayList<UnitOption>();

	public UnitOptionGroup(final GroupType type, final UnitOption... options) {
		this.type = type;
		this.optionNumberPerGroup = 1;
		this.groupSize = 1;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final GroupType type, final int optionNumberPerGroup, final int groupSize,
			final UnitOption... options) {
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.groupSize = groupSize;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final Parcel source) {
		readFromParecl(source);
	}

	public GroupType getType() {
		return type;
	}

	public int getOptionNumberPerGroup() {
		return optionNumberPerGroup;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(final int limit) {
		this.limit = limit;
		validateAmounts();
	}

	public void validateAmounts() {
		final int max = getMaxAmount();
		int current = 0;
		for (final UnitOption option : options) {
			if (type == GroupType.ONE_PER_MODEL) {
				option.setAmountSelected(option.getAmountSelected() > 0 ? limit : 0);
			}
			current += option.getAmountSelected();
			if (current > max) {
				option.setAmountSelected(option.getAmountSelected() + max - current);
				current = max;
			}
		}
	}

	public List<UnitOption> getOptions() {
		return options;
	}

	private int getMaxAmount() {
		int max = limit;
		switch (type) {
		case ONE_PER_UNIT:
			max = 1;
			break;

		case ONE_PER_MODEL:
			max = limit;
			break;

		case UP_TO_X_PER_UNIT:
			max = optionNumberPerGroup;
			break;

		default:
			max = optionNumberPerGroup * (limit / groupSize);
			break;
		}
		return max;
	}

	public boolean canSelectMore() {
		final int max = getMaxAmount();
		int current = 0;
		for (final UnitOption option : options) {
			current += option.getAmountSelected();
			if (current > max) {
				option.setAmountSelected(option.getAmountSelected() + max - current);
				current = max;
			}
		}
		return current < max;
	}

	public int getTotalCosts() {
		int total = 0;
		for (final UnitOption option : options) {
			total += option.getAmountSelected() * option.getCosts();
		}
		return total;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		dest.writeInt(type.ordinal());
		dest.writeInt(optionNumberPerGroup);
		dest.writeInt(groupSize);
		dest.writeList(options);
	}

	private void readFromParecl(final Parcel source) {
		type = GroupType.values()[source.readInt()];
		optionNumberPerGroup = source.readInt();
		groupSize = source.readInt();

		options = new ArrayList<UnitOption>();
		source.readList(options, UnitOption.class.getClassLoader());
	}
}
