package de.game_coding.armypicker.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class UnitOptionGroup extends Model {

	public enum GroupType {
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
		UP_TO_X_OF_EACH_PER_Y_MODELS,
		/**
		 * Can take each upgrade in this group X-times per unit
		 */
		UP_TO_X_OF_EACH_PER_UNIT,
		/**
		 * Can only be taken once for the entire unit and must always be taken
		 */
		X_PER_UNIT,
		/**
		 * Can only be taken X times for each model and must always be taken
		 */
		X_OF_EACH_PER_MODEL,
	}

	public enum ExpansionState {
		ALWAYS_EXPANDED, //
		EXPANDED, //
		COLLAPSED //
	}

	private GroupType type = GroupType.ONE_PER_MODEL;

	private int optionNumberPerGroup = 1;

	private int initialOptionNumberPerGroup = 1;

	private int groupSize = 1;

	private int reducer = 0;

	private int limit;

	private List<UnitOption> options = new ArrayList<UnitOption>();

	private List<IRule> rules = new ArrayList<IRule>();

	private int id;

	private Creator<?> ruleCreator;

	private boolean enabled = true;

	private ExpansionState expansion = ExpansionState.ALWAYS_EXPANDED;

	private final List<String> warnings = new ArrayList<String>();

	private String expansionTitle = "";

	public UnitOptionGroup(final int id, final GroupType type, final UnitOption... options) {
		this.id = id;
		this.type = type;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final GroupType type, final UnitOption... options) {
		this.type = type;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final int id, final GroupType type, final int optionNumberPerGroup, final int groupSize,
		final UnitOption... options) {
		this.id = id;
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.initialOptionNumberPerGroup = optionNumberPerGroup;
		this.groupSize = groupSize;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final int id, final GroupType type, final int optionNumberPerGroup,
		final UnitOption... options) {
		this.id = id;
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.initialOptionNumberPerGroup = optionNumberPerGroup;
		this.options = Arrays.asList(options);
	}

	public UnitOptionGroup(final GroupType type, final int optionNumberPerGroup, final int groupSize,
		final UnitOption... options) {
		this.type = type;
		this.optionNumberPerGroup = optionNumberPerGroup;
		this.initialOptionNumberPerGroup = optionNumberPerGroup;
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

	public void setOptionNumberPerGroup(final int optionNumberPerGroup) {
		if (type != GroupType.UP_TO_X_PER_Y_MODELS && type != GroupType.UP_TO_X_OF_EACH_PER_Y_MODELS
			&& type != GroupType.ONE_PER_MODEL && type != GroupType.X_OF_EACH_PER_MODEL) {
			if (this.optionNumberPerGroup != Math.max(0, optionNumberPerGroup)) {
				this.optionNumberPerGroup = Math.max(0, optionNumberPerGroup);
				validateAmounts();
			}
		}
	}

	public int getInitalOptionNumberPerGroup() {
		return initialOptionNumberPerGroup;
	}

	public int getGroupSize() {
		return groupSize;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(final int limit) {
		if (this.limit != Math.max(0, limit)) {
			this.limit = Math.max(0, limit);
			validateAmounts();
		}
	}

	public void validateAmounts() {
		final int max = enabled || type == GroupType.X_PER_UNIT ? getMaxAmount() : 0;
		int current = 0;
		for (final UnitOption option : options) {
			if (type == GroupType.ONE_PER_MODEL || type == GroupType.ONE_PER_MODEL_EXEPT_ONE) {
				option
					.setAmountSelected(option.getAmountSelected() > 0 || getOptions().size() == 1 ? max - reducer : 0);
			} else if (type == GroupType.UP_TO_X_OF_EACH_PER_Y_MODELS || type == GroupType.UP_TO_X_OF_EACH_PER_UNIT) {
				option.setAmountSelected(Math.min(option.getAmountSelected(), max));
				continue;
			} else if (type == GroupType.X_PER_UNIT) {
				option.setAmountSelected(
					option.getAmountSelected() == max - reducer || getOptions().size() == 1 ? max - reducer : 0);
			} else if (type == GroupType.X_OF_EACH_PER_MODEL) {
				option.setAmountSelected(max - reducer);
				continue;
			}
			if (option.getParentId() >= 0) {
				option.setAmountSelected(Math.min(option.getAmountSelected(), getOptionAmount(option.getParentId())));
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

	private UnitOption getOption(final int id) {
		for (final UnitOption option : options) {
			if (option.getId() == id) {
				return option;
			}
		}
		return null;
	}

	private int getOptionAmount(final int optionId) {
		final UnitOption option = getOption(optionId);
		if (option != null) {
			return option.getAmountSelected();
		}
		return 0;
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
		if (this.enabled != enabled) {
			this.enabled = enabled;
			for (final UnitOption unitOption : options) {
				if (unitOption.getAmountSelected() == 0 && unitOption.getDefaultAmount() > 0) {
					unitOption.setAmountSelected(unitOption.getDefaultAmount());
				}
			}
			validateAmounts();
		}
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setWarning(final String text) {
		if (!warnings.contains(text)) {
			warnings.add(text);
		}
	}

	public void clearWarning(final String text) {
		warnings.remove(text);
	}

	public List<String> getActiveWarnings() {
		return warnings;
	}

	public boolean isCollapsible() {
		return expansion != ExpansionState.ALWAYS_EXPANDED;
	}

	public boolean isCollapsed() {
		return expansion == ExpansionState.COLLAPSED;
	}

	public void collapse(final boolean collapsed) {
		expansion = collapsed ? ExpansionState.COLLAPSED : ExpansionState.EXPANDED;
	}

	public UnitOptionGroup makeExpandable(final String expansionTitle) {
		this.expansionTitle = expansionTitle;
		expansion = ExpansionState.COLLAPSED;
		return this;
	}

	public String getExpansionTitle() {
		return expansionTitle;
	}

	private int getMaxAmount() {
		int max = limit;
		switch (type) {
			case X_PER_UNIT:
				return optionNumberPerGroup;

			case X_OF_EACH_PER_MODEL:
				return limit * initialOptionNumberPerGroup;

			case ONE_PER_MODEL:
				max = limit;
				break;

			case ONE_PER_MODEL_EXEPT_ONE:
				max = limit - 1;
				break;

			case UP_TO_X_PER_UNIT:
			case UP_TO_X_OF_EACH_PER_UNIT:
				max = optionNumberPerGroup;
				break;

			default:
				max = optionNumberPerGroup * (limit / groupSize) - reducer;
				break;
		}
		return max;
	}

	public boolean canSelectMore(final UnitOption option) {
		final boolean hasMultipleOptions = (type != GroupType.ONE_PER_MODEL && type != GroupType.X_OF_EACH_PER_MODEL)
			|| options.size() > 1;
		if (!enabled || !hasMultipleOptions) {
			return false;
		}
		if (type == GroupType.UP_TO_X_OF_EACH_PER_Y_MODELS || type == GroupType.UP_TO_X_OF_EACH_PER_UNIT) {
			return option.getAmountSelected() < getMaxAmount();
		}
		final int max = getMaxAmount();
		int current = 0;
		if (option.getParentId() >= 0) {
			return option.getAmountSelected() < getOptionAmount(option.getParentId());
		}
		for (final UnitOption unitOption : options) {
			if (unitOption.getParentId() >= 0) {
				continue;
			}
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

	public int getAmountSelected() {
		int total = 0;
		for (final UnitOption option : options) {
			if (option.getParentId() < 0) {
				total += option.getAmountSelected();
			}
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
		dest.writeInt(initialOptionNumberPerGroup);
		dest.writeInt(groupSize);
		dest.writeString(expansionTitle);
		dest.writeInt(expansion.ordinal());
		writeList(dest, options);
		dest.writeInt(id);
		writeList(dest, rules);
	}

	@Override
	protected void readFromParcel(final Parcel source) {
		super.readFromParcel(source);
		if (getFileVersion() > getFeatureVersion()) {
			return;
		}
		type = GroupType.values()[source.readInt()];
		optionNumberPerGroup = source.readInt();
		initialOptionNumberPerGroup = source.readInt();
		groupSize = source.readInt();
		expansionTitle = source.readString();
		expansion = ExpansionState.values()[source.readInt()];

		options = readList(source, UnitOption.CREATOR);

		id = source.readInt();

		rules = readList(source, (Parcelable.Creator<IRule>) ruleCreator);
	}

	@Override
	protected int getFeatureVersion() {
		return 0;
	}

	@Override
	public String toString() {
		String name = new String();
		for (final UnitOption group : options) {
			name += group.toString() + ", ";
		}
		return name;
	}

	public void reduceBy(final int diff) {
		reducer = diff;
		if (type == GroupType.UP_TO_X_PER_Y_MODELS) {
			validateAmounts();
		}
	}
}
