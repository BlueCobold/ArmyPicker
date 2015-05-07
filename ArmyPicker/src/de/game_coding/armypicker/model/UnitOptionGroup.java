package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitOptionGroup extends Model {

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
		 * Taken for each model in the unit - all will be equipped with it
		 * except one model
		 */
		ONE_PER_MODEL_EXEPT_ONE,
		/**
		 * Can take a number of X of the upgrades for every Y models in the unit
		 */
		UP_TO_X_PER_Y_MODELS,
		/**
		 * Can take each upgrade in this group X-times for every Y models in the
		 * unit
		 */
		UP_TO_X_OF_EACH_PER_Y_MODELS
	}

	private GroupType type = GroupType.ONE_PER_MODEL;

	private int optionNumberPerGroup;

	private int groupSize;

	private int limit;

	private List<UnitOption> options = new ArrayList<UnitOption>();

	private List<IRule> rules = new ArrayList<IRule>();

	private int id;

	private Creator<?> ruleCreator;

	private boolean enabled = true;

	public UnitOptionGroup(final int id, final GroupType type, final UnitOption... options) {
		this.id = id;
		this.type = type;
		this.optionNumberPerGroup = 1;
		this.groupSize = 1;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final GroupType type, final UnitOption... options) {
		this.type = type;
		this.optionNumberPerGroup = 1;
		this.groupSize = 1;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final int id, final GroupType type, final int optionNumberPerGroup, final int groupSize,
		final UnitOption... options) {
		this.id = id;
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.groupSize = groupSize;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final GroupType type, final int optionNumberPerGroup, final int groupSize,
		final UnitOption... options) {
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.groupSize = groupSize;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final Parcel source, final Creator<?> ruleCreator) {
		this.ruleCreator = ruleCreator;
		readFromParcel(source);
	}

	public GroupType getType() {
		return type;
	}

	public void setType(final GroupType type) {
		this.type = type;
		validateAmounts();
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
		final int max = enabled ? getMaxAmount() : 0;
		int current = 0;
		for (final UnitOption option : options) {
			if (type == GroupType.ONE_PER_MODEL || type == GroupType.ONE_PER_MODEL_EXEPT_ONE) {
				option.setAmountSelected(option.getAmountSelected() > 0 ? max : 0);
			} else if (type == GroupType.UP_TO_X_OF_EACH_PER_Y_MODELS) {
				option.setAmountSelected(Math.min(option.getAmountSelected(), max));
				continue;
			}
			current += option.getAmountSelected();
			if (current > max) {
				option.setAmountSelected(option.getAmountSelected() + max - current);
				current = max;
			}
		}
		for (final IRule rule : rules) {
			rule.check();
		}
		return;
	}

	public List<UnitOption> getOptions() {
		return options;
	}

	public List<IRule> getRules() {
		return rules;
	}

	public int getId() {
		return id;
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
		validateAmounts();
	}

	public boolean isEnabled() {
		return enabled;
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

		case ONE_PER_MODEL_EXEPT_ONE:
			max = limit - 1;
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

	public boolean canSelectMore(final UnitOption option) {
		if (!enabled) {
			return false;
		}
		if (type == GroupType.UP_TO_X_OF_EACH_PER_Y_MODELS) {
			return option.getAmountSelected() < getMaxAmount();
		}
		final int max = getMaxAmount();
		int current = 0;
		for (final UnitOption unitOption : options) {
			current += unitOption.getAmountSelected();
			if (current > max) {
				unitOption.setAmountSelected(unitOption.getAmountSelected() + max - current);
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
		super.writeToParcel(dest, flags);
		dest.writeInt(type.ordinal());
		dest.writeInt(optionNumberPerGroup);
		dest.writeInt(groupSize);
		dest.writeList(options);
		dest.writeInt(id);
		dest.writeTypedList(rules);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		type = GroupType.values()[source.readInt()];
		optionNumberPerGroup = source.readInt();
		groupSize = source.readInt();

		options = new ArrayList<UnitOption>();
		source.readList(options, UnitOption.class.getClassLoader());

		id = source.readInt();

		if (ruleCreator != null) {
			rules = new ArrayList<IRule>();
			source.readTypedList(rules, (Parcelable.Creator<IRule>) ruleCreator);
		}
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}
}
